package com.ltp.banksystem.service;


import com.ltp.banksystem.dto.dtorequest.UserDTORequest;
import com.ltp.banksystem.dto.dtoresponce.UserDTOResponse;
import com.ltp.banksystem.model.User;
import com.ltp.banksystem.model.enums.Role;
import com.ltp.banksystem.repository.UserRepository;
import com.ltp.banksystem.utils.UserHelper;
import org.junit.jupiter.api.Disabled;
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
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class UserServiceTest {
    @InjectMocks
    private UserService userService;
    @Mock
    private UserRepository userRepository;
    @Mock
    private BCryptPasswordEncoder bCryptPasswordEncoder;

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
        verify(userRepository,times(1)).findById(userId);

        assertThrows(NoSuchElementException.class, () -> userService.findUserById(null));

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
    @Disabled
    void deleteUserById() {
    }

    @Test
    @Disabled
    void deleteUser() {
    }
}