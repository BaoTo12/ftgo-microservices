package com.chibao.orderservice.infrastructure.adapters.outbound.clients;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@FeignClient(name = "kitchen-service")
public interface KitchenFeignClient {

    @RequestMapping(
            method = RequestMethod.GET,
            value = "/v1/tickets/{ticketId}",
            consumes = "application/json"
    )
    String getTicket(@PathVariable("ticketId") String ticketId);

    @RequestMapping(
            method = RequestMethod.POST,
            value = "/v1/tickets",
            consumes = "application/json"
    )
    String createTicket(@RequestBody TicketCreateRequest request);
}
