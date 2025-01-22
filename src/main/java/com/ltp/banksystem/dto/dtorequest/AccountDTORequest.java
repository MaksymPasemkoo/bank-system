package com.ltp.banksystem.dto.dtorequest;

import com.ltp.banksystem.utils.AccountType;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AccountDTORequest {
    private final Long userId;
    private final AccountType accountType;
}
