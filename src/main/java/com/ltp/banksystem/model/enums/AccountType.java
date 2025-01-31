package com.ltp.banksystem.model.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;


@Getter
@AllArgsConstructor
public enum AccountType {
    CHECKING("Checking account"),
    SAVING("Saving account"),
    BUSINESS("Business account"),
    LOAN("Loan account");

    private final String description;


}
