package com.example.billing.kafka;

import com.example.billing.dto.OrderCreatedMessage;
import com.example.billing.service.AccountService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class KafkaConsumerService {

    private final AccountService accountService;
    @KafkaListener(topics = "${billing.kafka.order-created-topic}", groupId = "${billing.kafka.message-group-name}")
    public void receiveOrderCreatedMessage(OrderCreatedMessage message) {
        try {
            accountService.billing(message);
        } catch (Exception e) {
            log.warn("Kafka unknown error Order processing: ", message);
        }
    }
}
