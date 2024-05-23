//package com.example.billing.service;
//
//import com.example.billing.dto.DeliveryRejectedMessage;
//import com.example.billing.dto.PaymentRejectedMessage;
//import com.example.billing.dto.WarehouseReservationRejectedMessage;
//import com.example.billing.entity.PaymentEntity;
//import com.example.billing.entity.PaymentStatus;
//import com.example.billing.repository.PaymentRepository;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.stereotype.Service;
//
//import javax.transaction.Transactional;
//
//@Service
//@RequiredArgsConstructor
//@Slf4j
//public class SageCompensationService {
//
//    private final PaymentRepository paymentRepository;
//
//    @Transactional
//    public void executePaymentReject(PaymentRejectedMessage message) {
//        PaymentEntity payment = findPaymentByOrderId(message.getOrderId());
//        log.info("Payment was rejected: {}", message);
//        payment.setStatus(PaymentStatus.REJECTED_BY_PAYMENT);
//        paymentRepository.save(payment);
//    }
//
//    @Transactional
//    public void executeWarehouseReject(WarehouseReservationRejectedMessage message) {
//        PaymentEntity payment = findPaymentByOrderId(message.getOrderId());
//        log.info("Warehouse reservation was rejected: {}", message);
//        payment.setStatus(PaymentStatus.REJECTED_BY_WAREHOUSE);
//        paymentRepository.save(payment);
//    }
//
//    @Transactional
//    public void executeDeliveryReject(DeliveryRejectedMessage message) {
//        PaymentEntity payment = findPaymentByOrderId(message.getOrderId());
//        log.info("Delivery was rejected: {}", message);
//        payment.setStatus(PaymentStatus.REJECTED_BY_DELIVERY);
//        paymentRepository.save(payment);
//    }
//
//    private PaymentEntity findPaymentByOrderId(long orderId) {
//        return paymentRepository.findFirstByOrderId(orderId)
//                .orElseThrow(() -> new NumberFormatException("Wrong payment rejection. Can't find payment by payment id" + orderId));
//    }
//}
