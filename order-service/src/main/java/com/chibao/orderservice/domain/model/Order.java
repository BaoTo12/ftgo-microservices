package com.chibao.orderservice.domain.model;


import lombok.Getter;

import java.math.BigDecimal;
import java.util.List;

@Getter
public class Order {
    private final String id;
    private final String consumerId;
    private final String restaurantId;
    private final BigDecimal totalAmount;
    private OrderState state;
    private Long version;
    private final List<OrderItem> lineItems;

    public Order(String id, String consumerId, String restaurantId, BigDecimal totalAmount, List<OrderItem> lineItems) {
        this.id = id;
        this.consumerId = consumerId;
        this.restaurantId = restaurantId;
        this.totalAmount = totalAmount;
        this.lineItems = lineItems;
        this.state = OrderState.APPROVAL_PENDING;
    }

    public Order(String id, String consumerId, String restaurantId, BigDecimal totalAmount, List<OrderItem> lineItems, OrderState state, Long version) {
        this.id = id;
        this.consumerId = consumerId;
        this.restaurantId = restaurantId;
        this.totalAmount = totalAmount;
        this.lineItems = lineItems;
        this.state = state;
        this.version = version;
    }

    public void approve() {
        if (this.state != OrderState.APPROVAL_PENDING) {
            throw new IllegalStateException("Cannot approve order in state: " + this.state);
        }
        this.state = OrderState.APPROVED;
    }

    public void reject() {
        if (this.state != OrderState.APPROVAL_PENDING) {
            throw new IllegalStateException("Cannot reject order in state: " + this.state);
        }
        this.state = OrderState.REJECTED;
    }

    public void cancel() {
        if (this.state != OrderState.APPROVED) {
            throw new IllegalStateException("Cannot cancel order in state: " + this.state);
        }
        this.state = OrderState.CANCELLED;
    }

}

