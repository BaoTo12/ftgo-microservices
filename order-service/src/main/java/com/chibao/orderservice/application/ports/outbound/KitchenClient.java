package com.chibao.orderservice.application.ports.outbound;

public interface KitchenClient {
    boolean createTicket(String orderId, String restaurantId);
    void confirmTicket(String orderId);
    void rejectTicket(String orderId);
}
