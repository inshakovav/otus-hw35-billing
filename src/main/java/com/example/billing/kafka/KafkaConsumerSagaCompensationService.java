package com.example.billing.kafka;

import com.example.billing.dto.DeliveryRejectedMessage;
import com.example.billing.dto.WarehouseReservationRejectedMessage;
import com.example.billing.service.SageCompensationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class KafkaConsumerSagaCompensationService {

    private final SageCompensationService sageCompensationService;

    @KafkaListener(topics = "${billing.kafka.warehouse-rejected-topic}", groupId = "${billing.kafka.message-group-name}")
    public void receiveWarehouseRejected(WarehouseReservationRejectedMessage message) {
        try {
            sageCompensationService.executeWarehouseReject(message);
        } catch (Exception e) {
            log.warn("Kafka unknown error Order processing: ", message);
        }
    }

    @KafkaListener(topics = "${billing.kafka.delivery-rejected-topic}", groupId = "${billing.kafka.message-group-name}")
    public void receiveDeliveryRejected(DeliveryRejectedMessage message) {
        try {
            sageCompensationService.executeDeliveryReject(message);
        } catch (Exception e) {
            log.warn("Kafka unknown error Order processing: ", message);
        }
    }
}
