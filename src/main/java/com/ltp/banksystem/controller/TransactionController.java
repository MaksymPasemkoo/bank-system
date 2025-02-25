package com.ltp.banksystem.controller;

import com.ltp.banksystem.dto.dtorequest.DepositRequest;
import com.ltp.banksystem.dto.dtorequest.TransferRequest;
import com.ltp.banksystem.dto.dtorequest.WithdrawFromDepositRequest;
import com.ltp.banksystem.dto.dtorequest.WithdrawRequest;
import com.ltp.banksystem.dto.dtoresponce.TransactionDTOResponse;
import com.ltp.banksystem.model.Transaction;
import com.ltp.banksystem.service.TransactionService;
import com.ltp.banksystem.utils.TransactionHelper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/transaction")
public class TransactionController {
    private final TransactionService transactionService;

    @PreAuthorize("hasRole('USER')")
    @PostMapping("/deposit")
    public ResponseEntity<TransactionDTOResponse> deposit(@RequestBody final DepositRequest request) {
        final TransactionDTOResponse transactionDTOResponse = transactionService.deposit(request);
        return new ResponseEntity<>(transactionDTOResponse, HttpStatus.ACCEPTED);
    }

    @PreAuthorize("hasRole('USER')")
    @PostMapping("/withdraw-from-deposit")
    public ResponseEntity<TransactionDTOResponse> withdrawFromDeposit(@RequestBody final WithdrawFromDepositRequest request) {
        final TransactionDTOResponse transactionDTOResponse = transactionService.withdrawFromDeposit(request);
        return new ResponseEntity<>(transactionDTOResponse, HttpStatus.ACCEPTED);
    }

    @PreAuthorize("hasRole('USER')")
    @PostMapping("/transfer")
    public ResponseEntity<TransactionDTOResponse> transferMoney(@RequestBody final TransferRequest request) {
        final TransactionDTOResponse transactionDTOResponse = transactionService.transfer(request);
        return new ResponseEntity<>(transactionDTOResponse, HttpStatus.ACCEPTED);
    }

    @PreAuthorize("hasRole('USER')")
    @PostMapping("/withdraw")
    public ResponseEntity<TransactionDTOResponse> withdraw(@RequestBody final WithdrawRequest request) {
        final TransactionDTOResponse transactionDTOResponse = transactionService.withdraw(request);
        return new ResponseEntity<>(transactionDTOResponse, HttpStatus.ACCEPTED);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public ResponseEntity<List<TransactionDTOResponse>> findAllTransactions() {
        final List<TransactionDTOResponse> transactionDTOResponses = transactionService.findAllTransactions();
        return new ResponseEntity<>(transactionDTOResponses, HttpStatus.FOUND);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/{id}")
    public ResponseEntity<TransactionDTOResponse> findTransactionById(@PathVariable final Long id) {
        final TransactionDTOResponse transactionDTOResponse = transactionService.findTransactionById(id);
        return new ResponseEntity<>(transactionDTOResponse, HttpStatus.FOUND);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteTransactionById(@PathVariable final Long id) {
        transactionService.deleteTransactionById(id);
        return new ResponseEntity<>("Deleted", HttpStatus.OK);
    }
}
