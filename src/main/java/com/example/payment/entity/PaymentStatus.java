package com.example.payment.entity;

public enum PaymentStatus {
    PENDING,
    REJECTED_BY_PAYMENT,
    REJECTED_BY_WAREHOUSE,
    REJECTED_BY_DELIVERY,
    DELIVERY_SUCCEEDED
}
