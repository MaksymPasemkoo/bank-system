package com.ltp.banksystem.service;

import com.ltp.banksystem.dto.dtorequest.*;
import com.ltp.banksystem.dto.dtoresponce.TransactionDTOResponse;
import com.ltp.banksystem.exception.PermissionException;
import com.ltp.banksystem.exception.TransactionException;
import com.ltp.banksystem.model.Account;
import com.ltp.banksystem.model.Transaction;
import com.ltp.banksystem.model.User;
import com.ltp.banksystem.repository.AccountRepository;
import com.ltp.banksystem.repository.TransactionRepository;
import com.ltp.banksystem.repository.UserRepository;
import com.ltp.banksystem.utils.TransactionHelper;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.NoSuchElementException;

import static com.ltp.banksystem.model.enums.Role.ADMIN;
import static com.ltp.banksystem.model.enums.TransactionType.*;

@Service
@RequiredArgsConstructor
public class TransactionService {
    private final TransactionRepository transactionRepository;
    private final AccountRepository accountRepository;
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    public TransactionDTOResponse deposit(final DepositRequest request) {
        final Account account = getAccount(request.getAccountId(), request.getPassword());
        final Account bankAccount = getBankAccount();

        if (account.getBalance().compareTo(request.getAmount()) < 0) {
            throw new TransactionException("You do not have enough money.");
        }
        account.setBalance(account.getBalance().subtract(request.getAmount()));
        bankAccount.setBalance(bankAccount.getBalance().add(request.getAmount()));

        final Transaction transaction = new Transaction(DEPOSIT, request.getAmount(),
                LocalDate.now(), bankAccount, account);
        transactionRepository.save(transaction);

        return TransactionHelper.convertToTransactionDTOResponse(transaction);
    }

    public TransactionDTOResponse withdrawFromDeposit(final WithdrawFromDepositRequest request) {
        final Account account = getAccount(request.getAccountId(), request.getPassword());
        final Account bankAccount = getBankAccount();
        final Long transactionId = request.getTransactionId();

        final Transaction existingTransaction = transactionRepository.findById(transactionId)
                .orElseThrow(() -> new NoSuchElementException("There is no transaction with this id."));

        if (existingTransaction.getTransactionType() != DEPOSIT) {
            throw new TransactionException("This transaction is not deposit. ");
        }

        final LocalDate timestamp = existingTransaction.getTimestamp();
        final LocalDate today = LocalDate.now();
        final BigDecimal dateDifference = BigDecimal.valueOf(ChronoUnit.DAYS.between(timestamp, today));

        final BigDecimal transferAmount = transferCalculation(dateDifference, existingTransaction);

        if (bankAccount.getBalance().compareTo(transferAmount) < 0) {
            throw new TransactionException("Something went wrong.Try again later.");
        }

        account.setBalance(account.getBalance().add(transferAmount));
        bankAccount.setBalance(bankAccount.getBalance().subtract(transferAmount));

        final Transaction transaction = new Transaction(WITHDRAW_FROM_DEPOSIT, transferAmount, LocalDate.now()
                , bankAccount, account);
        transactionRepository.save(transaction);

        return TransactionHelper.convertToTransactionDTOResponse(transaction);
    }


    public TransactionDTOResponse transfer(final TransferRequest request) {
        final Account fromAccount = getAccount(request.getAccountId(), request.getPassword());
        final Account toAccount = accountRepository.findById(request.getToAccountId())
                .orElseThrow(() -> new NoSuchElementException("There is no account with this id."));
        final BigDecimal amount = request.getAmount();

        if (fromAccount.getBalance().compareTo(amount) < 0) {
            throw new TransactionException("You do not have enough money.");
        }

        fromAccount.setBalance(fromAccount.getBalance().subtract(request.getAmount()));
        toAccount.setBalance(toAccount.getBalance().add(amount));

        final Transaction transaction = new Transaction(TRANSFER, amount, LocalDate.now(), fromAccount, toAccount);
        transactionRepository.save(transaction);

        return TransactionHelper.convertToTransactionDTOResponse(transaction);
    }

    public TransactionDTOResponse withdraw(final WithdrawRequest request) {
        final Account account = getAccount(request.getAccountId(), request.getPassword());
        final BigDecimal amount = request.getAmount();

        if (account.getBalance().compareTo(amount) < 0) {
            throw new TransactionException("You do not have enough money.");
        }

        account.setBalance(account.getBalance().subtract(amount));

        final Transaction transaction = new Transaction(WITHDRAW, amount, LocalDate.now(), account, null);
        transactionRepository.save(transaction);

        return TransactionHelper.convertToTransactionDTOResponse(transaction);
    }

    public List<TransactionDTOResponse> findAllTransactions() {
        final List<Transaction> transactions = transactionRepository.findAll();
        return transactions.stream()
                .map(TransactionHelper::convertToTransactionDTOResponse)
                .toList();
    }

    public TransactionDTOResponse findTransactionById(final Long id) {
        final Transaction transaction = transactionRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Transaction not found."));
        return TransactionHelper.convertToTransactionDTOResponse(transaction);
    }


    public void deleteTransactionById(final Long id) {
        final Transaction transaction = transactionRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Transaction not found."));
        transactionRepository.delete(transaction);
    }

    private static BigDecimal transferCalculation(final BigDecimal dateDifference, final Transaction transaction) {
        final BigDecimal daysInYear;
        if (LocalDate.now().isLeapYear()) {
            daysInYear = BigDecimal.valueOf(366);
        } else {
            daysInYear = BigDecimal.valueOf(365);
        }
        final BigDecimal annualInterestRate = new BigDecimal("0.17");
        final BigDecimal daysRate = dateDifference.multiply(annualInterestRate).divide(daysInYear);
        final BigDecimal investedMoney = transaction.getAmount();
        final BigDecimal incomeMoney = investedMoney.multiply(daysRate);

        return investedMoney.add(incomeMoney);
    }

    private Account getBankAccount() {
        final List<User> admins = userRepository.findAllByRole(ADMIN);
        final User admin = admins.stream()
                .findAny()
                .orElseThrow(() -> new NoSuchElementException("There is no admin available now.Try later."));
        return accountRepository.findByUser(admin);
    }

    private Account getAccount(final Long accountId, final String checkPassword) {
        final Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new NoSuchElementException("Account not found."));

        final String password = account.getUser().getPassword();

        if (!bCryptPasswordEncoder.matches(checkPassword, password)) {
            throw new PermissionException("Password not correct");
        }
        return account;
    }

}
