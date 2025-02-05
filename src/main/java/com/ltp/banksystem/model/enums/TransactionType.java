package com.ltp.banksystem.model.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum TransactionType {
    DEPOSIT("Deposit"),
    WITHDRAW("Withdraw"),
    TRANSFER("Transfer"),
    WITHDRAW_FROM_DEPOSIT("Take money from deposit");

    private final String description;
}
