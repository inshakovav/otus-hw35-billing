package com.example.billing;

import com.example.billing.dto.OrderCreatedMessage;
import com.example.billing.dto.PaymentExecutedMessage;
import com.example.billing.dto.PaymentRejectedMessage;
import com.example.billing.dto.TopUpDto;
import com.example.billing.entity.AccountEntity;
import com.example.billing.repository.AccountRepository;
import com.example.billing.service.AccountService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.context.WebApplicationContext;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Slf4j
@ActiveProfiles("test")
class ApplicationTests {

    @Autowired
    AccountRepository accountRepository;

    @Autowired
    private WebApplicationContext wac;

    @Autowired
    private KafkaSucceededConsumer kafkaSucceededConsumer;

    @Autowired
    private AccountService accountService;

    @Autowired
    private KafkaRejectedConsumer kafkaRejectedConsumer;

    @Autowired
    private KafkaOrderProducer kafkaOrderProducer;

    @Test
    void paymentSucceededTest() throws InterruptedException {
        // setup
        OrderCreatedMessage orderCreatedMessage = OrderCreatedMessage.builder()
                .accountId(2L)
                .orderId(123L)
                .orderPrice(new BigDecimal(1.2))
                .build();
//        TopUpDto topUpDto = new TopUpDto();
//        topUpDto.setAmount(new BigDecimal(1.2));
//        accountService.topUp(2L, topUpDto);

        // act
        kafkaOrderProducer.sendOrder(orderCreatedMessage);

        // verify
        boolean messageConsumed = kafkaSucceededConsumer.getLatch().await(10, TimeUnit.SECONDS);
//        long lastPaymentId = getPaymentEntity();
        assertTrue(messageConsumed);
        PaymentExecutedMessage executedMessage = kafkaSucceededConsumer.getExecutedMessage();
        assertEquals(executedMessage.getAccountId(), 2L);
        assertEquals(executedMessage.getOrderId(), 123L);

        BigDecimal orderPrice = executedMessage.getOrderPrice();
        BigDecimal orderPriceRounded = orderPrice.setScale(2, RoundingMode.DOWN);
        BigDecimal expectedOrderPrice = new BigDecimal(1.2).setScale(2, RoundingMode.DOWN);
        assertEquals(expectedOrderPrice, orderPriceRounded);
//        assertEquals(executedMessage.getPaymentId(), lastPaymentId);
    }

    @Test
    void paymentRejectedTest() throws InterruptedException {
        // setup
        OrderCreatedMessage orderCreatedMessage = OrderCreatedMessage.builder()
                .accountId(2L)
                .orderId(123L)
                .orderPrice(new BigDecimal(1000.2))
                .build();

        // act
        kafkaOrderProducer.sendOrder(orderCreatedMessage);

        // verify
        boolean messageConsumed = kafkaRejectedConsumer.getLatch().await(10, TimeUnit.SECONDS);
//        long lastPaymentId = getPaymentEntity();
        assertTrue(messageConsumed);
        PaymentRejectedMessage executedMessage = kafkaRejectedConsumer.getRejectedMessage();
        assertEquals(executedMessage.getAccountId(), 2L);
        assertEquals(executedMessage.getOrderId(), 123L);

//        BigDecimal orderPrice = executedMessage.getOrderPrice();
//        BigDecimal orderPriceRounded = orderPrice.setScale(2, RoundingMode.DOWN);
//        BigDecimal expectedOrderPrice = new BigDecimal(1.2).setScale(2, RoundingMode.DOWN);
//        assertEquals(expectedOrderPrice, orderPriceRounded);
    }
}
