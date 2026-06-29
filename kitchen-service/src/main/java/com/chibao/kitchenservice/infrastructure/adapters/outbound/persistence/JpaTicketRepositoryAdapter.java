package com.chibao.kitchenservice.infrastructure.adapters.outbound.persistence;

import com.chibao.kitchenservice.application.outbound.TicketRepository;
import com.chibao.kitchenservice.domain.model.Ticket;
import com.chibao.kitchenservice.infrastructure.adapters.outbound.persistence.entity.TicketEntity;
import com.chibao.kitchenservice.infrastructure.adapters.outbound.persistence.mapper.TicketEntityMapper;
import com.chibao.kitchenservice.infrastructure.adapters.outbound.persistence.repository.SpringDataTicketRepository;
import org.springframework.stereotype.Component;

@Component
public class JpaTicketRepositoryAdapter implements TicketRepository {
    private final SpringDataTicketRepository repository;

    public JpaTicketRepositoryAdapter(SpringDataTicketRepository repository) {
        this.repository = repository;
    }

    @Override
    public Ticket save(Ticket ticket) {
        TicketEntity entity = TicketEntityMapper.toEntity(ticket);
        TicketEntity saved = repository.save(entity);
        return TicketEntityMapper.toDomain(saved);
    }

    @Override
    public Ticket findById(String id) {
        return repository.findById(id)
                .map(TicketEntityMapper::toDomain)
                .orElse(null);
    }
}
