package com.ltp.banksystem.service;

import com.ltp.banksystem.dto.dtorequest.AccountCredentials;
import com.ltp.banksystem.dto.dtorequest.AccountDTORequest;
import com.ltp.banksystem.dto.dtoresponce.AccountDTOResponse;
import com.ltp.banksystem.exception.PermissionException;
import com.ltp.banksystem.model.Account;
import com.ltp.banksystem.model.User;
import com.ltp.banksystem.repository.AccountRepository;
import com.ltp.banksystem.repository.UserRepository;
import com.ltp.banksystem.model.enums.AccountType;
import com.ltp.banksystem.utils.AccountHelper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;


import java.math.BigDecimal;
import java.util.List;
import java.util.NoSuchElementException;

import static com.ltp.banksystem.model.enums.Role.ADMIN;

@Service
@RequiredArgsConstructor
public class AccountService {
    private final AccountRepository accountRepository;
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    public AccountDTOResponse createOrUpdateAccount(final AccountDTORequest accountDTORequest) {
        final User user = userRepository.findById(accountDTORequest.getUserId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        final AccountType accountType = accountDTORequest.getAccountType();
        final BigDecimal balance = accountDTORequest.getBalance();

        final Account account = new Account(user, accountType, balance);

        if (accountType == AccountType.BUSINESS && user.getRole() != ADMIN) {
            throw new PermissionException("Access for admin only.");
        }
        accountRepository.save(account);

        return AccountHelper.convertToAccountDTOResponse(account);
    }

    public AccountDTOResponse findAccountById(final Long id) {
        final Account account = accountRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Account not found."));
        return AccountHelper.convertToAccountDTOResponse(account);
    }

    public List<AccountDTOResponse> findAllAccounts() {
        List<Account> accounts = accountRepository.findAll();
        return accounts.stream()
                .map(AccountHelper::convertToAccountDTOResponse)
                .toList();
    }

    public void deleteAccountById(final Long id) {
        final Account account = accountRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Account  not found."));
        accountRepository.delete(account);
    }

    public void deleteAccount(final AccountCredentials accountCredentials) {
        final Long accountId = accountCredentials.getAccountId();
        final Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new NoSuchElementException("Account not found."));

        final String password = account.getUser().getPassword();
        final String checkPassword = accountCredentials.getPassword();

        if (!bCryptPasswordEncoder.matches(checkPassword, password)) {
            throw new PermissionException("Password not correct");
        }
        accountRepository.delete(account);
    }


}
