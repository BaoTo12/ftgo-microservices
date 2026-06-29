package com.chibao.orderservice.infrastructure.adapters.outbound.clients;

import com.chibao.orderservice.application.ports.outbound.KitchenClient;
import org.springframework.stereotype.Component;

@Component
public class KitchenClientAdapter implements KitchenClient {

    private final KitchenFeignClient kitchenFeignClient;

    public KitchenClientAdapter(KitchenFeignClient kitchenFeignClient) {
        this.kitchenFeignClient = kitchenFeignClient;
    }

    @Override
    public boolean createTicket(String orderId, String restaurantId) {
        System.out.println("Hexagonal Infrastructure Adapter: creating ticket for order " + orderId + " via FeignClient");
        try {
            TicketCreateRequest request = new TicketCreateRequest(orderId, orderId, restaurantId);
            kitchenFeignClient.createTicket(request);
            return true;
        } catch (Exception e) {
            System.err.println("Error calling kitchen-service to create ticket: " + e.getMessage());
            return false;
        }
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
