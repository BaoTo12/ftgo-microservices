package com.chibao.kitchenservice.infrastructure.adapters.inbound.dto;

import lombok.Getter;

@Getter
public class TicketResponse {
    private String id;
    private String orderId;
    private String status;

    public TicketResponse(String id, String orderId, String status) {
        this.id = id;
        this.orderId = orderId;
        this.status = status;
    }
}

