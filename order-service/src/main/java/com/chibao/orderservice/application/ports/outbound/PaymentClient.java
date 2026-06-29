package com.chibao.orderservice.application.ports.outbound;

import java.math.BigDecimal;

public interface PaymentClient {
    boolean authorizePayment(String consumerId, BigDecimal amount);
}

