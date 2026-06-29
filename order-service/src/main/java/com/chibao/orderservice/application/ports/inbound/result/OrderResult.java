package com.chibao.orderservice.application.ports.inbound.result;

import lombok.Getter;

import java.math.BigDecimal;

@Getter
public class OrderResult {
    private final String id;
    private final String state;
    private final BigDecimal totalAmount;

    public OrderResult(String id, String state, BigDecimal totalAmount) {
        this.id = id;
        this.state = state;
        this.totalAmount = totalAmount;
    }
}
