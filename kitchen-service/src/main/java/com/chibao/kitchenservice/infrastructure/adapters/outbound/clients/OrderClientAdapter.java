package com.chibao.kitchenservice.infrastructure.adapters.outbound.clients;

import com.chibao.kitchenservice.application.outbound.OrderClient;
import org.springframework.stereotype.Component;

@Component
public class OrderClientAdapter implements OrderClient {
    private final OrderFeignClient feignClient;

    public OrderClientAdapter(OrderFeignClient feignClient) {
        this.feignClient = feignClient;
    }

    @Override
    public String getOrderDetails(String orderId) {
        return feignClient.getOrder(orderId);
    }
}
