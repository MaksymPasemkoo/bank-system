package com.ltp.banksystem.dto.dtorequest;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class WithdrawFromDepositRequest {
    private final Long accountId;
    private final String password;
    private final Long transactionId;
}
