package com.chibao.orderservice.application.ports.outbound;

import com.chibao.orderservice.domain.model.Order;

public interface OrderRepository {
    Order save(Order order);
    Order findById(String id);
}
