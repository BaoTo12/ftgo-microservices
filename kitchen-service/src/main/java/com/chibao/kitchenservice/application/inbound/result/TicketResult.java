package com.chibao.kitchenservice.application.inbound.result;

import lombok.Getter;

@Getter
public class TicketResult {
    private final String id;
    private final String orderId;
    private final String status;

    public TicketResult(String id, String orderId, String status) {
        this.id = id;
        this.orderId = orderId;
        this.status = status;
    }
}

