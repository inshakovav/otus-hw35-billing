package com.example.billing.controller;

import com.example.billing.dto.AccountCreateDto;
import com.example.billing.dto.TopUpDto;
import com.example.billing.entity.AccountEntity;
import com.example.billing.repository.AccountRepository;
import com.example.billing.service.AccountService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/account")
@RequiredArgsConstructor
public class AccountController {

    private final AccountRepository repository;
    private final AccountService accountService;

    @GetMapping
    public List<AccountEntity> getAll() {
        return repository.findAll();
    }

    @GetMapping("/{id}")
    public AccountEntity getById(@PathVariable Long id) {
        return repository.findById(id).orElseThrow(new RuntimeException("Not fond account id:" + id);
    }

    @PostMapping
    public AccountEntity add(@RequestBody AccountCreateDto accountCreateDto) {
        return accountService.create(accountCreateDto);
    }

    @PostMapping("/top-up/{id}")
    public AccountEntity topUp(@PathVariable Long id, @RequestBody TopUpDto amount) {
        log.info("Top up accountId:{} with amounts of:{}", id, amount.getAmount());
        return accountService.topUp(id, amount);
    }
}
