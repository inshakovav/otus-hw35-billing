package com.example.billing.service;

import com.example.billing.dto.*;
import com.example.billing.entity.AccountEntity;
import com.example.billing.kafka.KafkaProducerService;
import com.example.billing.repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.util.Optional;

import static org.springframework.util.Assert.isNull;

@Slf4j
@Service
@RequiredArgsConstructor
public class AccountService {

    private final AccountRepository accountRepository;
    public final KafkaProducerService kafkaProducerService;

    public AccountEntity create(AccountCreateDto accountCreateDto) {
        AccountEntity account = AccountEntity.builder()
                .name(accountCreateDto.getName())
                .balance(new BigDecimal(0.0))
                .build();
        return accountRepository.save(account);
    }

    @Transactional
    public AccountEntity topUp(Long accountId, TopUpDto topUpDto) {
        Optional<AccountEntity> optionalAccount = accountRepository.findById(accountId);
        if (optionalAccount.isEmpty()) {
            throw new RuntimeException("Can't find account with id:" + accountId);
        }
        AccountEntity account = optionalAccount.get();
        log.info("Top up balance:{} to:{}", account.getBalance(), topUpDto.getAmount());
        account.setBalance(account.getBalance().add(topUpDto.getAmount()));
        accountRepository.save(account);
        return account;
    }

    public void billing(OrderCreatedMessage message) {
        AccountEntity account = writeOff(message);
        if (account != null) {
            PaymentExecutedMessage paymentExecutedMessage = PaymentExecutedMessage.builder()
                    .accountId(message.getAccountId())
                    .orderId(message.getOrderId())
                    .orderPrice(message.getOrderPrice())
                    .accountId(account.getId())
                    .build();
            kafkaProducerService.sendSucceededPayment(paymentExecutedMessage);
        } else {
            PaymentRejectedMessage paymentRejectedMessage = PaymentRejectedMessage.builder()
                    .accountId(message.getAccountId())
                    .orderId(message.getOrderId())
                    .orderPrice(message.getOrderPrice())
                    .accountId(account.getId())
                    .errorCode("Not enough money")
                    .build();
            kafkaProducerService.sendRejectedPayment(paymentRejectedMessage);
        }
    }

    @Transactional
    public AccountEntity writeOff(OrderCreatedMessage message) {
        Optional<AccountEntity> optionalAccount = accountRepository.findById(message.getAccountId());
        if (optionalAccount.isEmpty()) {
            log.warn("Can't find account with id:{}", message.getAccountId());
            return null;
        }
        AccountEntity account = optionalAccount.get();
        if (account.getBalance().compareTo(message.getOrderPrice()) < 0) {
            log.info("Not enough money on the account. Balance:{}", account.getBalance());
            return null;
        }
        subtractMoney(account, message.getOrderPrice());
        accountRepository.save(account);
        return account;
    }

    void subtractMoney(AccountEntity account, BigDecimal orderPrice) {
        BigDecimal subtractResult = account.getBalance().subtract(orderPrice);
        account.setBalance(subtractResult);
        log.info("Write of:{} from account:{}, balance:{}", orderPrice, account.getId(), subtractResult);
    }
}
