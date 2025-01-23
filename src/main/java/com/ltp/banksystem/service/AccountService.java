package com.ltp.banksystem.service;

import com.ltp.banksystem.dto.dtorequest.AccountDTORequest;
import com.ltp.banksystem.model.Account;
import com.ltp.banksystem.model.User;
import com.ltp.banksystem.repository.AccountRepository;
import com.ltp.banksystem.repository.UserRepository;
import com.ltp.banksystem.utils.AccountType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.util.List;
import java.util.NoSuchElementException;

@Service
public class AccountService {
    @Autowired
    private AccountRepository accountRepository;
    @Autowired
    private UserRepository userRepository;

    public Account createOrUpdateAccount(final AccountDTORequest accountDTORequest) {
        final User user = userRepository.findById(accountDTORequest.getUserId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        final AccountType accountType = accountDTORequest.getAccountType();
        final BigDecimal balance = accountDTORequest.getBalance();
        final Account account = new Account(user, accountType, balance);
        accountRepository.save(account);
        return account;
    }

    public Account findAccountById(final Long id) {
        return accountRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Resource not found."));
    }

    public List<Account> findAllAccounts() {
        return accountRepository.findAll();
    }

    public void deleteAccountById(final Long id) {
        Account account = accountRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Resource not found."));
        accountRepository.delete(account);
    }

}
