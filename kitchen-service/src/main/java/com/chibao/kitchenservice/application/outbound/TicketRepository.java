package com.chibao.kitchenservice.application.outbound;

import com.chibao.kitchenservice.domain.model.Ticket;

public interface TicketRepository {
    Ticket save(Ticket ticket);
    Ticket findById(String id);
}
