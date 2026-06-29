package com.chibao.orderservice.infrastructure.adapters.outbound.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class OptimisticLockRetryAspect {

    @Around("@annotation(com.ftgo.order.infrastructure.adapters.secondary.aspect.RetryOnOptimisticLock)")
    public Object retry(ProceedingJoinPoint joinPoint) throws Throwable {
        int maxRetries = 3;
        int numAttempts = 0;
        ObjectOptimisticLockingFailureException lockFailureException;
        do {
            numAttempts++;
            try {
                return joinPoint.proceed();
            } catch (ObjectOptimisticLockingFailureException ex) {
                lockFailureException = ex;
                if (numAttempts >= maxRetries) {
                    throw lockFailureException;
                }
                Thread.sleep(numAttempts * 100L);
            }
        } while (numAttempts < maxRetries);
        throw lockFailureException;
    }
}

