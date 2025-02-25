package com.ltp.banksystem.service;

import com.ltp.banksystem.dto.dtorequest.UserCredentials;
import com.ltp.banksystem.dto.dtorequest.UserDTORequest;
import com.ltp.banksystem.dto.dtoresponce.UserDTOResponse;
import com.ltp.banksystem.exception.PermissionException;
import com.ltp.banksystem.model.User;
import com.ltp.banksystem.model.enums.Role;
import com.ltp.banksystem.repository.UserRepository;
import com.ltp.banksystem.utils.UserHelper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    public UserDTOResponse createOrUpdateUser(final UserDTORequest userDTORequest) {
        final Role role = userDTORequest.getRole();
        final String userName = userDTORequest.getUsername();
        final String password = userDTORequest.getPassword();

        final User user = new User(role, userName, password);
        user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
        userRepository.save(user);

        return UserHelper.convertToUserDTOResponse(user);
    }

    public UserDTOResponse findUserById(final Long id) {
        final User user = userRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("User not found."));
        return UserHelper.convertToUserDTOResponse(user);
    }

    public List<UserDTOResponse> findUsers() {
        final List<User> users = userRepository.findAll();
        return users.stream()
                .map(UserHelper::convertToUserDTOResponse)
                .toList();
    }

    public void deleteUserById(final Long id) {
        final User user = userRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("User not found."));
        userRepository.delete(user);
    }


    public void deleteUser(final UserCredentials userCredentials) {
        final String username = userCredentials.getUsername();
        final String checkPassword = userCredentials.getPassword();

        final User user = userRepository.findByUsername(username);
        if (user == null) throw new UsernameNotFoundException("User not found.");

        final String password = user.getPassword();

        if (!bCryptPasswordEncoder.matches(checkPassword, password)) {
            throw new PermissionException("Password not correct.");
        }
        userRepository.delete(user);
    }
}
