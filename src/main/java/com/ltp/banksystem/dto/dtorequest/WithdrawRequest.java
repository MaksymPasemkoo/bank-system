package com.ltp.banksystem.dto.dtorequest;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;

@AllArgsConstructor
@Getter
public class WithdrawRequest {
    private final Long accountId;
    private final String password;
    private final BigDecimal amount;
}
