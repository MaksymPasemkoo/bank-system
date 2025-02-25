package com.ltp.banksystem.controller;

import com.ltp.banksystem.dto.dtorequest.UserCredentials;
import com.ltp.banksystem.dto.dtorequest.UserDTORequest;
import com.ltp.banksystem.dto.dtoresponce.UserDTOResponse;
import com.ltp.banksystem.model.User;
import com.ltp.banksystem.service.UserService;
import com.ltp.banksystem.utils.UserHelper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/user")
public class UserController {
    private final UserService userService;

    @PostMapping
    public ResponseEntity<UserDTOResponse> createUser(@RequestBody final UserDTORequest userDTORequest) {
        final UserDTOResponse userDTOResponse = userService.createOrUpdateUser(userDTORequest);
        return new ResponseEntity<>(userDTOResponse, HttpStatus.ACCEPTED);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/{id}")
    public ResponseEntity<UserDTOResponse> findUserById(@PathVariable final Long id) {
        final UserDTOResponse userDTOResponse = userService.findUserById(id);
        return new ResponseEntity<>(userDTOResponse, HttpStatus.FOUND);
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping
    public ResponseEntity<List<UserDTOResponse>> findUsers() {
        final List<UserDTOResponse> userDTOResponses = userService.findUsers();
        return new ResponseEntity<>(userDTOResponses, HttpStatus.FOUND);
    }

    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    @PutMapping
    public ResponseEntity<UserDTOResponse> updateUser(@RequestBody final UserDTORequest userDTORequest) {
        final UserDTOResponse userDTOResponse = userService.createOrUpdateUser(userDTORequest);
        return new ResponseEntity<>(userDTOResponse, HttpStatus.ACCEPTED);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteUserById(@PathVariable final Long id) {
        userService.deleteUserById(id);
        return new ResponseEntity<>("Deleted", HttpStatus.OK);
    }

    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    @DeleteMapping
    public ResponseEntity<String> deleteUser(@RequestBody UserCredentials userCredentials) {
        userService.deleteUser(userCredentials);
        return new ResponseEntity<>("Deleted", HttpStatus.OK);
    }

}
