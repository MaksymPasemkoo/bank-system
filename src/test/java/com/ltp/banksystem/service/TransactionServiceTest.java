package com.ltp.banksystem.service;

import com.ltp.banksystem.dto.dtorequest.DepositRequest;
import com.ltp.banksystem.dto.dtoresponce.TransactionDTOResponse;
import com.ltp.banksystem.exception.PermissionException;
import com.ltp.banksystem.model.Account;
import com.ltp.banksystem.model.Transaction;
import com.ltp.banksystem.model.User;
import com.ltp.banksystem.model.enums.AccountType;
import com.ltp.banksystem.model.enums.Role;
import com.ltp.banksystem.repository.AccountRepository;
import com.ltp.banksystem.repository.TransactionRepository;
import com.ltp.banksystem.repository.UserRepository;
import com.ltp.banksystem.utils.TransactionHelper;
import com.ltp.banksystem.exception.TransactionException;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static com.ltp.banksystem.model.enums.TransactionType.DEPOSIT;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TransactionServiceTest {

    @InjectMocks
    private TransactionService transactionService;
    @Mock
    private TransactionRepository transactionRepository;
    @Mock
    private AccountRepository accountRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Test
    void depositShouldReturnTransactionDTOResponse() {
        //given
        final DepositRequest depositRequest = new DepositRequest(2L, "rgiew", BigDecimal.valueOf(111));
        final Account account = new Account(2L, new User(1L, Role.USER, "jjvo"
                , "rgiew"),
                AccountType.CHECKING,
                BigDecimal.valueOf(13441));

        final Account bankAccount = new Account(1L, new User(1L, Role.ADMIN, "jdfal"
                , "fjslfj"),
                AccountType.BUSINESS,
                BigDecimal.valueOf(13441));

        final Transaction expectedTransaction = new Transaction(DEPOSIT, depositRequest.getAmount(),
                LocalDate.now(), bankAccount, account);
        ReflectionTestUtils.setField(expectedTransaction, "transactionId", 1L);

        final TransactionDTOResponse expectedTransactionDTOResponse = TransactionHelper
                .convertToTransactionDTOResponse(expectedTransaction);

        //mock the calls
        when(accountRepository.findById(eq(account.getAccountId()))).thenReturn(Optional.of(account));
        when(bCryptPasswordEncoder.matches(eq(depositRequest.getPassword()), anyString()))
                .thenReturn(true);
        when(userRepository.findAllByRole(eq(Role.ADMIN))).thenReturn(List.of(bankAccount.getUser()));
        when(accountRepository.findByUser(eq(bankAccount.getUser()))).thenReturn(bankAccount);
        when(transactionRepository.save(any(Transaction.class)))
                .thenAnswer(
                        invocationOnMock -> {
                            final Transaction transaction = invocationOnMock.getArgument(0);
                            ReflectionTestUtils.setField(transaction, "transactionId", 1L);
                            return transaction;
                        }
                );

        //when
        final TransactionDTOResponse actualTransactionDTOResponse = transactionService.deposit(depositRequest);

        //then
        assertThat(actualTransactionDTOResponse)
                .usingRecursiveComparison()
                .isEqualTo(expectedTransactionDTOResponse);

        verify(accountRepository, times(1)).findById(eq(account.getAccountId()));
        verify(bCryptPasswordEncoder, times(1))
                .matches(eq(depositRequest.getPassword()), anyString());
        verify(userRepository, times(1)).findAllByRole(eq(Role.ADMIN));
        verify(accountRepository, times(1)).findByUser(eq(bankAccount.getUser()));
        verify(transactionRepository, times(1)).save(any(Transaction.class));
    }

    @Test
    void depositShouldThrowExceptionWhenAccountNotFoundById() {
        //given
        final DepositRequest depositRequest = new DepositRequest(2L, "rgiew", BigDecimal.valueOf(111));

        //mock the calls
        when(accountRepository.findById(eq(depositRequest.getAccountId()))).thenReturn(Optional.empty());

        //when + then
        assertThrows(NoSuchElementException.class, () -> transactionService.deposit(depositRequest));

        verify(accountRepository, times(1)).findById(eq(depositRequest.getAccountId()));

        verifyNoInteractions(bCryptPasswordEncoder, userRepository, transactionRepository);
    }

    @Test
    void depositShouldThrowExceptionWhenAccountPasswordIsNotCorrect() {
        //given
        final DepositRequest depositRequest = new DepositRequest(2L, "rgiew", BigDecimal.valueOf(111));
        final Account account = new Account(2L, new User(1L, Role.USER, "jjvo"
                , "rgiew"),
                AccountType.CHECKING,
                BigDecimal.valueOf(13441));

        //mock the calls
        when(accountRepository.findById(eq(depositRequest.getAccountId()))).thenReturn(Optional.of(account));
        when(bCryptPasswordEncoder.matches(eq(depositRequest.getPassword()), eq(account.getUser().getPassword())))
                .thenReturn(false);

        //when + then
        assertThrows(PermissionException.class, () -> transactionService.deposit(depositRequest));

        verify(accountRepository, times(1)).findById(eq(depositRequest.getAccountId()));
        verify(bCryptPasswordEncoder, times(1))
                .matches(eq(depositRequest.getPassword()), eq(account.getUser().getPassword()));
        verifyNoInteractions(userRepository, transactionRepository);
    }

    @Test
    void depositShouldThrowExceptionWhenAdminNotFound() {
        //given
        final DepositRequest depositRequest = new DepositRequest(2L, "rgiew", BigDecimal.valueOf(111));

        final Account account = new Account(2L, new User(1L, Role.USER, "jjvo"
                , "rgiew"),
                AccountType.CHECKING,
                BigDecimal.valueOf(13441));

        final Account bankAccount = new Account(1L, new User(1L, Role.ADMIN, "jdfal"
                , "fjslfj"),
                AccountType.BUSINESS,
                BigDecimal.valueOf(13441));


        //mock the calls
        when(accountRepository.findById(eq(depositRequest.getAccountId()))).thenReturn(Optional.of(account));
        when(bCryptPasswordEncoder.matches(eq(depositRequest.getPassword()), eq(account.getUser().getPassword())))
                .thenReturn(true);
        when(userRepository.findAllByRole(eq(Role.ADMIN))).thenReturn(Collections.emptyList());


        //when + then
        assertThrows(NoSuchElementException.class, () -> transactionService.deposit(depositRequest));

        verify(accountRepository, times(1)).findById(eq(depositRequest.getAccountId()));
        verify(bCryptPasswordEncoder, times(1))
                .matches(eq(depositRequest.getPassword()), eq(account.getUser().getPassword()));
        verify(userRepository, times(1)).findAllByRole(eq(Role.ADMIN));
        verifyNoInteractions(transactionRepository);
    }

    @Test
    void depositShouldThrowExceptionWhenAccountDoNotHaveEnoughMoney() {
        //given
        final DepositRequest depositRequest = new DepositRequest(2L, "rgiew", BigDecimal.valueOf(1111));
        final Account account = new Account(2L, new User(1L, Role.USER, "jjvo"
                , "rgiew"),
                AccountType.CHECKING,
                BigDecimal.valueOf(134));

        final Account bankAccount = new Account(1L, new User(1L, Role.ADMIN, "jdfal"
                , "fjslfj"),
                AccountType.BUSINESS,
                BigDecimal.valueOf(13441));


        //mock the calls
        when(accountRepository.findById(eq(account.getAccountId()))).thenReturn(Optional.of(account));
        when(bCryptPasswordEncoder.matches(eq(depositRequest.getPassword()), anyString()))
                .thenReturn(true);
        when(userRepository.findAllByRole(eq(Role.ADMIN))).thenReturn(List.of(bankAccount.getUser()));
        when(accountRepository.findByUser(eq(bankAccount.getUser()))).thenReturn(bankAccount);


        //when + then
        assertThrows(TransactionException.class, () -> transactionService.deposit(depositRequest));

        verify(accountRepository, times(1)).findById(eq(account.getAccountId()));
        verify(bCryptPasswordEncoder, times(1))
                .matches(eq(depositRequest.getPassword()), anyString());
        verify(userRepository, times(1)).findAllByRole(eq(Role.ADMIN));
        verify(accountRepository, times(1)).findByUser(eq(bankAccount.getUser()));
        verifyNoInteractions(transactionRepository);
    }

    @Test
    void depositShouldIncreaseBankAccountBalanceAndDecreaseAccountBalance() {
        //given
        final DepositRequest depositRequest = new DepositRequest(2L, "rgiew", BigDecimal.valueOf(111));
        final BigDecimal accountBalanceBeforeTransaction = BigDecimal.valueOf(13111);
        final Account account = new Account(2L, new User(1L, Role.USER, "jjvo"
                , "rgiew"),
                AccountType.CHECKING,
                accountBalanceBeforeTransaction);

        final BigDecimal bankAccountBalanceBeforeTransaction = BigDecimal.valueOf(13000);
        final Account bankAccount = new Account(1L, new User(1L, Role.ADMIN, "jdfal"
                , "fjslfj"),
                AccountType.BUSINESS,
                bankAccountBalanceBeforeTransaction);

        final Transaction expectedTransaction = new Transaction(DEPOSIT, depositRequest.getAmount(),
                LocalDate.now(), bankAccount, account);
        ReflectionTestUtils.setField(expectedTransaction, "transactionId", 1L);


        //mock the calls
        when(accountRepository.findById(eq(account.getAccountId()))).thenReturn(Optional.of(account));
        when(bCryptPasswordEncoder.matches(eq(depositRequest.getPassword()), anyString()))
                .thenReturn(true);
        when(userRepository.findAllByRole(eq(Role.ADMIN))).thenReturn(List.of(bankAccount.getUser()));
        when(accountRepository.findByUser(eq(bankAccount.getUser()))).thenReturn(bankAccount);
        when(transactionRepository.save(any(Transaction.class)))
                .thenAnswer(
                        invocationOnMock -> {
                            final Transaction transaction = invocationOnMock.getArgument(0);
                            ReflectionTestUtils.setField(transaction, "transactionId", 1L);
                            return transaction;
                        }
                );

        //when
        transactionService.deposit(depositRequest);
        final BigDecimal accountBalanceAfterTransaction = accountBalanceBeforeTransaction
                .subtract(depositRequest.getAmount());
        final BigDecimal bankAccountBalanceAfterTransaction = bankAccountBalanceBeforeTransaction
                .add(depositRequest.getAmount());

        //then
        assertThat(account.getBalance())
                .isEqualTo(accountBalanceAfterTransaction);
        assertThat(bankAccount.getBalance())
                .isEqualTo(bankAccountBalanceAfterTransaction);

        verify(accountRepository, times(1)).findById(eq(account.getAccountId()));
        verify(bCryptPasswordEncoder, times(1))
                .matches(eq(depositRequest.getPassword()), anyString());
        verify(userRepository, times(1)).findAllByRole(eq(Role.ADMIN));
        verify(accountRepository, times(1)).findByUser(eq(bankAccount.getUser()));
        verify(transactionRepository, times(1)).save(any(Transaction.class));
    }

    @Disabled
    @Test
    void withdrawFromDeposit() {
    }

    @Disabled
    @Test
    void transfer() {
    }

    @Disabled
    @Test
    void withdraw() {
    }

    @Disabled
    @Test
    void findAllTransactions() {
    }

    @Disabled
    @Test
    void findTransactionById() {
    }

    @Disabled
    @Test
    void deleteTransactionById() {
    }
}