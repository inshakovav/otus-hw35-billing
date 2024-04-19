package com.example.payment;

import com.example.payment.dto.PaymentExecutedMessage;
import com.example.payment.dto.PaymentRejectedMessage;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.concurrent.CountDownLatch;

@Component
@Slf4j
@Getter
public class KafkaRejectedConsumer {

    private CountDownLatch latch = new CountDownLatch(1);
    private PaymentRejectedMessage rejectedMessage;

    @KafkaListener(topics = "hw30.payment.rejected", groupId = "my-group")
    public void receiveRejected(PaymentRejectedMessage message) {
        log.info("received Rejected payload='{}'", message.toString());
        rejectedMessage = message;
        latch.countDown();
    }

    public void resetLatch() {
        latch = new CountDownLatch(1);
    }
}
