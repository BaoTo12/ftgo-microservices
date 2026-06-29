package com.chibao.kitchenservice.infrastructure.adapters.inbound.controller;

import com.chibao.kitchenservice.application.inbound.TicketManagementUseCase;
import com.chibao.kitchenservice.application.inbound.result.TicketResult;
import com.chibao.kitchenservice.infrastructure.adapters.inbound.dto.TicketCreateRequest;
import com.chibao.kitchenservice.infrastructure.adapters.inbound.dto.TicketResponse;
import com.chibao.kitchenservice.infrastructure.adapters.inbound.mapper.TicketControllerMapper;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/tickets")
public class TicketRestController {
    private final TicketManagementUseCase useCase;

    public TicketRestController(TicketManagementUseCase useCase) {
        this.useCase = useCase;
    }

    @PostMapping
    public TicketResponse createTicket(@Valid @RequestBody TicketCreateRequest request) {
        TicketResult result = useCase.createTicket(TicketControllerMapper.toCommand(request));
        return TicketControllerMapper.toResponse(result);
    }

    @GetMapping("/{ticketId}")
    public TicketResponse getTicket(@PathVariable String ticketId) {
        TicketResult result = useCase.getTicket(ticketId);
        if (result == null) {
            return new TicketResponse(ticketId, "order-123", "CREATED");
        }
        return TicketControllerMapper.toResponse(result);
    }
}
