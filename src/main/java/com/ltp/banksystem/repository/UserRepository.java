package com.ltp.banksystem.repository;

import com.ltp.banksystem.model.User;
import com.ltp.banksystem.model.enums.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    User findByUsername(String username);

    List<User> findAllByRole(Role role);

    boolean existsUsersByUsername(String username);
}
