package com.ltp.banksystem.utils;

import com.ltp.banksystem.dto.dtorequest.AccountDTORequest;
import com.ltp.banksystem.dto.dtoresponce.AccountDTOResponse;
import com.ltp.banksystem.model.Account;
import com.ltp.banksystem.model.enums.AccountType;

import java.math.BigDecimal;

public class AccountHelper {
    public static AccountDTOResponse convertToAccountDTOResponse(final Account account) {
        final Long id = account.getAccountId();
        final Long userId = account.getUser().getUserId();
        final AccountType accountType = account.getAccountType();
        final BigDecimal balance = account.getBalance();

        return new AccountDTOResponse(id, userId, accountType, balance);
    }

    public static AccountDTORequest convertToAccountDTORequest(final Account account) {
        final Long userId = account.getUser().getUserId();
        final AccountType accountType = account.getAccountType();
        final BigDecimal balance = account.getBalance();

        return new AccountDTORequest(userId, accountType, balance);
    }
}
