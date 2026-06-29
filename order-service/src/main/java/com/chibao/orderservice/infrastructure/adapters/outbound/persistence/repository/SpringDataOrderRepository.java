package com.chibao.orderservice.infrastructure.adapters.outbound.persistence.repository;

import com.chibao.orderservice.infrastructure.adapters.outbound.persistence.entity.OrderEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SpringDataOrderRepository extends JpaRepository<OrderEntity, String> {
}

