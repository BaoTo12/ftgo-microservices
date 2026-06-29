package com.chibao.kitchenservice.application.inbound;

import com.chibao.kitchenservice.application.inbound.command.CreateTicketCommand;
import com.chibao.kitchenservice.application.inbound.result.TicketResult;

public interface TicketManagementUseCase {
    TicketResult createTicket(CreateTicketCommand command);
    TicketResult getTicket(String id);
}
