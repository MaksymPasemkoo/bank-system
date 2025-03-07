package com.ltp.banksystem.controller;

import com.ltp.banksystem.dto.dtorequest.AccountCredentials;
import com.ltp.banksystem.dto.dtorequest.AccountDTORequest;
import com.ltp.banksystem.dto.dtoresponce.AccountDTOResponse;
import com.ltp.banksystem.service.AccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/account")
public class AccountController {
    private final AccountService accountService;

    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    @PostMapping
    public ResponseEntity<AccountDTOResponse> createAccount(@RequestBody final AccountDTORequest accountDTORequest) {
        final AccountDTOResponse accountDTOResponse = accountService.createOrUpdateAccount(accountDTORequest);
        return ResponseEntity.ok(accountDTOResponse);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/{id}")
    public ResponseEntity<AccountDTOResponse> findAccountById(@PathVariable final Long id) {
        final AccountDTOResponse accountDTOResponse = accountService.findAccountById(id);
        return ResponseEntity.status(HttpStatus.FOUND).body(accountDTOResponse);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public ResponseEntity<List<AccountDTOResponse>> findAllAccounts() {
        final List<AccountDTOResponse> accountDTOResponses = accountService.findAllAccounts();
        return ResponseEntity.status(HttpStatus.FOUND).body(accountDTOResponses);
    }

    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    @PutMapping
    public ResponseEntity<AccountDTOResponse> updateAccount(@RequestBody final AccountDTORequest accountDTORequest) {
        final AccountDTOResponse accountDTOResponse = accountService.createOrUpdateAccount(accountDTORequest);
        return ResponseEntity.ok(accountDTOResponse);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAccountById(@PathVariable final Long id) {
        final boolean isDeleted = accountService.deleteAccountById(id);
        return isDeleted ? ResponseEntity.ok().build()
                : ResponseEntity.notFound().build();
    }

    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    @DeleteMapping
    public ResponseEntity<String> deleteAccount(@RequestBody final AccountCredentials accountCredentials) {
        final boolean isDeleted = accountService.deleteAccount(accountCredentials);
        return isDeleted ? ResponseEntity.ok().build()
                : ResponseEntity.notFound().build();
    }
}
