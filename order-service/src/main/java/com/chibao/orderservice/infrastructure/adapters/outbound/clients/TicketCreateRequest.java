package com.chibao.orderservice.infrastructure.adapters.outbound.clients;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TicketCreateRequest {
    private String id;
    private String orderId;
    private String restaurantId;
}
