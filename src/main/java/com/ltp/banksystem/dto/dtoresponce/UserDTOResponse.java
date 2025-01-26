package com.ltp.banksystem.dto.dtoresponce;

import com.ltp.banksystem.model.enums.Role;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UserDTOResponse {
    private final Long id;
    private final Role role;
    private final String username;
    private final String password;
}
