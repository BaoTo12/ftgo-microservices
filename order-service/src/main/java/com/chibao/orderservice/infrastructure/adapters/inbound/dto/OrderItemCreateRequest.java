package com.chibao.orderservice.infrastructure.adapters.inbound.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class OrderItemCreateRequest {
    @NotBlank(message = "Menu item ID is required")
    private String menuItemId;

    @NotBlank(message = "Item name is required")
    private String name;

    @NotNull(message = "Item price is required")
    @Positive(message = "Item price must be positive")
    private BigDecimal price;

    @Min(value = 1, message = "Quantity must be at least 1")
    private int quantity;
}
