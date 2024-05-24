package com.example.billing.dto;

import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class PaymentExecutedMessage {
    private Long accountId;
    private Long orderId;
    private BigDecimal orderPrice;
//    private Long paymentId;
}
