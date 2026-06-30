package com.chibao.orderservice.application.mapper;

import com.chibao.orderservice.application.ports.inbound.command.CreateOrderCommand;
import com.chibao.orderservice.application.ports.inbound.result.OrderResult;
import com.chibao.orderservice.domain.model.Order;
import com.chibao.orderservice.domain.model.OrderItem;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class OrderMapper {
    public static List<OrderItem> toDomainItems(CreateOrderCommand command) {
        if (command.getItems() == null) return Collections.emptyList();
        return command.getItems().stream()
                .map(item -> new OrderItem(item.getMenuItemId(), item.getName(), item.getPrice(), item.getQuantity()))
                .collect(Collectors.toList());
    }

    public static OrderResult toResult(Order order) {
        if (order == null) return null;
        return new OrderResult(order.getId(), order.getState().name(), order.getTotalAmount());
    }
}