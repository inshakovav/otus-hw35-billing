package com.example.billing.kafka;

import com.example.billing.dto.PaymentExecutedMessage;
import com.example.billing.dto.PaymentRejectedMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class KafkaProducerService {
    @Value("${billing.kafka.payment-executed-topic}")
    private String paymentSucceededTopic;

    @Value("${billing.kafka.payment-rejected-topic}")
    private String paymentRejectedTopic;

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public void sendSucceededPayment(PaymentExecutedMessage message) {
        kafkaTemplate.send(paymentSucceededTopic, message);
    }

    public void sendRejectedPayment(PaymentRejectedMessage message) {
        kafkaTemplate.send(paymentRejectedTopic, message);
    }
}
