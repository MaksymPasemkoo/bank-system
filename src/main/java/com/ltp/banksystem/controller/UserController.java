package com.ltp.banksystem.controller;

import com.ltp.banksystem.dto.dtorequest.UserDTORequest;
import com.ltp.banksystem.dto.dtoresponce.UserDTOResponse;
import com.ltp.banksystem.model.User;
import com.ltp.banksystem.repository.UserRepository;
import com.ltp.banksystem.service.UserService;
import com.ltp.banksystem.utils.UserHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class UserController {
    @Autowired
    private UserService userService;

    @PostMapping("user")
    public ResponseEntity<UserDTOResponse> createUser(@RequestBody final UserDTORequest userDTORequest){
        final User user = userService.createOrUpdateUser(userDTORequest);
        final UserDTOResponse userDTOResponse = UserHelper.convertToUserDTOResponse(user);
        return new ResponseEntity<>(userDTOResponse, HttpStatus.ACCEPTED);
    }

    @GetMapping("user/{id}")
    public ResponseEntity<UserDTOResponse> findAccountById(@PathVariable final Long id){
        final User user = userService.findUserById(id);
        final UserDTOResponse userDTOResponse = UserHelper.convertToUserDTOResponse(user);
        return new ResponseEntity<>(userDTOResponse,HttpStatus.FOUND);
    }

    @GetMapping("users")
    public ResponseEntity<List<UserDTOResponse>> findAccounts(){
        final List<User> users = userService.findUsers();
        final List<UserDTOResponse> userDTOResponses = users.stream()
                .map(UserHelper::convertToUserDTOResponse)
                .toList();

        return new ResponseEntity<>(userDTOResponses,HttpStatus.FOUND);
    }

    @PutMapping("user")
    public ResponseEntity<UserDTOResponse> updateUser(@RequestBody final UserDTORequest userDTORequest){
        final User user = userService.createOrUpdateUser(userDTORequest);
        final UserDTOResponse userDTOResponse = UserHelper.convertToUserDTOResponse(user);
        return new ResponseEntity<>(userDTOResponse, HttpStatus.ACCEPTED);
    }

    @DeleteMapping("user/{id}")
    public ResponseEntity<Void> deleteUserById(@PathVariable final Long id){
        userService.deleteUser(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }

}
