package com.ltp.banksystem.dto.dtorequest;

import com.ltp.banksystem.model.enums.AccountType;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;

@AllArgsConstructor
@Getter
public class AccountDTORequest {
    private final Long userId;
    private final AccountType accountType;
    private final BigDecimal balance;

}
