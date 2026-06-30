# FTGO Microservices: API Endpoints & Core System Flows

This document combines the service topology, API contracts, bootstrapping processes, registration sequences, and distributed transactional flows (Saga Orchestration) across the **`ftgo-microservices`** workspace.

---

## 1. What the Project Does

The application represents a reference architecture for food delivery ordering, restaurant ticketing, dynamic configuration, and discovery management:
1. **Centralized Configuration**: The `config-server` serves environment properties (connection ports, database URLs, Eureka zones) from a shared directory (`shared-config-repo`).
2. **Service Registry**: The `eurekaserver` acts as a coordinator, tracking host names/IP addresses and mapping logical microservice names.
3. **Dynamic Outbound Call Routing**: Microservices use OpenFeign to invoke target endpoints without hardcoding IP addresses or ports, dynamically querying Eureka to locate instances.
4. **Distributed Saga Transaction**: Orchestrates complex order placements across multiple services. If a middle step (e.g. payment authorization) fails, it executes compensating operations (e.g. ticket cancellation) to roll back downstream changes.

---

## 2. System Topology & Port Mapping

```mermaid
graph TD
    Client[REST Client / Client Browser]
    Eureka[Eureka Server : 8070]
    Config[Config Server : 8888]
    Order[Order Service : 8081]
    Kitchen[Kitchen Service : 8082]
    
    OrderDB[(PostgreSQL orderdb)]
    KitchenDB[(PostgreSQL kitchendb)]

    Client -->|POST /v1/orders| Order
    Order -->|Register / Lookup| Eureka
    Kitchen -->|Register / Lookup| Eureka
    Order -.->|Fetch Configs| Config
    Kitchen -.->|Fetch Configs| Config
    Order -->|Dynamic Feign Call| Kitchen
    
    Order --> OrderDB
    Kitchen --> KitchenDB
```

---

## 3. Inbound API Endpoints Reference

### 3.1 Order Service (`order-service` : Port `8081`)

Exposes REST endpoints to place and monitor customer checkouts:

1. **Create Order** (`POST /v1/orders`):
   * **Body Payload (`OrderCreateRequest`)**:
     ```json
     {
       "consumerId": "consumer_99",
       "restaurantId": "restaurant_01",
       "totalAmount": 34.50,
       "items": [
         { "menuItemId": "pizza_margherita", "quantity": 2 }
       ]
     }
     ```
   * **Workflow**: Saves order as `CREATED` locally. Requests kitchen validation from `kitchen-service` and calls payment verification. Upon confirmation, transitions state to `APPROVED`. If validation or payment fails, transitions to `REJECTED`.
   * **Response Payload (`OrderResponse`)**:
     ```json
     {
       "id": "4b9e28ac-1a3b-4cde-8e9f-524bc109f291",
       "status": "APPROVED",
       "totalAmount": 34.50
     }
     ```

2. **Get Order Details** (`GET /v1/orders/{orderId}`):
   * **Workflow**: Queries `orderdb` and returns order state.
   * **Response Payload (`OrderResponse`)**: Mapped to database state.

---

### 3.2 Kitchen Service (`kitchen-service` : Port `8082`)

Exposes REST endpoints to manage and retrieve food preparation tickets:

1. **Create Ticket** (`POST /v1/tickets`):
   * **Body Payload (`TicketCreateRequest`)**:
     ```json
     {
       "id": "4b9e28ac-1a3b-4cde-8e9f-524bc109f291",
       "orderId": "4b9e28ac-1a3b-4cde-8e9f-524bc109f291",
       "restaurantId": "restaurant_01"
     }
     ```
   * **Workflow**: Creates a food ticket, schedules target preparation time (+30 min), persists to `kitchendb`, and registers state as `CREATED`.
   * **Response Payload (`TicketResponse`)**:
     ```json
     {
       "id": "4b9e28ac-1a3b-4cde-8e9f-524bc109f291",
       "orderId": "4b9e28ac-1a3b-4cde-8e9f-524bc109f291",
       "state": "CREATED"
     }
     ```

2. **Get Ticket Details** (`GET /v1/tickets/{ticketId}`):
   * **Response Payload (`TicketResponse`)**: Mapped to database ticket state.

---

### 3.3 Config Server (`config-server` : Port `8888`)

Distributes centralized properties to microservices:

1. **Fetch Configuration Profile** (`GET /{application}/{profile}`):
   * **Workflow**: Config Server parses `shared-config-repo/` for properties matching `{application}-{profile}.yml` and returns a flat key-value payload.

---

