package com.ltp.banksystem.dto.dtoresponce;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UserDTOResponse {
    private Long id;
    private String username;
    private String password;
}
