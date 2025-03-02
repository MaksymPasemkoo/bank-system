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
import org.springframework.test.util.ReflectionTestUtils;

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
    void tearDown() {
        userRepository.deleteAll();
    }

    @Test
    void createOrUpdateShouldReturnUserDTOResponse() {
        //given
        final User expectedUser = new User(Role.ADMIN, "admin", "admin");
        ReflectionTestUtils.setField(expectedUser, "userId", 1L);

        final UserDTORequest userDTORequest = UserHelper.convertToUserDTORequest(expectedUser);
        final UserDTOResponse expectedUserDTOResponse = UserHelper.convertToUserDTOResponse(expectedUser);

        //mock the calls
        when(bCryptPasswordEncoder.encode(anyString())).thenReturn("admin");
        when(userRepository.save(any(User.class)))
                .thenAnswer(
                        invocationOnMock -> {
                            final User user = invocationOnMock.getArgument(0);
                            ReflectionTestUtils.setField(user, "userId", 1L);
                            return user;
                        }
                );

        //when
        final UserDTOResponse actualUserDTOResponse = userService.createOrUpdateUser(userDTORequest);

        //then
        assertThat(actualUserDTOResponse)
                .usingRecursiveComparison()
                .isEqualTo(expectedUserDTOResponse);

        verify(bCryptPasswordEncoder).encode(anyString());
        verify(userRepository).save(any(User.class));
    }

    @Test
    void findUserByIdShouldReturnUserDTOResponse() {
        //given
        final Long userId = 1L;
        final User expectedUser = new User(userId, Role.ADMIN, "admin", "admin");
        final UserDTOResponse expectedUserDtoResponse = UserHelper.convertToUserDTOResponse(expectedUser);

        //mock the calls
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(expectedUser));

        //when
        final UserDTOResponse actualUserDTOResponse = userService.findUserById(userId);

        //then
        assertThat(actualUserDTOResponse).isEqualTo(expectedUserDtoResponse);

        verify(userRepository, times(1)).findById(userId);
    }

    @Test
    void findUserByIdShouldThrowExceptionWhenUserDoesNotExist() {
        //given
        final Long userId = 1L;

        //mock the calls
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        //when + then
        assertThrows(NoSuchElementException.class, () -> userService.findUserById(userId));

        verify(userRepository, times(1)).findById(eq(1L));
    }

    @Test
    void findUsersShouldReturnAllUserDtoResponses() {
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

        verify(userRepository, times(1)).findAll();
    }

    @Test
    void deleteUserByIdShouldReturnTrueAndDeleteUser() {
        //given
        final Long userId = 1L;

        //mock the calls
        when(userRepository.existsById(anyLong())).thenReturn(true);

        //when
        final boolean isDeleted = userService.deleteUserById(userId);

        //then
        assertTrue(isDeleted);

        verify(userRepository, times(1)).existsById(eq(userId));
    }

    @Test
    void deleteUserByIdShouldReturnFalseWhenUserDoesNotExist() {
        //given
        final Long userId = 1L;

        //mock the calls
        when(userRepository.existsById(anyLong())).thenReturn(false);

        //when
        final boolean isDeleted = userService.deleteUserById(userId);

        assertFalse(isDeleted);

        verify(userRepository, times(1)).existsById(eq(userId));
    }

    @Test
    void deleteUserShouldReturnTrueAndDeleteIfExists() {
        //given
        final String username = "someone";
        final String password = "something";
        final UserCredentials userCredentials = new UserCredentials(username, password);
        final User user = new User(1L, Role.ADMIN, username, password);

        //mock the calls
        when(userRepository.findByUsername(anyString())).thenReturn(user);
        when(userRepository.existsUsersByUsername(anyString())).thenReturn(true);
        when(bCryptPasswordEncoder.matches(password, user.getPassword())).thenReturn(true);

        //when
        final boolean isDeleted = userService.deleteUser(userCredentials);

        //then
        assertTrue(isDeleted);

        verify(userRepository, times(1)).findByUsername(eq(username));
        verify(bCryptPasswordEncoder, times(1)).matches(password, user.getPassword());
        verify(userRepository, times(1)).existsUsersByUsername(username);

    }

    @Test
    void deleteUserShouldReturnFalseWhenUsernameDoesNotExist() {
        //given
        final String username = "someone";
        final String password = "something";
        final UserCredentials userCredentials = new UserCredentials(username, password);
        final User user = new User(1L, Role.ADMIN, username, password);

        //mock the calls
        when(userRepository.findByUsername(anyString())).thenReturn(null);

        //when
        final boolean isDeleted = userService.deleteUser(userCredentials);

        //then
        assertFalse(isDeleted);

        verify(userRepository, times(1)).findByUsername(username);
        verify(userRepository, times(1)).existsUsersByUsername(username);
        verify(bCryptPasswordEncoder, times(0)).matches(password, user.getPassword());
    }

    @Test
    void deleteUserShouldThrowExceptionWhenPasswordIncorrect() {
        //given
        final String username = "someone";
        final String password = "something";
        final UserCredentials userCredentials = new UserCredentials(username, password);
        final User user = new User(1L, Role.ADMIN, username, password);

        //mock the calls
        when(userRepository.findByUsername(anyString())).thenReturn(user);
        when(userRepository.existsUsersByUsername(anyString())).thenReturn(true);
        when(bCryptPasswordEncoder.matches(password, user.getPassword())).thenReturn(false);

        //when + then
        assertThrows(PermissionException.class, () -> userService.deleteUser(userCredentials));

        verify(userRepository, times(1)).findByUsername(eq(username));
        verify(userRepository, times(1)).existsUsersByUsername(eq(username));
        verify(bCryptPasswordEncoder, times(1)).matches(password, user.getPassword());
    }


}