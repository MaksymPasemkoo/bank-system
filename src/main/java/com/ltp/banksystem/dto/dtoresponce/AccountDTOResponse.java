package com.ltp.banksystem.dto.dtoresponce;

import com.ltp.banksystem.utils.AccountType;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AccountDTOResponse {
    private final Long id;
    private final Long userId;
    private final AccountType accountType;
}
