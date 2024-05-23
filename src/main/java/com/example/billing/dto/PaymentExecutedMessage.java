package com.example.billing.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class PaymentExecutedMessage {
    private Long orderId;
    private String orderDescription;
    private Long productId;
    private String deliveryAddress;
    private Long paymentId;
}
