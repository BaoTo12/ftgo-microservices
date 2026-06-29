package com.chibao.kitchenservice.application.service;

import com.chibao.kitchenservice.application.inbound.TicketManagementUseCase;
import com.chibao.kitchenservice.application.inbound.command.CreateTicketCommand;
import com.chibao.kitchenservice.application.inbound.result.TicketResult;
import com.chibao.kitchenservice.application.mapper.TicketMapper;
import com.chibao.kitchenservice.application.outbound.TicketRepository;
import com.chibao.kitchenservice.domain.model.Ticket;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class TicketManagementService implements TicketManagementUseCase {
    private final TicketRepository repository;


    @Override
    public TicketResult createTicket(CreateTicketCommand command) {
        Ticket ticket = new Ticket(command.getId(), command.getOrderId(), command.getRestaurantId(), LocalDateTime.now().plusMinutes(30));
        Ticket saved = repository.save(ticket);
        return TicketMapper.toResult(saved);
    }

    @Override
    public TicketResult getTicket(String id) {
        Ticket ticket = repository.findById(id);
        return TicketMapper.toResult(ticket);
    }
}
