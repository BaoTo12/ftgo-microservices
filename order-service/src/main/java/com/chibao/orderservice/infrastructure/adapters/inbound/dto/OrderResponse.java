package com.chibao.orderservice.infrastructure.adapters.inbound.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;

@Getter
@RequiredArgsConstructor
public class OrderResponse {
    private final String id;
    private final String state;
    private final BigDecimal totalAmount;

}
