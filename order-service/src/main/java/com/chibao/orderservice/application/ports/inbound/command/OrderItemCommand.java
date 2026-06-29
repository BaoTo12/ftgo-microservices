package com.chibao.orderservice.application.ports.inbound.command;

import lombok.Getter;

import java.math.BigDecimal;

@Getter
public class OrderItemCommand {
    private final String menuItemId;
    private final String name;
    private final BigDecimal price;
    private final int quantity;

    public OrderItemCommand(String menuItemId, String name, BigDecimal price, int quantity) {
        this.menuItemId = menuItemId;
        this.name = name;
        this.price = price;
        this.quantity = quantity;
    }

}

