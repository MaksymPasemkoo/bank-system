package com.ltp.banksystem.dto.dtorequest;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@AllArgsConstructor
public class UserCredentials {
    private final String username;
    private final String password;
}
