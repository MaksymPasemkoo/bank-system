package com.ltp.banksystem.controller;

import com.ltp.banksystem.dto.dtorequest.AccountDTORequest;
import com.ltp.banksystem.dto.dtoresponce.AccountDTOResponse;
import com.ltp.banksystem.model.Account;
import com.ltp.banksystem.service.AccountService;
import com.ltp.banksystem.utils.AccountHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class AccountController {
    @Autowired
    private AccountService accountService;

    @PostMapping("account")
    public ResponseEntity<AccountDTOResponse> createAccount(@RequestBody final AccountDTORequest accountDTORequest) {
        final Account account = accountService.createOrUpdateAccount(accountDTORequest);
        final AccountDTOResponse accountDTOResponse = AccountHelper.convertToAccountDTOResponse(account);
        return new ResponseEntity<>(accountDTOResponse, HttpStatus.OK);
    }

    @GetMapping("account/{id}")
    public ResponseEntity<AccountDTOResponse> findAccountById(@PathVariable final Long id) {
        final Account account = accountService.findAccountById(id);
        final AccountDTOResponse accountDTOResponse = AccountHelper.convertToAccountDTOResponse(account);
        return new ResponseEntity<>(accountDTOResponse, HttpStatus.FOUND);
    }

    @GetMapping("accounts")
    public ResponseEntity<List<AccountDTOResponse>> findAllAccounts() {
        final List<Account> accounts = accountService.findAllAccounts();
        final List<AccountDTOResponse> accountDTOResponses = accounts.stream()
                .map(AccountHelper::convertToAccountDTOResponse)
                .toList();

        return new ResponseEntity<>(accountDTOResponses, HttpStatus.FOUND);
    }

    @PutMapping("account")
    public ResponseEntity<AccountDTOResponse> updateAccount(@RequestBody final AccountDTORequest accountDTORequest) {
        final Account account = accountService.createOrUpdateAccount(accountDTORequest);
        final AccountDTOResponse accountDTOResponse = AccountHelper.convertToAccountDTOResponse(account);
        return new ResponseEntity<>(accountDTOResponse, HttpStatus.OK);
    }

    @DeleteMapping("account/{id}")
    public ResponseEntity<Void> deleteAccount(@PathVariable final Long id) {
        accountService.deleteAccountById(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
