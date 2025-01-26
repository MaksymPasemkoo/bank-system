package com.ltp.banksystem.service;

import com.ltp.banksystem.dto.dtorequest.UserDTORequest;
import com.ltp.banksystem.exception.PermissionException;
import com.ltp.banksystem.model.User;
import com.ltp.banksystem.model.enums.Role;
import com.ltp.banksystem.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.NoSuchElementException;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    public User createOrUpdateUser(final UserDTORequest userDTORequest){
        final Role role = userDTORequest.getRole();
        final String userName = userDTORequest.getUsername();
        final String password = userDTORequest.getPassword();

        final User user = new User(role,userName,password);
        user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
        userRepository.save(user);
        return user;
    }

    public User findUserById(final Long id){
        return userRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("User is not found."));
    }

    public List<User> findUsers(){
        return userRepository.findAll();
    }

    public void deleteUserById(final Long id){
        final User user = userRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("User is not found."));
        userRepository.delete(user);
    }


    public void deleteUser(final String username,final String password){
        final User user = userRepository.findByUsername(username);
        if(user == null) throw new UsernameNotFoundException("User is not found.");

        if(!bCryptPasswordEncoder.matches(password,user.getPassword())){
            throw new PermissionException("Password is not correct.");
        }
        userRepository.delete(user);
    }
}
