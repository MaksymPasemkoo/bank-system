package com.ltp.banksystem.utils;

import com.ltp.banksystem.dto.dtorequest.UserDTORequest;
import com.ltp.banksystem.dto.dtoresponce.UserDTOResponse;
import com.ltp.banksystem.model.User;

public class UserHelper {
    public static UserDTOResponse convertToUserDTOResponse(final User user){
        Long id = user.getUserId();
        String userName = user.getUserName();
        String password = user.getPassword();
        return new UserDTOResponse(id,userName,password);
    }

    public static UserDTORequest convertToUserDTORequest(final User user){
        String userName = user.getUserName();
        String password = user.getPassword();
        return new UserDTORequest(userName,password);
    }
}
