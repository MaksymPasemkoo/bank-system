package com.ltp.banksystem.utils;

import lombok.Getter;

@Getter
public enum AccountType {
    CHECKING("Checking account"),
    SAVING("Saving account"),
    BUSINESS("Business account"),
    LOAN("Loan account");

    private final String description;

    AccountType(String description) {
        this.description = description;
    }

}
