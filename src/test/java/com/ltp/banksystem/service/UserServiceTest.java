package com.ltp.banksystem.service;


import com.ltp.banksystem.dto.dtorequest.UserCredentials;
import com.ltp.banksystem.dto.dtorequest.UserDTORequest;
import com.ltp.banksystem.dto.dtoresponce.UserDTOResponse;
import com.ltp.banksystem.exception.PermissionException;
import com.ltp.banksystem.model.User;
import com.ltp.banksystem.model.enums.Role;
import com.ltp.banksystem.repository.UserRepository;
import com.ltp.banksystem.utils.UserHelper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class UserServiceTest {
    @InjectMocks
    private UserService userService;
    @Mock
    private UserRepository userRepository;
    @Mock
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @AfterEach
    void tearDown(){
        userRepository.deleteAll();
    }

    @Test
    void shouldCreateOrUpdateStudent() {
        //given
        final User user = new User(Role.ADMIN, "admin", "admin");
        final UserDTORequest userDTORequest = UserHelper.convertToUserDTORequest(user);

        //mock the calls
        when(bCryptPasswordEncoder.encode(Mockito.anyString())).thenReturn("encodePassword");
        when(userRepository.save(Mockito.any(User.class))).thenReturn(user);

        //when
        final UserDTOResponse savedUserDTOResponse = userService.createOrUpdateUser(userDTORequest);

        //then
        assertThat(savedUserDTOResponse).isNotNull();
        assertThat(savedUserDTOResponse.getUsername()).isEqualTo(user.getUsername());
        assertThat(savedUserDTOResponse.getRole()).isEqualTo(user.getRole());

        verify(bCryptPasswordEncoder).encode(user.getPassword());
        verify(userRepository).save(Mockito.any(User.class));
    }

    @Test
    void shouldFindUserById() {
        //given
        final Long userId = 1L;
        final User expectedUser = new User(userId,Role.ADMIN, "admin", "admin");
        final UserDTOResponse expectedUserDtoResponse = UserHelper.convertToUserDTOResponse(expectedUser);

        //mock the calls
        when(userRepository.findById(userId)).thenReturn(Optional.of(expectedUser));

        //when
        final UserDTOResponse actualUserDTOResponse = userService.findUserById(userId);

        //then
        assertThat(actualUserDTOResponse).isEqualTo(expectedUserDtoResponse);
        assertThrows(NoSuchElementException.class, () -> userService.findUserById(null));

        verify(userRepository,times(1)).findById(userId);
    }

    @Test
    void shouldReturnAllUsersAsUserDtoResponse() {
        //given
        final List<UserDTOResponse> expectedUserDTOResponses = List.of(
                new UserDTOResponse(1L, Role.USER, "oleg", "oleg"),
                new UserDTOResponse(2L, Role.USER, "someone", "something"),
                new UserDTOResponse(3L, Role.ADMIN, "maksym", "rqhef"),
                new UserDTOResponse(4L, Role.USER, "oleg", "someone")
        );

        final List<User> users = expectedUserDTOResponses.stream()
                .map(UserHelper::convertToUser)
                .toList();

        //mock the calls
        when(userRepository.findAll()).thenReturn(users);

        //when
        final List<UserDTOResponse> actualUserDTOResponses = userService.findUsers();

        //then
        assertThat(actualUserDTOResponses).isEqualTo(expectedUserDTOResponses);
        verify(userRepository,times(1)).findAll();
    }

    @Test
    void shouldDeleteUserById() {
        //given
        final Long userId = 1L;

        //mock the calls
        when(userRepository.existsById(userId)).thenReturn(true);

        //when
        final boolean isDeleted = userService.deleteUserById(userId);

        //then
        assertTrue(isDeleted);

        verify(userRepository,times(1)).existsById(userId);
    }

    @Test
    void shouldNotDeleteUserById(){
        //given
        final Long userId = 2L;

        //mock the calls
        when(userRepository.existsById(userId)).thenReturn(false);

        //when
        final boolean isDeleted = userService.deleteUserById(userId);

        assertFalse(isDeleted);

        verify(userRepository,times(1)).existsById(userId);
    }

    @Test
    void shouldDeleteUserByCredentials() {
        //given
        final String username = "someone";
        final String password = "something";
        final UserCredentials userCredentials = new UserCredentials(username,password);
        final User user = new User(1L,Role.ADMIN,username,password);

        //mock the calls
        when(userRepository.findByUsername(username)).thenReturn(user);
        when(userRepository.existsUsersByUsername(username)).thenReturn(true);
        when(bCryptPasswordEncoder.matches(password,user.getPassword())).thenReturn(true);

        //when
        final boolean isDeleted = userService.deleteUser(userCredentials);

        //then
        assertTrue(isDeleted);

        verify(userRepository,times(1)).findByUsername(username);
        verify(bCryptPasswordEncoder,times(1)).matches(password,user.getPassword());
        verify(userRepository,times(1)).existsUsersByUsername(username);

    }

    @Test
    void shouldNotDeleteUserByCredentialsWhenUsernameDoesNotExist() {
        //given
        final String username = "someone";
        final String password = "something";
        final UserCredentials userCredentials = new UserCredentials(username,password);
        final User user = new User(1L,Role.ADMIN,username,password);

        //mock the calls
        when(userRepository.findByUsername(username)).thenReturn(null);

        //when
        final boolean isDeleted = userService.deleteUser(userCredentials);

        //then
        assertFalse(isDeleted);

        verify(userRepository,times(1)).findByUsername(username);
        verify(userRepository,times(1)).existsUsersByUsername(username);
        verify(bCryptPasswordEncoder,times(0)).matches(password,user.getPassword());
    }

    @Test
    void shouldNotDeleteUserByCredentialsWhenPasswordIncorrect() {
        //given
        final String username = "someone";
        final String password = "something";
        final UserCredentials userCredentials = new UserCredentials(username,password);
        final User user = new User(1L,Role.ADMIN,username,password);

        //mock the calls
        when(userRepository.findByUsername(username)).thenReturn(user);
        when(userRepository.existsUsersByUsername(username)).thenReturn(true);
        when(bCryptPasswordEncoder.matches(password,user.getPassword())).thenReturn(false);

        //when + then
        assertThrows(PermissionException.class,() -> userService.deleteUser(userCredentials));

        verify(userRepository,times(1)).findByUsername(username);
        verify(userRepository,times(1)).existsUsersByUsername(username);
        verify(bCryptPasswordEncoder,times(1)).matches(password,user.getPassword());
    }


}