package com.ltp.banksystem.model;

import com.ltp.banksystem.utils.AccountType;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;


@Entity
@Data
@NoArgsConstructor
public class Account {
    @Id
    private Long accountId;
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
    private AccountType accountType;

    public Account(final User user, final AccountType accountType) {
        this.user = user;
        this.accountType = accountType;
    }
}
