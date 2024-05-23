package com.example.billing.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class PaymentRejectedMessage {
    private Long orderId;
    private Long paymentId;
    private String errorCode;
}
