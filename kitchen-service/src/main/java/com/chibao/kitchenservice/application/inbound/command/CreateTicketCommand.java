package com.chibao.kitchenservice.application.inbound.command;


import lombok.Getter;

@Getter
public class CreateTicketCommand {
    private final String id;
    private final String orderId;
    private final String restaurantId;

    public CreateTicketCommand(String id, String orderId, String restaurantId) {
        this.id = id;
        this.orderId = orderId;
        this.restaurantId = restaurantId;
    }

}
