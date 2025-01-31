package com.ltp.banksystem.repository;

import com.ltp.banksystem.model.Account;
import com.ltp.banksystem.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AccountRepository extends JpaRepository<Account,Long> {
    Account findByUser(User user);
}
