package com.ltp.banksystem.dto.dtorequest;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UserDTORequest {
    private String username;
    private String password;
}
