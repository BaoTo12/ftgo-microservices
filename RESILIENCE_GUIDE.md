# Architecting Resilience: Resilience4j Design Guide for FTGO Microservices

Distributed architectures are vulnerable to cascading failures. If a single microservice runs slowly or goes offline, calling services can quickly exhaust their thread pools waiting for responses, bringing down the entire application. 

This guide details how to apply **Resilience4j** patterns inside the **`ftgo-microservices`** workspace to protect service boundaries, limit resource utilization, and degrade gracefully.

---

## 1. Resilience Patterns Matrix

| Pattern | Goal | Where to Apply (Target Class) | Why There? |
| :--- | :--- | :--- | :--- |
| **Circuit Breaker** | Stops invoking a downstream dependency once error thresholds are exceeded. | [KitchenClientAdapter](file:///C:/Users/Admin/Desktop/projects/ftgo-microservices/order-service/src/main/java/com/chibao/orderservice/infrastructure/adapters/outbound/clients/KitchenClientAdapter.java) & [PaymentClientAdapter](file:///C:/Users/Admin/Desktop/projects/ftgo-microservices/order-service/src/main/java/com/chibao/orderservice/infrastructure/adapters/outbound/clients/PaymentClientAdapter.java) | Prevents caller threads from block-waiting on dead services and failing cascadingly. |
| **Retry** | Re-issues failed requests automatically under the assumption of transient faults. | [PaymentClientAdapter](file:///C:/Users/Admin/Desktop/projects/ftgo-microservices/order-service/src/main/java/com/chibao/orderservice/infrastructure/adapters/outbound/clients/PaymentClientAdapter.java) | Handles network blips or short-lived database timeouts during payment handshakes. |
| **Bulkhead** | Restricts concurrent execution resources allocated to a specific call. | [KitchenClientAdapter](file:///C:/Users/Admin/Desktop/projects/ftgo-microservices/order-service/src/main/java/com/chibao/orderservice/infrastructure/adapters/outbound/clients/KitchenClientAdapter.java) | Isolates failures; a slow `kitchen-service` cannot consume all Tomcat request threads. |
| **Rate Limiter** | Restricts the frequency of calls a service accepts. | [OrderRestController](file:///C:/Users/Admin/Desktop/projects/ftgo-microservices/order-service/src/main/java/com/chibao/orderservice/infrastructure/adapters/inbound/controller/OrderRestController.java) | Protects the system from excessive load or Denial of Service (DoS) attempts at api entrypoints. |
| **TimeLimiter** | Enforces a hard execution time limit on asynchronous operations. | Outbound Async Client Adapters | Halts calls that take too long, protecting resources from slow network sockets. |
| **Fallback** | Provides an alternative code execution path when a failure occurs. | [KitchenClientAdapter](file:///C:/Users/Admin/Desktop/projects/ftgo-microservices/order-service/src/main/java/com/chibao/orderservice/infrastructure/adapters/outbound/clients/KitchenClientAdapter.java) | Permits degraded but operational processing (e.g. queuing tickets locally). |

---

## 2. Deep Dive: Architectural Rationale & Code Explanation

### 2.1 Circuit Breaker & Fallback

#### Where to Apply:
Outbound adapters making remote HTTP calls, specifically [KitchenClientAdapter.java](file:///C:/Users/Admin/Desktop/projects/ftgo-microservices/order-service/src/main/java/com/chibao/orderservice/infrastructure/adapters/outbound/clients/KitchenClientAdapter.java).

#### Why Apply There:
During order creation, `order-service` must communicate with `kitchen-service`. If `kitchen-service` is down, the Circuit Breaker trips immediately (transitioning from `CLOSED` to `OPEN`), rejecting subsequent calls without hitting the network. The **Fallback** mechanism catches this exception and returns a dummy ticket state, letting the Order creation finish in a `PENDING` state rather than throwing a raw 500 error to the customer.

#### Code Explanation:
```java
@Component
public class KitchenClientAdapter implements KitchenClient {

    private final KitchenFeignClient kitchenFeignClient;

    public KitchenClientAdapter(KitchenFeignClient kitchenFeignClient) {
        this.kitchenFeignClient = kitchenFeignClient;
    }

    @Override
    @CircuitBreaker(name = "kitchenService", fallbackMethod = "createTicketFallback")
    public boolean createTicket(String orderId, String restaurantId) {
        // Active HTTP network call via Feign Client
        TicketCreateRequest request = new TicketCreateRequest(orderId, orderId, restaurantId);
        kitchenFeignClient.createTicket(request);
        return true;
    }

    // Fallback method MUST have the same signature as the target method, plus a Throwable parameter
    public boolean createTicketFallback(String orderId, String restaurantId, Throwable t) {
        System.err.println("Circuit Breaker Tripped! Fallback executed. Reason: " + t.getMessage());
        
        // Graceful degradation: return true to let order placement proceed, 
        // and register a background job/outbox to sync ticket creation later.
        return true; 
    }
}
```

---

### 2.2 Retry

#### Where to Apply:
Outbound network adapters that call external systems like [PaymentClientAdapter.java](file:///C:/Users/Admin/Desktop/projects/ftgo-microservices/order-service/src/main/java/com/chibao/orderservice/infrastructure/adapters/outbound/clients/PaymentClientAdapter.java).

#### Why Apply There:
External payment processors might periodically reject socket connections or experience brief network lag. If the call fails, retrying it 2 or 3 times with a short delay (exponential backoff) often succeeds, making the service resilient without requiring user re-submission.

#### Code Explanation:
```java
@Component
public class PaymentClientAdapter implements PaymentClient {

    @Override
    @Retry(name = "paymentService", fallbackMethod = "authorizePaymentFallback")
    public boolean authorizePayment(String consumerId, double amount) {
        System.out.println("Processing payment for consumer: " + consumerId);
        // External network call logic...
        return true;
    }

    public boolean authorizePaymentFallback(String consumerId, double amount, Throwable t) {
        System.err.println("All payment retries failed. Declining transaction.");
        return false;
    }
}
```

##### Accompanying YAML Configuration (served via Config Server):
```yaml
resilience4j:
  retry:
    instances:
      paymentService:
        maxAttempts: 3            # Total attempts (1 initial + 2 retries)
        waitDuration: 1000ms       # Delay between attempts
        enableExponentialBackoff: true
        exponentialBackoffMultiplier: 2
```

---

### 2.3 Bulkhead

#### Where to Apply:
Service calls that are resource-heavy or depend on unreliable dependencies, such as the `KitchenClientAdapter` calls.

#### Why Apply There:
If `kitchen-service` slows down, requests start piling up in the Tomcat pool. A Bulkhead acts as a firewall by restricting the maximum number of concurrent threads allowed to execute `KitchenClientAdapter` methods. Even if all kitchen threads are saturated, the remaining threads in `order-service` are free to serve other API endpoints like order lookups or cancellations.

#### Code Explanation (Semaphore-based):
```java
@Component
public class KitchenClientAdapter implements KitchenClient {

    @Override
    @Bulkhead(name = "kitchenBulkhead", type = Bulkhead.Type.SEMAPHORE)
    public boolean createTicket(String orderId, String restaurantId) {
        // Limited concurrent threads allowed here
        return true;
    }
}
```

##### Accompanying YAML Configuration:
```yaml
resilience4j:
  bulkhead:
    instances:
      kitchenBulkhead:
        maxConcurrentCalls: 10     # Max threads allowed in this method concurrently
        maxWaitDuration: 500ms     # Time to wait for a free semaphore slot before failing
```

---

### 2.4 Rate Limiter

#### Where to Apply:
Inbound Controller classes, such as the [OrderRestController.java](file:///C:/Users/Admin/Desktop/projects/ftgo-microservices/order-service/src/main/java/com/chibao/orderservice/infrastructure/adapters/inbound/controller/OrderRestController.java).

#### Why Apply There:
Protects entry endpoints from API abuse, brute-force spamming, or server overload by limiting the number of calls allowed in a given time window.

#### Code Explanation:
```java
@RestController
@RequestMapping("/v1/orders")
public class OrderRestController {

    @PostMapping
    @RateLimiter(name = "orderCreationLimit")
    public OrderResponse createOrder(@Valid @RequestBody OrderCreateRequest request) {
        // Enforces rate limits on incoming POST request
        return orderService.create(request);
    }
}
```

##### Accompanying YAML Configuration:
```yaml
resilience4j:
  ratelimiter:
    instances:
      orderCreationLimit:
        limitForPeriod: 50          # Limit to 50 requests
        limitRefreshPeriod: 1s      # Reset request bucket every second
        timeoutDuration: 0ms        # Reject immediately if limit is exceeded
```

---

### 2.5 TimeLimiter

#### Where to Apply:
Asynchronous/Reactive network boundaries where hanging connections must be forcibly killed.

#### Why Apply There:
Unlike simple timeouts, Resilience4j's `TimeLimiter` restricts the duration of executing a call that returns a `CompletableFuture` or reactive publisher (`Mono` / `Flux`), ensuring resources are freed if the downstream call hangs on TCP handshake stages.

#### Code Explanation:
```java
@Component
public class KitchenClientAdapter implements KitchenClient {

    @Override
    @TimeLimiter(name = "kitchenTimeout")
    public CompletableFuture<Boolean> createTicketAsync(String orderId, String restaurantId) {
        return CompletableFuture.supplyAsync(() -> {
            // Long running network query
            return true;
        });
    }
}
```

##### Accompanying YAML Configuration:
```yaml
resilience4j:
  timelimiter:
    instances:
      kitchenTimeout:
        timeoutDuration: 2s         # Force terminate task if it takes more than 2 seconds
        cancelRunningFuture: true   # Terminate the underlying thread pool task
```
