package com.chibao.orderservice.application.ports.inbound.command;

import lombok.Getter;

import java.math.BigDecimal;
import java.util.List;

@Getter
public class CreateOrderCommand {
    private final String consumerId;
    private final String restaurantId;
    private final BigDecimal totalAmount;
    private final List<OrderItemCommand> items;

    public CreateOrderCommand(String consumerId, String restaurantId, BigDecimal totalAmount, List<OrderItemCommand> items) {
        this.consumerId = consumerId;
        this.restaurantId = restaurantId;
        this.totalAmount = totalAmount;
        this.items = items;
    }

}

