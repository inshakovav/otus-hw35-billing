package com.example.billing.dto;

import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class OrderCreatedMessage {
    private Long orderId;
    private String orderDescription;
    private Long productId;
    private BigDecimal productPrice;
    private BigDecimal productQuantity;
    private String deliveryAddress;
}
