package com.chibao.kitchenservice.infrastructure.adapters.outbound.persistence.repository;

import com.chibao.kitchenservice.infrastructure.adapters.outbound.persistence.entity.TicketEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SpringDataTicketRepository extends JpaRepository<TicketEntity, String> {
}

