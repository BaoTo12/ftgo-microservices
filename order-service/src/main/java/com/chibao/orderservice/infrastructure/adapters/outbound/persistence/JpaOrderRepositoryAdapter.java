package com.chibao.orderservice.infrastructure.adapters.outbound.persistence;

import com.chibao.orderservice.application.ports.outbound.OrderRepository;
import com.chibao.orderservice.domain.model.Order;
import com.chibao.orderservice.infrastructure.adapters.outbound.persistence.entity.OrderEntity;
import com.chibao.orderservice.infrastructure.adapters.outbound.persistence.mapper.OrderEntityMapper;
import com.chibao.orderservice.infrastructure.adapters.outbound.persistence.repository.SpringDataOrderRepository;
import org.springframework.stereotype.Component;

@Component
public class JpaOrderRepositoryAdapter implements OrderRepository {
    private final SpringDataOrderRepository repository;

    public JpaOrderRepositoryAdapter(SpringDataOrderRepository repository) {
        this.repository = repository;
    }

    @Override
    public Order save(Order order) {
        OrderEntity entity = OrderEntityMapper.toEntity(order);
        OrderEntity saved = repository.save(entity);
        return OrderEntityMapper.toDomain(saved);
    }

    @Override
    public Order findById(String id) {
        return repository.findById(id)
                .map(OrderEntityMapper::toDomain)
                .orElse(null);
    }
}