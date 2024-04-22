package com.example.payment.kafka;

import com.example.payment.dto.DeliveryRejectedMessage;
import com.example.payment.dto.PaymentRejectedMessage;
import com.example.payment.dto.WarehouseReservationRejectedMessage;
import com.example.payment.service.SageCompensationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class KafkaConsumerSagaCompensationService {

    private final SageCompensationService sageCompensationService;

    @KafkaListener(topics = "${payment.kafka.warehouse-rejected-topic}", groupId = "${payment.kafka.message-group-name}")
    public void receiveWarehouseRejected(WarehouseReservationRejectedMessage message) {
        try {
            sageCompensationService.executeWarehouseReject(message);
        } catch (Exception e) {
            log.warn("Kafka unknown error Order processing: ", message);
        }
    }

    @KafkaListener(topics = "${payment.kafka.delivery-rejected-topic}", groupId = "${payment.kafka.message-group-name}")
    public void receiveDeliveryRejected(DeliveryRejectedMessage message) {
        try {
            sageCompensationService.executeDeliveryReject(message);
        } catch (Exception e) {
            log.warn("Kafka unknown error Order processing: ", message);
        }
    }
}
