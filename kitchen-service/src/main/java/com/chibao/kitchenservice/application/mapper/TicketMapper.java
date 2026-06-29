package com.chibao.kitchenservice.application.mapper;

import com.chibao.kitchenservice.application.inbound.result.TicketResult;
import com.chibao.kitchenservice.domain.model.Ticket;

public class TicketMapper {
    public static TicketResult toResult(Ticket ticket) {
        if (ticket == null) return null;
        return new TicketResult(ticket.getId(), ticket.getOrderId(), ticket.getPreparationStatus().name());
    }
}

