package com.ltp.banksystem.utils;

import com.ltp.banksystem.dto.dtoresponce.TransactionDTOResponse;
import com.ltp.banksystem.model.Account;
import com.ltp.banksystem.model.Transaction;
import com.ltp.banksystem.model.enums.TransactionType;

import java.math.BigDecimal;
import java.time.LocalDate;

public class TransactionHelper {

    public static TransactionDTOResponse convertToTransactionDTOResponse(final Transaction transaction){
        final Long transactionId = transaction.getTransactionId();
        final TransactionType transactionType = transaction.getTransactionType();
        final BigDecimal amount = transaction.getAmount();
        final LocalDate timestamp = transaction.getTimestamp();
        final Account accountFrom = transaction.getAccountFrom();
        final Account accountTo = transaction.getAccountTo();
        return new TransactionDTOResponse(transactionId,transactionType,amount,timestamp,accountFrom,accountTo);
    }


}
