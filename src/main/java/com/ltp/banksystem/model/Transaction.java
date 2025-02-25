package com.ltp.banksystem.model;

import com.ltp.banksystem.model.enums.TransactionType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Transaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long transactionId;
    private TransactionType transactionType;
    private BigDecimal amount;
    private LocalDate timestamp;
    @ManyToOne
    @JoinColumn(name = "from_account_id")
    private Account accountFrom;
    @ManyToOne
    @JoinColumn(name = "to_account_id")
    private Account accountTo;

    public Transaction(TransactionType transactionType
            , BigDecimal amount, LocalDate timestamp, Account accountFrom, Account accountTo) {
        this.transactionType = transactionType;
        this.amount = amount;
        this.timestamp = timestamp;
        this.accountFrom = accountFrom;
        this.accountTo = accountTo;
    }
}
