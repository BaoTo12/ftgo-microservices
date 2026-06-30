package com.chibao.orderservice.domain.model;

import lombok.Getter;

import java.math.BigDecimal;

@Getter
public class OrderItem {
    private final String menuItemId;
    private final String name;
    private final BigDecimal price;
    private final int quantity;

    public OrderItem(String menuItemId, String name, BigDecimal price, int quantity) {
        this.menuItemId = menuItemId;
        this.name = name;
        this.price = price;
        this.quantity = quantity;
    }

}
