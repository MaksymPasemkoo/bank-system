package com.ltp.banksystem.controller;

import com.ltp.banksystem.dto.dtorequest.UserCredentials;
import com.ltp.banksystem.dto.dtorequest.UserDTORequest;
import com.ltp.banksystem.dto.dtoresponce.UserDTOResponse;
import com.ltp.banksystem.service.AuthenticationService;
import com.ltp.banksystem.service.UserService;
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
    private final AuthenticationService authenticationService;

    @PostMapping("/register")
    public ResponseEntity<UserDTOResponse> register(@RequestBody final UserDTORequest userDTORequest) {
        final UserDTOResponse userDTOResponse = userService.createOrUpdateUser(userDTORequest);
        return ResponseEntity.accepted().body(userDTOResponse);
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody final UserDTORequest userDTORequest) {
        final String jwtToken = authenticationService.login(userDTORequest);
        return ResponseEntity.ok(jwtToken);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/{id}")
    public ResponseEntity<UserDTOResponse> findUserById(@PathVariable final Long id) {
        final UserDTOResponse userDTOResponse = userService.findUserById(id);
        return ResponseEntity.status(HttpStatus.FOUND).body(userDTOResponse);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public ResponseEntity<List<UserDTOResponse>> findUsers() {
        final List<UserDTOResponse> userDTOResponses = userService.findUsers();
        return ResponseEntity.status(HttpStatus.FOUND).body(userDTOResponses);
    }

    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    @PutMapping
    public ResponseEntity<UserDTOResponse> updateUser(@RequestBody final UserDTORequest userDTORequest) {
        final UserDTOResponse userDTOResponse = userService.createOrUpdateUser(userDTORequest);
        return ResponseEntity.accepted().body(userDTOResponse);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUserById(@PathVariable final Long id) {
        final boolean isDeleted = userService.deleteUserById(id);
        return isDeleted ? ResponseEntity.ok().build()
                : ResponseEntity.notFound().build();
    }

    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    @DeleteMapping
    public ResponseEntity<Void> deleteUser(@RequestBody UserCredentials userCredentials) {
        final boolean isDeleted = userService.deleteUser(userCredentials);
        return isDeleted ? ResponseEntity.ok().build()
                : ResponseEntity.notFound().build();

    }

}
