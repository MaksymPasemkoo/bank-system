package com.ltp.banksystem.dto.dtoresponce;

import com.ltp.banksystem.model.Account;
import com.ltp.banksystem.model.enums.TransactionType;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDate;

@AllArgsConstructor
@Getter
public class TransactionDTOResponse {
    private final Long transactionId;
    private final TransactionType transactionType;
    private final BigDecimal amount;
    private final LocalDate timestamp;
    private final Account accountFrom;
    private final Account accountTo;
}

