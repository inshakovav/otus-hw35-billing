package com.example.billing.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class PaymentServiceTest {
    PaymentService paymentService = new PaymentService(null, null, null);

    @Test
    void testExecutePayment_whenOrderIdNotDivisibleBy5_thenReturnTrue() {

        boolean result = paymentService.executePayment(Long.valueOf(1));
        Assertions.assertEquals(true, result);
    }

    @Test
    void testExecutePayment_whenOrderIdDivisibleBy5_thenReturnFalse() {

        boolean result = paymentService.executePayment(Long.valueOf(15));
        Assertions.assertEquals(false, result);
    }

    @Test
    void testExecutePayment_whenOrderIdDivisibleBy5AndLess5_thenReturnTrue() {

        boolean result = paymentService.executePayment(Long.valueOf(0));
        Assertions.assertEquals(true, result);
    }
}