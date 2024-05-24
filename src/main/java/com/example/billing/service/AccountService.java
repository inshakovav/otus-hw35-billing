package com.example.billing.service;

import com.example.billing.dto.*;
import com.example.billing.entity.AccountEntity;
import com.example.billing.kafka.KafkaProducerService;
import com.example.billing.repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.crossstore.ChangeSetPersister;
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
        AccountEntity account = findAccount(message);
        try {
            account = writeOff(account, message.getOrderPrice());
            PaymentExecutedMessage paymentExecutedMessage = PaymentExecutedMessage.builder()
                    .accountId(message.getAccountId())
                    .orderId(message.getOrderId())
                    .orderPrice(message.getOrderPrice())
                    .accountId(account.getId())
                    .build();
            kafkaProducerService.sendSucceededPayment(paymentExecutedMessage);
        } catch (RuntimeException ex) {
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
    public AccountEntity writeOff(AccountEntity account, BigDecimal orderPrice) {
        if (account.getBalance().compareTo(orderPrice) < 0) {
            log.info("Write off rejected. Not enough money on the account. Balance:{}, orderPrice:{}", account.getBalance(), orderPrice);
            throw new RuntimeException("Not enough money on the account. Balance:" + account.getBalance());
        }
        subtractMoney(account, orderPrice);
        accountRepository.save(account);
        return account;
    }

    private AccountEntity findAccount(OrderCreatedMessage message) {
        AccountEntity account = accountRepository.findById(message.getAccountId())
                .orElseThrow(() -> new RuntimeException("Can't find account with id:" + message.getAccountId()));
        return account;
    }

    void subtractMoney(AccountEntity account, BigDecimal orderPrice) {
        BigDecimal subtractResult = account.getBalance().subtract(orderPrice);
        account.setBalance(subtractResult);
        log.info("Write of:{} from account:{}, balance:{}", orderPrice, account.getId(), subtractResult);
    }
}
