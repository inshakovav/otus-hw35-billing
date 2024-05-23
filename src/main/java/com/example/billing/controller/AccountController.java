package com.example.billing.controller;

import com.example.billing.dto.AccountCreateDto;
import com.example.billing.entity.AccountEntity;
import com.example.billing.repository.AccountRepository;
import com.example.billing.service.AccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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

    @PostMapping
    public AccountEntity add(@RequestBody AccountCreateDto accountCreateDto) {
        return accountService.create(accountCreateDto);
    }
}
