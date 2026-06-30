package com.chibao.orderservice.infrastructure.adapters.outbound.clients;

import com.chibao.orderservice.application.ports.outbound.KitchenClient;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class KitchenClientAdapter implements KitchenClient {

    private final KitchenFeignClient kitchenFeignClient;

    @Override
    @CircuitBreaker(name = "kitchenAdapterBreaker", fallbackMethod = "createTicketFallback")
    public boolean createTicket(String orderId, String restaurantId) {
        System.out
                .println("Hexagonal Infrastructure Adapter: creating ticket for order " + orderId + " via FeignClient");
        TicketCreateRequest request = new TicketCreateRequest(orderId, orderId, restaurantId);
        kitchenFeignClient.createTicket(request);
        return true;
    }

    /**
     * HÀM FALLBACK CƠ HỌC: Được kích hoạt tự động trong 2 trường hợp:
     */
    public boolean createTicketFallback(String orderId, String restaurantId, Throwable throwable) {
        System.err.println("Circuit Breaker kích hoạt Fallback! Lý do: " + throwable.getMessage());
        // Trả về false về cho Domain Core xử lý logic hủy đơn hàng với trạng thái REJECTED
        return false;
    }

    @Override
    public void confirmTicket(String orderId) {
        System.out.println("Hexagonal Infrastructure Adapter: confirming ticket for order " + orderId);
    }

    @Override
    public void rejectTicket(String orderId) {
        System.out.println("Hexagonal Infrastructure Adapter: rejecting ticket for order " + orderId);
    }
}
