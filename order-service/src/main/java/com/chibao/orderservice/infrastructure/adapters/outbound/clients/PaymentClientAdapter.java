package com.chibao.orderservice.infrastructure.adapters.outbound.clients;

import com.chibao.orderservice.application.ports.outbound.PaymentClient;
import io.github.resilience4j.bulkhead.annotation.Bulkhead;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Slf4j
@Component
public class PaymentClientAdapter implements PaymentClient {

    @Override
    @Retry(name = "paymentRetry")
    @RateLimiter(name = "paymentRateLimiter", fallbackMethod = "authorizePaymentFallback")
    @CircuitBreaker(name = "paymentCircuitBreaker", fallbackMethod = "authorizePaymentFallback")
    @Bulkhead(name = "paymentBulkhead", fallbackMethod = "authorizePaymentFallback")
    public boolean authorizePayment(String consumerId, BigDecimal amount) {
        System.out.println("Hexagonal Infrastructure Adapter: authorizing payment via client: " + amount);
        return true;
    }
    public boolean authorizePaymentFallback(String consumerId, BigDecimal amount, Throwable throwable) {
        log.error("Payment execution boundary failure detected. Initializing failure sequence: {}", throwable.getMessage());
        return false;
    }
}

