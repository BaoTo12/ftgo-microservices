package com.chibao.orderservice.infrastructure.adapters.inbound.mapper;



import com.chibao.orderservice.application.ports.inbound.command.CreateOrderCommand;
import com.chibao.orderservice.application.ports.inbound.command.OrderItemCommand;
import com.chibao.orderservice.application.ports.inbound.result.OrderResult;
import com.chibao.orderservice.infrastructure.adapters.inbound.dto.OrderCreateRequest;
import com.chibao.orderservice.infrastructure.adapters.inbound.dto.OrderResponse;

import java.util.Collections;
import java.util.stream.Collectors;


public class OrderControllerMapper {
    public static CreateOrderCommand toCommand(OrderCreateRequest dto) {
        if (dto == null) return null;
        return new CreateOrderCommand(
                dto.getConsumerId(),
                dto.getRestaurantId(),
                dto.getTotalAmount(),
                dto.getItems() == null ? Collections.emptyList() : dto.getItems().stream()
                                                                   .map(item -> new OrderItemCommand(item.getMenuItemId(), item.getName(), item.getPrice(), item.getQuantity()))
                                                                   .collect(Collectors.toList())
        );
    }

    public static OrderResponse toResponse(OrderResult result) {
        if (result == null) return null;
        return new OrderResponse(result.getId(), result.getState(), result.getTotalAmount());
    }
}
