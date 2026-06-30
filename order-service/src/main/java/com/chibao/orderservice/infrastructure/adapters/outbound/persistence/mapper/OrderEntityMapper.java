package com.chibao.orderservice.infrastructure.adapters.outbound.persistence.mapper;

import com.chibao.orderservice.domain.model.Order;
import com.chibao.orderservice.domain.model.OrderItem;
import com.chibao.orderservice.infrastructure.adapters.outbound.persistence.entity.OrderEntity;
import com.chibao.orderservice.infrastructure.adapters.outbound.persistence.entity.OrderLineItemEntity;

import java.util.List;
import java.util.stream.Collectors;

public class OrderEntityMapper {
    public static OrderEntity toEntity(Order order) {
        if (order == null) return null;
        OrderEntity entity = new OrderEntity();
        entity.setId(order.getId());
        entity.setConsumerId(order.getConsumerId());
        entity.setRestaurantId(order.getRestaurantId());
        entity.setTotalAmount(order.getTotalAmount());
        entity.setState(order.getState());
        entity.setVersion(order.getVersion());
        if (order.getLineItems() != null) {
            entity.setLineItems(order.getLineItems().stream()
                    .map(item -> new OrderLineItemEntity(item.getMenuItemId(), item.getName(), item.getPrice(), item.getQuantity()))
                    .collect(Collectors.toList()));
        }
        return entity;
    }

    public static Order toDomain(OrderEntity entity) {
        if (entity == null) return null;
        List<OrderItem> items = entity.getLineItems().stream()
                .map(item -> new OrderItem(item.getMenuItemId(), item.getName(), item.getPrice(), item.getQuantity()))
                .collect(Collectors.toList());
        return new Order(
                entity.getId(),
                entity.getConsumerId(),
                entity.getRestaurantId(),
                entity.getTotalAmount(),
                items,
                entity.getState(),
                entity.getVersion()
        );
    }
}