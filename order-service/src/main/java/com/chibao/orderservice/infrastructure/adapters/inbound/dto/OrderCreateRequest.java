package com.chibao.orderservice.infrastructure.adapters.inbound.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

@Setter
@Getter
public class OrderCreateRequest {

    @NotBlank(message = "Consumer ID is required")
    private String consumerId;

    @NotBlank(message = "Restaurant ID is required")
    private String restaurantId;

    @NotNull(message = "Total amount is required")
    @Positive(message = "Total amount must be greater than zero")
    private BigDecimal totalAmount;

    @NotEmpty(message = "Order must contain at least one item")
    @Valid
    private List<OrderItemCreateRequest> items;

}

