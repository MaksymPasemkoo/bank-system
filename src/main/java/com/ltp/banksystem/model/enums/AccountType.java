package com.ltp.banksystem.model.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum AccountType {
    CHECKING("Checking account"),
    SAVING("Saving account"),
    BUSINESS("Business account"),
    LOAN("Loan account");

    private final String description;


}
