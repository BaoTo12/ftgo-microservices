package com.chibao.orderservice.application.ports.inbound;

import com.chibao.orderservice.application.ports.inbound.command.CreateOrderCommand;
import com.chibao.orderservice.application.ports.inbound.result.OrderResult;

public interface OrderManagementUseCase {
    OrderResult createOrder(CreateOrderCommand command);
    OrderResult getOrder(String orderId);
}
