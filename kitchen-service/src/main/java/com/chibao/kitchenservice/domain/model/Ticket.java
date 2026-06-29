package com.chibao.kitchenservice.domain.model;

import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class Ticket {
    private final String id;
    private final String orderId;
    private final String restaurantId;
    private TicketState preparationStatus;
    private final LocalDateTime estimatedReadyTime;
    private Long version;

    public Ticket(String id, String orderId, String restaurantId, LocalDateTime estimatedReadyTime) {
        this.id = id;
        this.orderId = orderId;
        this.restaurantId = restaurantId;
        this.estimatedReadyTime = estimatedReadyTime;
        this.preparationStatus = TicketState.CREATED;
    }

    public Ticket(String id, String orderId, String restaurantId, TicketState preparationStatus, LocalDateTime estimatedReadyTime, Long version) {
        this.id = id;
        this.orderId = orderId;
        this.restaurantId = restaurantId;
        this.preparationStatus = preparationStatus;
        this.estimatedReadyTime = estimatedReadyTime;
        this.version = version;
    }

    public void accept() {
        if (this.preparationStatus != TicketState.CREATED) {
            throw new IllegalStateException("Ticket already accepted or completed: " + this.preparationStatus);
        }
        this.preparationStatus = TicketState.PREPARING;
    }

    public void readyForPickup() {
        if (this.preparationStatus != TicketState.PREPARING) {
            throw new IllegalStateException("Ticket must be preparing before ready for pickup!");
        }
        this.preparationStatus = TicketState.READY_FOR_PICKUP;
    }

    public void release() {
        if (this.preparationStatus != TicketState.READY_FOR_PICKUP) {
            throw new IllegalStateException("Ticket must be ready for pickup before released!");
        }
        this.preparationStatus = TicketState.RELEASED;
    }

    public void setPreparationStatus(TicketState status) { this.preparationStatus = status; }
}
