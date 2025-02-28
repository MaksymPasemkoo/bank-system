package com.ltp.banksystem.service;

import com.ltp.banksystem.dto.dtorequest.AccountCredentials;
import com.ltp.banksystem.dto.dtorequest.AccountDTORequest;
import com.ltp.banksystem.dto.dtoresponce.AccountDTOResponse;
import com.ltp.banksystem.exception.PermissionException;
import com.ltp.banksystem.model.Account;
import com.ltp.banksystem.model.User;
import com.ltp.banksystem.model.enums.AccountType;
import com.ltp.banksystem.model.enums.Role;
import com.ltp.banksystem.repository.AccountRepository;
import com.ltp.banksystem.repository.UserRepository;
import com.ltp.banksystem.utils.AccountHelper;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.math.BigDecimal;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AccountServiceTest {
    @InjectMocks
    private AccountService accountService;
    @Mock
    private AccountRepository accountRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Test
    void shouldCreateOrUpdateStudent() {
        //given
        final Account account = new Account(1L, new User(1L, Role.ADMIN, "jdfal", "fjslfj"),
                AccountType.BUSINESS,
                BigDecimal.valueOf(13441));

        final AccountDTORequest accountDTORequest = AccountHelper.convertToAccountDTORequest(account);

        //mock the calls
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(account.getUser()));
        when(accountRepository.save(any(Account.class))).thenReturn(account);

        //when
        final AccountDTOResponse actualAccountDTOResponse = accountService.createOrUpdateAccount(accountDTORequest);

        //then
        assertThat(actualAccountDTOResponse.getUserId()).isEqualTo(account.getUser().getUserId());
        assertThat(actualAccountDTOResponse.getAccountType()).isEqualTo(account.getAccountType());
        assertThat(actualAccountDTOResponse.getBalance()).isEqualTo(account.getBalance());

        verify(userRepository, times(1)).findById(account.getUser().getUserId());
        verify(accountRepository, times(1)).save(any(Account.class));
    }

    @Test
    void shouldReturnAccountDTOResponseByIdWhenAccountExists() {
        //given
        final Long id = 1L;
        final Account account = new Account(1L, new User(1L, Role.ADMIN, "jdfal", "fjslfj"),
                AccountType.BUSINESS,
                BigDecimal.valueOf(13441));
        final AccountDTOResponse expectedAccountDTOResponse = AccountHelper.convertToAccountDTOResponse(account);

        //mock the calls
        when(accountRepository.findById(id)).thenReturn(Optional.of(account));

        //when
        final AccountDTOResponse actualAccountDTOResponse = accountService.findAccountById(id);

        //then
        assertThat(actualAccountDTOResponse).isEqualTo(expectedAccountDTOResponse);

        verify(accountRepository, times(1)).findById(id);
    }

    @Test
    void shouldThrowExceptionWhenAccountNotFound() {
        //given
        final Long id = 1L;

        //mock the calls
        when(accountRepository.findById(id)).thenReturn(Optional.empty());

        //when + then
        assertThrows(NoSuchElementException.class, () -> accountService.findAccountById(id));

        verify(accountRepository, times(1)).findById(id);
    }

    @Test
    void shouldReturnListOfAccountDTOResponses() {
        //given
        final List<Account> expectedAccountList = List.of(
                new Account(1L, new User(1L, Role.ADMIN, "jdfal", "fjslfj"),
                        AccountType.BUSINESS,
                        BigDecimal.valueOf(13441)),
                new Account(2L, new User(2L, Role.USER, "jdfal", "fjslfj"),
                        AccountType.CHECKING,
                        BigDecimal.valueOf(13441)),
                new Account(3L, new User(3L, Role.ADMIN, "jdfal", "fjslfj"),
                        AccountType.SAVING,
                        BigDecimal.valueOf(13441))
        );
        final List<AccountDTOResponse> expectedAccountDTOResponses = expectedAccountList.stream()
                .map(AccountHelper::convertToAccountDTOResponse)
                .toList();

        //mock the calls
        when(accountRepository.findAll()).thenReturn(expectedAccountList);

        //when
        final List<AccountDTOResponse> actualAccountDTOResponses = accountService.findAllAccounts();

        //then
        assertThat(actualAccountDTOResponses).isEqualTo(expectedAccountDTOResponses);

        verify(accountRepository, times(1)).findAll();
    }

    @Test
    void shouldReturnTrueAndDeleteAccountByIdWhenAccountExists() {
        //given
        final Long id = 1L;

        //mock the calls
        when(accountRepository.existsById(id)).thenReturn(true);

        //when
        final boolean isDeleted = accountService.deleteAccountById(id);

        //then
        assertTrue(isDeleted);

        verify(accountRepository,times(1)).existsById(id);
        verify(accountRepository,times(1)).deleteById(id);
    }

    @Test
    void shouldReturnFalseWhenAccountDoesNotFoundById() {
        //given
        final Long id = 1L;

        //mock the calls
        when(accountRepository.existsById(id)).thenReturn(false);

        //when
        final boolean isDeleted = accountService.deleteAccountById(id);

        //then
        assertFalse(isDeleted);

        verify(accountRepository,times(1)).existsById(id);
        verify(accountRepository,never()).deleteById(id);
    }

    @Test
    void shouldReturnTrueAndDeleteByAccountCredentials() {
        //given
        final Long id = 1L;
        final String password = "some password";
        final Account account = new Account(id, new User(1L, Role.ADMIN, "jdfal", password),
                AccountType.BUSINESS,
                BigDecimal.valueOf(13441));

        final AccountCredentials accountCredentials = new AccountCredentials(id,password);

        //mock the calls
        when(accountRepository.findById(id)).thenReturn(Optional.of(account));
        when(bCryptPasswordEncoder.matches(password,password)).thenReturn(true);

        //when
        final boolean isDeleted = accountService.deleteAccount(accountCredentials);

        //then
        assertTrue(isDeleted);

        verify(accountRepository,times(1)).findById(id);
        verify(bCryptPasswordEncoder,times(1)).matches(password,password);
        verify(accountRepository,times(1)).delete(account);
    }

    @Test
    void shouldReturnFalseWhenAccountDoesNotFoundByIdByAccountCredentials() {
        //given
        final Long id = 1L;
        final String password = "some password";
        final Account account = new Account(id, new User(1L, Role.ADMIN, "jdfal", password),
                AccountType.BUSINESS,
                BigDecimal.valueOf(13441));

        final AccountCredentials accountCredentials = new AccountCredentials(id,password);

        //mock the calls
        when(accountRepository.findById(id)).thenReturn(Optional.empty());

        //when
        final boolean isDeleted = accountService.deleteAccount(accountCredentials);

        //then
        assertFalse(isDeleted);

        verify(accountRepository,times(1)).findById(id);
        verify(bCryptPasswordEncoder,never()).matches(password,password);
        verify(accountRepository,never()).delete(account);
    }

    @Test
    void shouldThrowExceptionWhenPasswordIsNotCorrectByAccountCredentials() {
        //given
        final Long id = 1L;
        final String password = "some password";
        final Account account = new Account(id, new User(1L, Role.ADMIN, "jdfal", password),
                AccountType.BUSINESS,
                BigDecimal.valueOf(13441));

        final AccountCredentials accountCredentials = new AccountCredentials(id,password);

        //mock the calls
        when(accountRepository.findById(id)).thenReturn(Optional.of(account));
        when(bCryptPasswordEncoder.matches(password,password)).thenReturn(false);


        //when + then
        assertThrows(PermissionException.class,() -> accountService.deleteAccount(accountCredentials));

        verify(accountRepository,times(1)).findById(id);
        verify(bCryptPasswordEncoder,times(1)).matches(password,password);
        verify(accountRepository,never()).delete(account);
    }
}