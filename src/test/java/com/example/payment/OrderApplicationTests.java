package com.example.payment;

import com.example.payment.dto.OrderCreatedMessage;
import com.example.payment.dto.PaymentExecutedMessage;
import com.example.payment.dto.PaymentRejectedMessage;
import com.example.payment.entity.PaymentEntity;
import com.example.payment.kafka.KafkaProducerService;
import com.example.payment.repository.PaymentRepository;
import jakarta.persistence.EntityManager;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import static java.lang.Thread.sleep;
import static org.junit.jupiter.api.Assertions.*;

//@SpringBootTest
@Slf4j
@ActiveProfiles("test")
class OrderApplicationTests {

    @Autowired
    PaymentRepository paymentRepository;

    @Autowired
    private WebApplicationContext wac;

    @Autowired
    private KafkaSucceededConsumer kafkaSucceededConsumer;

    @Autowired
    private KafkaRejectedConsumer kafkaRejectedConsumer;

    @Autowired
    private KafkaOrderProducer kafkaOrderProducer;

    @Test
    void paymentSucceededTest() throws InterruptedException {
        // setup
        OrderCreatedMessage orderCreatedMessage = OrderCreatedMessage.builder()
                .orderId(16L)
                .orderDescription("Description of order")
                .productId(123L)
                .productPrice(new BigDecimal(6.2))
                .productQuantity(new BigDecimal(2.0))
                .deliveryAddress("г. Москва, пер. Камергерский")
                .build();

        // act
        kafkaOrderProducer.sendOrder(orderCreatedMessage);

        // verify
        boolean messageConsumed = kafkaSucceededConsumer.getLatch().await(10, TimeUnit.SECONDS);
        long lastPaymentId = getPaymentEntity();
        assertTrue(messageConsumed);
        PaymentExecutedMessage executedMessage = kafkaSucceededConsumer.getExecutedMessage();
        assertEquals(executedMessage.getOrderId(), 16L);
        assertEquals(executedMessage.getOrderDescription(), "Description of order");
        assertEquals(executedMessage.getProductId(), 123L);
        assertEquals(executedMessage.getDeliveryAddress(), "г. Москва, пер. Камергерский");
        assertEquals(executedMessage.getPaymentId(), lastPaymentId);
    }

    private long getPaymentEntity() {
        Optional<PaymentEntity> lastPayment = paymentRepository.findFirstByOrderByIdDesc();
        log.info("Last record={}", lastPayment);
        if (lastPayment.isEmpty()) {
            fail("DB error, Payment doesn't saved");
        }
        return lastPayment.get().getId();
    }

    @Test
    void paymentRejectedTest() throws InterruptedException {
        // setup
        OrderCreatedMessage orderCreatedMessage = OrderCreatedMessage.builder()
                .orderId(15L) // 15 orderId will be payment rejected, is dividable by 5
                .orderDescription("Description of order")
                .productId(123L)
                .productPrice(new BigDecimal(6.2))
                .productQuantity(new BigDecimal(2.0))
                .deliveryAddress("г. Москва, пер. Камергерский")
                .build();

        // act
        kafkaOrderProducer.sendOrder(orderCreatedMessage);

        // verify
        boolean messageConsumed = kafkaRejectedConsumer.getLatch().await(10, TimeUnit.SECONDS);
        long lastPaymentId = getPaymentEntity();
        assertTrue(messageConsumed);
        PaymentRejectedMessage message = kafkaRejectedConsumer.getRejectedMessage();
        assertEquals(message.getOrderId(), 15L);
        assertEquals(message.getPaymentId(), lastPaymentId);
        assertEquals(message.getErrorCode(), "Order id dividable by 5");
    }
}
