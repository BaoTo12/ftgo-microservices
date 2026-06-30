package com.chibao.orderservice.application.service;

import com.chibao.orderservice.application.mapper.OrderMapper;
import com.chibao.orderservice.application.ports.inbound.OrderManagementUseCase;
import com.chibao.orderservice.application.ports.inbound.command.CreateOrderCommand;
import com.chibao.orderservice.application.ports.inbound.result.OrderResult;
import com.chibao.orderservice.application.ports.outbound.KitchenClient;
import com.chibao.orderservice.application.ports.outbound.OrderRepository;
import com.chibao.orderservice.application.ports.outbound.PaymentClient;
import com.chibao.orderservice.domain.model.Order;
import com.chibao.orderservice.domain.model.OrderItem;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class OrderManagementService implements OrderManagementUseCase {
    private final OrderRepository repository;
    private final KitchenClient kitchenClient;
    private final PaymentClient paymentClient;


    @Override
    public OrderResult createOrder(CreateOrderCommand command) {
        List<OrderItem> items = OrderMapper.toDomainItems(command);
        Order order = new Order(UUID.randomUUID().toString(), command.getConsumerId(), command.getRestaurantId(), command.getTotalAmount(), items);
        Order savedOrder = repository.save(order);

        try {
            boolean ticketCreated = kitchenClient.createTicket(savedOrder.getId(), savedOrder.getRestaurantId());
            if (!ticketCreated) {
                savedOrder.reject();
                repository.save(savedOrder);
                return OrderMapper.toResult(savedOrder);
            }

            boolean paymentAuthorized = paymentClient.authorizePayment(savedOrder.getConsumerId(), savedOrder.getTotalAmount());
            if (!paymentAuthorized) {
                savedOrder.reject();
                repository.save(savedOrder);
                kitchenClient.rejectTicket(savedOrder.getId());
                return OrderMapper.toResult(savedOrder);
            }

            savedOrder.approve();
            repository.save(savedOrder);
            kitchenClient.confirmTicket(savedOrder.getId());

        } catch (Exception ex) {
            savedOrder.reject();
            repository.save(savedOrder);
            kitchenClient.rejectTicket(savedOrder.getId());
        }

        return OrderMapper.toResult(savedOrder);
    }

    @Override
    public OrderResult getOrder(String orderId) {
        Order order = repository.findById(orderId);
        return OrderMapper.toResult(order);
    }
}

