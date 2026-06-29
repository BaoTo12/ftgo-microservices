package com.chibao.orderservice.infrastructure.adapters.outbound.clients;


import com.chibao.orderservice.application.ports.outbound.KitchenClient;
import org.springframework.stereotype.Component;

@Component
public class KitchenClientAdapter implements KitchenClient {

    @Override
    public boolean createTicket(String orderId, String restaurantId) {
        System.out.println("Hexagonal Infrastructure Adapter: creating ticket for order " + orderId);
        return true;
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

