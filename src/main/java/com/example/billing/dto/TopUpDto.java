package com.example.billing.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class TopUpDto {
    BigDecimal amount;
}
