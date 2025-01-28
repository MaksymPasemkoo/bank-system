package com.ltp.banksystem.controller;

import com.ltp.banksystem.dto.dtorequest.AccountCredentials;
import com.ltp.banksystem.dto.dtorequest.AccountDTORequest;
import com.ltp.banksystem.dto.dtoresponce.AccountDTOResponse;
import com.ltp.banksystem.model.Account;
import com.ltp.banksystem.service.AccountService;
import com.ltp.banksystem.utils.AccountHelper;
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

    @PreAuthorize("hasRole('USER')")
    @PostMapping
    public ResponseEntity<AccountDTOResponse> createAccount(@RequestBody final AccountDTORequest accountDTORequest) {
        final Account account = accountService.createOrUpdateAccount(accountDTORequest);
        final AccountDTOResponse accountDTOResponse = AccountHelper.convertToAccountDTOResponse(account);
        return new ResponseEntity<>(accountDTOResponse, HttpStatus.OK);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/{id}")
    public ResponseEntity<AccountDTOResponse> findAccountById(@PathVariable final Long id) {
        final Account account = accountService.findAccountById(id);
        final AccountDTOResponse accountDTOResponse = AccountHelper.convertToAccountDTOResponse(account);
        return new ResponseEntity<>(accountDTOResponse, HttpStatus.FOUND);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public ResponseEntity<List<AccountDTOResponse>> findAllAccounts() {
        final List<Account> accounts = accountService.findAllAccounts();
        final List<AccountDTOResponse> accountDTOResponses = accounts.stream()
                .map(AccountHelper::convertToAccountDTOResponse)
                .toList();

        return new ResponseEntity<>(accountDTOResponses, HttpStatus.FOUND);
    }

    @PreAuthorize("hasRole('USER')")
    @PutMapping
    public ResponseEntity<AccountDTOResponse> updateAccount(@RequestBody final AccountDTORequest accountDTORequest) {
        final Account account = accountService.createOrUpdateAccount(accountDTORequest);
        final AccountDTOResponse accountDTOResponse = AccountHelper.convertToAccountDTOResponse(account);
        return new ResponseEntity<>(accountDTOResponse, HttpStatus.OK);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteAccountById(@PathVariable final Long id) {
        accountService.deleteAccountById(id);
        return new ResponseEntity<>("Deleted",HttpStatus.OK);
    }

    @PreAuthorize("hasRole('USER')")
    @DeleteMapping
    public ResponseEntity<String> deleteAccount(@RequestBody final AccountCredentials accountCredentials){
        accountService.deleteAccount(accountCredentials);
        return new ResponseEntity<>("Deleted",HttpStatus.OK);
    }
}
