package com.ltp.banksystem.dto.dtorequest;

import com.ltp.banksystem.model.Account;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;

@AllArgsConstructor
@Getter
public class TransferRequest {
    private final Long accountId;
    private final String password;
    private final Long toAccountId;
    private final BigDecimal amount;
}
