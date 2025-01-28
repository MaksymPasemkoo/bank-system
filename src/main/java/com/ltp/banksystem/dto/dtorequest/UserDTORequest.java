package com.ltp.banksystem.dto.dtorequest;

import com.ltp.banksystem.model.enums.Role;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class UserDTORequest {
    private final Role role;
    private final String username;
    private final String password;
}
