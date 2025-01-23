package com.ltp.banksystem.service;

import com.ltp.banksystem.dto.dtorequest.UserDTORequest;
import com.ltp.banksystem.model.User;
import com.ltp.banksystem.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    public User createOrUpdateUser(final UserDTORequest userDTORequest){
        final String userName = userDTORequest.getUsername();
        final String password = userDTORequest.getPassword();

        final User user = new User(userName,password);
        user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
        userRepository.save(user);
        return user;
    }

    public User findUserById(final Long id){
        return userRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Resource not found."));
    }

    public List<User> findUsers(){
        return userRepository.findAll();
    }

    public void deleteUser(final Long id){
        final User user = userRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Resource not found."));
        userRepository.delete(user);
    }
}
