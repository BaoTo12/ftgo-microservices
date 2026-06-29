package com.chibao.kitchenservice.infrastructure.adapters.inbound.mapper;

import com.chibao.kitchenservice.application.inbound.command.CreateTicketCommand;
import com.chibao.kitchenservice.application.inbound.result.TicketResult;
import com.chibao.kitchenservice.infrastructure.adapters.inbound.dto.TicketCreateRequest;
import com.chibao.kitchenservice.infrastructure.adapters.inbound.dto.TicketResponse;

public class TicketControllerMapper {
    public static CreateTicketCommand toCommand(TicketCreateRequest request) {
        if (request == null) return null;
        return new CreateTicketCommand(request.getId(), request.getOrderId(), request.getRestaurantId());
    }

    public static TicketResponse toResponse(TicketResult result) {
        if (result == null) return null;
        return new TicketResponse(result.getId(), result.getOrderId(), result.getStatus());
    }
}

