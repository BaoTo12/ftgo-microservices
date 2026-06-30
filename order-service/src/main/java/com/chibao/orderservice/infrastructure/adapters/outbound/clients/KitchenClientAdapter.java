package com.chibao.orderservice.infrastructure.adapters.outbound.clients;

import com.chibao.orderservice.application.ports.outbound.KitchenClient;
import io.github.resilience4j.bulkhead.annotation.Bulkhead;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class KitchenClientAdapter implements KitchenClient {

    private final KitchenFeignClient kitchenFeignClient;

    @Override
    @Retry(name = "kitchenRetry")
    @Bulkhead(name = "kitchenBulkhead", type = Bulkhead.Type.THREADPOOL)
    @CircuitBreaker(name = "kitchenCircuitBreaker", fallbackMethod = "createTicketFallback")
    public boolean createTicket(String orderId, String restaurantId) {
        System.out
                .println("Hexagonal Infrastructure Adapter: creating ticket for order " + orderId + " via FeignClient");
        TicketCreateRequest request = new TicketCreateRequest(orderId, orderId, restaurantId);
        kitchenFeignClient.createTicket(request);
        return true;
    }

    public boolean createTicketFallback(String orderId, String restaurantId, Throwable throwable) {
        System.err.println("Circuit Breaker kích hoạt Fallback! Lý do: " + throwable.getMessage());
        // Trả về true để cho phép quy trình tạo đơn hàng tiếp tục ở chế độ fallback ngoại tuyến (được đồng bộ hóa sau)
        return true;
    }

    @Override
    @Retry(name = "kitchenCompensationRetry")
    public void confirmTicket(String orderId) {
        System.out.println("Hexagonal Infrastructure Adapter: confirming ticket for order " + orderId);
    }

    @Override
    @Retry(name = "kitchenCompensationRetry")
    public void rejectTicket(String orderId) {
        System.out.println("Hexagonal Infrastructure Adapter: rejecting ticket for order " + orderId);
    }
}
