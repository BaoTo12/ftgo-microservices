package com.chibao.kitchenservice.infrastructure.adapters.outbound.persistence.entity;

import com.chibao.kitchenservice.domain.model.TicketState;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Setter
@Getter
@Entity
@Table(name = "tickets")
public class TicketEntity {
    @Id
    private String id;

    @Column(name = "order_id", unique = true)
    private String orderId;

    @Column(name = "restaurant_id")
    private String restaurantId;

    @Enumerated(EnumType.STRING)
    @Column(name = "preparation_status")
    private TicketState preparationStatus;

    @Column(name = "estimated_ready_time")
    private LocalDateTime estimatedReadyTime;

    @Version
    private Long version;

    public TicketEntity() {}

}
