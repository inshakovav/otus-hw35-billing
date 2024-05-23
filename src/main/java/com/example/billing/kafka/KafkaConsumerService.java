//package com.example.billing.kafka;
//
//import com.example.billing.dto.DeliveryExecutedMessage;
//import com.example.billing.dto.OrderCreatedMessage;
//import com.example.billing.service.PaymentService;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.kafka.annotation.KafkaListener;
//import org.springframework.stereotype.Component;
//
//@Slf4j
//@Component
//@RequiredArgsConstructor
//public class KafkaConsumerService {
//
//    private final PaymentService billingService;
//    @KafkaListener(topics = "${billing.kafka.order-created-topic}", groupId = "${billing.kafka.message-group-name}")
//    public void receiveOrderCreatedMessage(OrderCreatedMessage message) {
//        try {
//            paymentService.process(message);
//        } catch (Exception e) {
//            log.warn("Kafka unknown error Order processing: ", message);
//        }
//    }
//
//    @KafkaListener(topics = "${billing.kafka.delivery-executed-topic}", groupId = "${billing.kafka.message-group-name}")
//    public void receiveDeliveryExecuted(DeliveryExecutedMessage message) {
//        try {
//            billingService.executeDeliveryExecution(message);
//        } catch (Exception e) {
//            log.warn("Kafka unknown error Order processing: ", message);
//        }
//    }
//}
