package com.chibao.kitchenservice.infrastructure.adapters.outbound.persistence.mapper;

import com.chibao.kitchenservice.domain.model.Ticket;
import com.chibao.kitchenservice.infrastructure.adapters.outbound.persistence.entity.TicketEntity;

public class TicketEntityMapper {
    public static TicketEntity toEntity(Ticket ticket) {
        if (ticket == null) return null;
        TicketEntity entity = new TicketEntity();
        entity.setId(ticket.getId());
        entity.setOrderId(ticket.getOrderId());
        entity.setRestaurantId(ticket.getRestaurantId());
        entity.setPreparationStatus(ticket.getPreparationStatus());
        entity.setEstimatedReadyTime(ticket.getEstimatedReadyTime());
        entity.setVersion(ticket.getVersion());
        return entity;
    }

    public static Ticket toDomain(TicketEntity entity) {
        if (entity == null) return null;
        return new Ticket(
                entity.getId(),
                entity.getOrderId(),
                entity.getRestaurantId(),
                entity.getPreparationStatus(),
                entity.getEstimatedReadyTime(),
                entity.getVersion()
        );
    }
}
