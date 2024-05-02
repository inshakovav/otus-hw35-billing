package com.example.payment.service;

import com.example.payment.dto.DeliveryExecutedMessage;
import com.example.payment.dto.OrderCreatedMessage;
import com.example.payment.dto.PaymentExecutedMessage;
import com.example.payment.dto.PaymentRejectedMessage;
import com.example.payment.entity.PaymentEntity;
import com.example.payment.entity.PaymentStatus;
import com.example.payment.kafka.KafkaProducerService;
import com.example.payment.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final KafkaProducerService kafkaProducerService;
    private final SageCompensationService sageCompensationService;

    public void process(OrderCreatedMessage message) {
        PaymentEntity paymentEntity = saveToDb(message);
        boolean isPaymentSuccess = executePayment(message.getOrderId());
        sendPaymentResult(message, paymentEntity, isPaymentSuccess);
    }

    private void sendPaymentResult(OrderCreatedMessage message, PaymentEntity paymentEntity, boolean isPaymentSuccess) {
        if (isPaymentSuccess) {
            log.info("Order successfully paid: {}", message);
            PaymentExecutedMessage paymentExecutedMessage = PaymentExecutedMessage.builder()
                    .orderId(message.getOrderId())
                    .orderDescription(message.getOrderDescription())
                    .productId(message.getProductId())
                    .deliveryAddress(message.getDeliveryAddress())
                    .paymentId(paymentEntity.getId())
                    .build();
            kafkaProducerService.sendSucceededPayment(paymentExecutedMessage);
        } else {
            log.warn("Order NOT paid: {}", message);
            PaymentRejectedMessage paymentRejectedMessage = PaymentRejectedMessage.builder()
                    .orderId(message.getOrderId())
                    .paymentId(paymentEntity.getId())
                    .errorCode("Order id dividable by 5")
                    .build();
            kafkaProducerService.sendRejectedPayment(paymentRejectedMessage);
            sageCompensationService.executePaymentReject(paymentRejectedMessage);
        }
    }

    public PaymentEntity saveToDb(OrderCreatedMessage message) {
        PaymentEntity entity = new PaymentEntity();
        entity.setStatus(PaymentStatus.PENDING);
        entity.setOrderId(message.getOrderId());
        PaymentEntity dbEntity = paymentRepository.save(entity);
        return dbEntity;
    }

    /**
     *
     * @param orderId - from Order service
     * @return return false for all orderId divisible by 5 and grate then 4.
     * Example: 5 - false, 10 - false, 2 - true, 0 -true
     */
    public boolean executePayment(Long orderId) {
        if(orderId < 5) {
            return true;
        }
        return (orderId%5L) != 0;
    }

    @Transactional
    public void executeDeliveryExecution(DeliveryExecutedMessage message) {
        PaymentEntity payment = paymentRepository.findFirstByOrderId(message.getOrderId())
                .orElseThrow(() -> new NumberFormatException("Wrong payment rejection. Can't find payment by payment order id" + message.getOrderId()));;
        log.info("Delivery was succeeded. ---Finish---: {}", message);
        payment.setStatus(PaymentStatus.DELIVERY_SUCCEEDED);
        paymentRepository.save(payment);
    }
}
