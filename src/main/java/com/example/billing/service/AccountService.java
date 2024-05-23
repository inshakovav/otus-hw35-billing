package com.example.billing.service;

import com.example.billing.dto.AccountCreateDto;
import com.example.billing.entity.AccountEntity;
import com.example.billing.repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

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
}
