package com.ltp.banksystem.repository;

import com.ltp.banksystem.model.Account;
import com.ltp.banksystem.model.User;
import com.ltp.banksystem.model.enums.AccountType;
import com.ltp.banksystem.model.enums.Role;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;


@DataJpaTest
class AccountRepositoryTest {
    @Autowired
    private AccountRepository accountRepository;
    @Autowired
    private UserRepository userRepository;

    @AfterEach
    void tearDown(){
        accountRepository.deleteAll();
        userRepository.deleteAll();
    }
    @Test
    void shouldReturnAccountsWhenAccountTypeIsBusiness() {
        final User user1 = new User(Role.ADMIN, "admin", "admin");
        final User user2 = new User(Role.ADMIN, "fja", "fjadjfa");
        final User user3 = new User(Role.USER, "jrijqo", "vvo");
        final User user4 = new User(Role.USER, "kfajj", "jfhiuhqpi");

        final List<User> users = List.of(user1, user2, user3, user4);
        userRepository.saveAll(users);

        final Account account1 = new Account(user1, AccountType.BUSINESS, BigDecimal.valueOf(10033320));
        final Account account2 = new Account(user2, AccountType.BUSINESS, BigDecimal.valueOf(1033300));
        final Account account3 = new Account(user3, AccountType.CHECKING, BigDecimal.valueOf(1000));
        final Account account4 = new Account(user4, AccountType.SAVING, BigDecimal.valueOf(1000));

        final List<Account> accounts = List.of(account1,account2,account3,account4);
        accountRepository.saveAll(accounts);

        final List<Account> expectedAccount = List.of(account1,account2);
        final List<Account> actualAccounts = accountRepository.findAccountsByAccountType(AccountType.BUSINESS);

        assertThat(actualAccounts).isEqualTo(expectedAccount);

    }

    @Test
    void findAccountByUser(){
        final User user = new User(Role.ADMIN, "admin", "admin");
        userRepository.save(user);

        final Account expectedAccount = new Account(user, AccountType.BUSINESS, BigDecimal.valueOf(1000));
        accountRepository.save(expectedAccount);

        final Account actualAccount = accountRepository.findByUser(user);

        assertThat(actualAccount).isEqualTo(expectedAccount);
    }
}