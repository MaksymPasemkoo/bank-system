package com.ltp.banksystem.utils;

import com.ltp.banksystem.dto.dtorequest.UserDTORequest;
import com.ltp.banksystem.dto.dtoresponce.UserDTOResponse;
import com.ltp.banksystem.model.User;
import com.ltp.banksystem.model.enums.Role;

public class UserHelper {
    public static UserDTOResponse convertToUserDTOResponse(final User user){
        Long id = user.getUserId();
        Role role = user.getRole();
        String userName = user.getUsername();
        String password = user.getPassword();
        return new UserDTOResponse(id,role,userName,password);
    }

    public static UserDTORequest convertToUserDTORequest(final User user){
        Role role = user.getRole();
        String userName = user.getUsername();
        String password = user.getPassword();
        return new UserDTORequest(role,userName,password);
    }
}
