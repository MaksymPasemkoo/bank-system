package com.ltp.banksystem.model;

import com.ltp.banksystem.model.enums.AccountType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;


@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Account {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long accountId;
    @ManyToOne
    @JoinColumn(name = "user_id")
    protected User user;
    private AccountType accountType;
    private BigDecimal balance;

    public Account(final User user, final AccountType accountType, final BigDecimal balance) {
        this.user = user;
        this.accountType = accountType;
        this.balance = balance;
    }
}
