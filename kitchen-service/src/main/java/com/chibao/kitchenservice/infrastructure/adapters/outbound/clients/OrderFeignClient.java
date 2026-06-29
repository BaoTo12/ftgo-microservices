package com.chibao.kitchenservice.infrastructure.adapters.outbound.clients;

import com.chibao.kitchenservice.config.FeignClientSSLConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@FeignClient(
        name = "order-service"
        // configuration = FeignClientSSLConfig.class
)
public interface OrderFeignClient {
    @RequestMapping(
            method = RequestMethod.GET,
            value = "/v1/orders/{orderId}",
            consumes = "application/json"
    )
    String getOrder(@PathVariable("orderId") String orderId);
}
