package com.ltp.banksystem.dto.dtorequest;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class AccountCredentials {
    private final Long accountId;
    private final String password;
}