### 3.4 Eureka Server (`eureka-server` : Port `8070`)

Hosts dashboard and API query registry endpoint:

1. **Dashboard Interface** (`GET /`): Access via browser to check registry dashboard.
2. **Registry Lookup** (`GET /eureka/apps`): Lists XML/JSON coordinates of all active registered clients.

---

## 4. End-to-End Core System Flows

### 4.1 Bootstrap Configuration Fetch Flow

During startup, microservice context initialization queries `config-server` before starting the database or web listener:

```mermaid
sequenceDiagram
    autonumber
    participant ClientService as order-service / kitchen-service
    participant ConfigServer as config-server (8888)
    participant Disk as shared-config-repo

    ClientService->>ClientService: Read local application.yaml
    Note over ClientService: Parse spring.config.import -> configserver:http://localhost:8888
    ClientService->>ConfigServer: GET /order-service/dev
    ConfigServer->>Disk: Search files (order-service-dev.yml)
    Disk-->>ConfigServer: Return raw properties
    Note over ConfigServer: Merge profiles & decrypt cipher text
    ConfigServer-->>ClientService: Return JSON PropertySources
    Note over ClientService: Inject datasource settings, start active context
```

---

### 4.2 Service Discovery Registration Flow

Active services register coordinates with Eureka and maintain connection status via heartbeats:

```mermaid
sequenceDiagram
    autonumber
    participant ClientService as order-service / kitchen-service
    participant Eureka as eureka-server (8070)

    ClientService->>Eureka: POST /eureka/apps/{appName} (Registry Handshake)
    Note over Eureka: Save active coordinates and status (UP)
    Eureka-->>ClientService: 204 No Content (Registry Success)
    
    loop Heartbeat Interval (Every 30 seconds)
        ClientService->>Eureka: PUT /eureka/apps/{appName}/{instanceId}
        Eureka-->>ClientService: 200 OK
    end
```

---

### 4.3 Saga Order Placement Orchestration

#### Flow A: Happy Path (Successful checkout)

```mermaid
sequenceDiagram
    autonumber
    actor Client
    participant Order as order-service (8081)
    participant Eureka as eureka-server (8070)
    participant Kitchen as kitchen-service (8082)
    participant Payment as Payment Gateway

    Client->>Order: POST /v1/orders
    Note over Order: Save local order (CREATED)
    
    Order->>Eureka: Lookup address of "kitchen-service"
    Eureka-->>Order: Return http://localhost:8082
    
    Order->>Kitchen: Feign POST: /v1/tickets
    Note over Kitchen: Save local ticket (CREATED)
    Kitchen-->>Order: Return TicketResponse (CREATED)
    
    Order->>Payment: Authorize transaction amount
    Payment-->>Order: Payment Authorized (Success)
    
    Note over Order: Update order status to APPROVED
    Order-->>Client: Return OrderResponse (APPROVED)
```

---

#### Flow B: Payment Authorization Failure (Compensation Rollback)

```mermaid
sequenceDiagram
    autonumber
    actor Client
    participant Order as order-service (8081)
    participant Kitchen as kitchen-service (8082)
    participant Payment as Payment Gateway

    Client->>Order: POST /v1/orders
    Note over Order: Save local order (CREATED)
    
    Order->>Kitchen: Feign POST: /v1/tickets (via Eureka lookup)
    Note over Kitchen: Save local ticket (CREATED)
    Kitchen-->>Order: Return TicketResponse
    
    Order->>Payment: Authorize transaction amount
    Payment-->>Order: Payment Declined (Failure)
    
    Note over Order: Start Rollback: Update order to REJECTED
    Order->>Kitchen: Feign POST: /v1/tickets (Compensating action: rejectTicket)
    Note over Kitchen: Cancel/Reject ticket details
    Order-->>Client: Return OrderResponse (REJECTED)
```

---

#### Flow C: Network Outage Exception Handling

If `kitchen-service` is unreachable or times out during the Feign request:

```mermaid
sequenceDiagram
    autonumber
    actor Client
    participant Order as order-service (8081)
    participant Kitchen as kitchen-service (8082)

    Client->>Order: POST /v1/orders
    Note over Order: Save local order (CREATED)
    
    Order->>Kitchen: Feign POST: /v1/tickets
    Note over Kitchen: Network Timeout / Exception
    Kitchen--XOrder: Connection Error / Down
    
    Note over Order: Exception Caught: Update order to REJECTED
    Order->>Kitchen: Try compensating action: rejectTicket
    Order-->>Client: Return OrderResponse (REJECTED)
```
