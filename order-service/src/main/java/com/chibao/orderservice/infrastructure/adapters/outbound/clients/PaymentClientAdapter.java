package com.chibao.orderservice.infrastructure.adapters.outbound.clients;

import com.chibao.orderservice.application.ports.outbound.PaymentClient;
import io.github.resilience4j.bulkhead.annotation.Bulkhead;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class PaymentClientAdapter implements PaymentClient {

    @Override
    @Bulkhead(name = "paymentBulkhead", fallbackMethod = "authorizePaymentFallback")
    public boolean authorizePayment(String consumerId, BigDecimal amount) {
        System.out.println("Hexagonal Infrastructure Adapter: authorizing payment via client: " + amount);
        return true;
    }
}

