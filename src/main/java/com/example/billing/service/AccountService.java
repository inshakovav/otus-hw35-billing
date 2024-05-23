package com.example.billing.service;

import com.example.billing.dto.AccountCreateDto;
import com.example.billing.dto.TopUpDto;
import com.example.billing.entity.AccountEntity;
import com.example.billing.repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class AccountService {

    private final AccountRepository accountRepository;

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
}
