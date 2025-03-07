package com.ltp.banksystem.service;

import com.ltp.banksystem.dto.dtorequest.UserDTORequest;
import com.ltp.banksystem.exception.PermissionException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthenticationService {
    private final AuthenticationManager authenticationManager;
    private final JwtTokenService jwtTokenService;

    public String login(final UserDTORequest userDTORequest){
        final UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken =
                new UsernamePasswordAuthenticationToken(userDTORequest.getUsername(),userDTORequest.getPassword());

        final Authentication authentication =  authenticationManager.authenticate(usernamePasswordAuthenticationToken);
        if (!authentication.isAuthenticated()){
            throw new PermissionException("Password or username is incorrect.");
        }
        return jwtTokenService.generateJwtToken(userDTORequest.getUsername());
    }
}
