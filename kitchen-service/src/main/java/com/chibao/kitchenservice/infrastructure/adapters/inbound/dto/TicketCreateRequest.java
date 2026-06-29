package com.chibao.kitchenservice.infrastructure.adapters.inbound.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class TicketCreateRequest {

    @NotBlank(message = "Ticket ID is required")
    private String id;

    @NotBlank(message = "Order ID is required")
    private String orderId;

    @NotBlank(message = "Restaurant ID is required")
    private String restaurantId;

}
