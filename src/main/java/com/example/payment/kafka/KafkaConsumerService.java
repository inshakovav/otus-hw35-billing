package com.example.payment.kafka;

import com.example.payment.dto.DeliveryExecutedMessage;
import com.example.payment.dto.OrderCreatedMessage;
import com.example.payment.service.PaymentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class KafkaConsumerService {

    private final PaymentService paymentService;
    @KafkaListener(topics = "${payment.kafka.order-created-topic}", groupId = "${payment.kafka.message-group-name}")
    public void receiveOrderCreatedMessage(OrderCreatedMessage message) {
        try {
            paymentService.process(message);
        } catch (Exception e) {
            log.warn("Kafka unknown error Order processing: ", message);
        }
    }

    @KafkaListener(topics = "${payment.kafka.delivery-executed-topic}", groupId = "${payment.kafka.message-group-name}")
    public void receiveDeliveryExecuted(DeliveryExecutedMessage message) {
        try {
            paymentService.executeDeliveryExecution(message);
        } catch (Exception e) {
            log.warn("Kafka unknown error Order processing: ", message);
        }
    }
}
