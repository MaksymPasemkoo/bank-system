package com.ltp.banksystem.utils;

import com.ltp.banksystem.dto.dtorequest.UserDTORequest;
import com.ltp.banksystem.dto.dtoresponce.UserDTOResponse;
import com.ltp.banksystem.model.User;
import com.ltp.banksystem.model.enums.Role;

public class UserHelper {
    public static UserDTOResponse convertToUserDTOResponse(final User user) {
        final Long id = user.getUserId();
        final Role role = user.getRole();
        final String userName = user.getUsername();
        final String password = user.getPassword();
        return new UserDTOResponse(id, role, userName, password);
    }

    public static UserDTORequest convertToUserDTORequest(final User user) {
        final Role role = user.getRole();
        final String userName = user.getUsername();
        final String password = user.getPassword();
        return new UserDTORequest(role, userName, password);
    }

    public static User convertToUser(final UserDTOResponse userDTOResponse){
        final Long id = userDTOResponse.getId();
        final Role role = userDTOResponse.getRole();
        final String userName = userDTOResponse.getUsername();
        final String password = userDTOResponse.getPassword();
        return new User(id,role,userName,password);
    }
}
