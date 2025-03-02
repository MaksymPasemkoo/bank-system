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
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
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
    void createOrUpdateAccountShouldReturnDTOResponse() {
        //given
        final Account expectedAccount = new Account(new User(1L, Role.ADMIN, "jdfal", "fjslfj"),
                AccountType.BUSINESS,
                BigDecimal.valueOf(13441));
        ReflectionTestUtils.setField(expectedAccount, "accountId", 1L);

        final AccountDTORequest accountDTORequest = AccountHelper.convertToAccountDTORequest(expectedAccount);
        final AccountDTOResponse expectedAccountDTOResponse = AccountHelper.convertToAccountDTOResponse(expectedAccount);

        //mock the calls
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(expectedAccount.getUser()));
        when(accountRepository.save(any(Account.class)))
                .thenAnswer(
                        invocationOnMock -> {
                            final Account account = invocationOnMock.getArgument(0);
                            ReflectionTestUtils.setField(account,"accountId",1L);
                            return account;
                        }
                );

        //when
        final AccountDTOResponse actualAccountDTOResponse = accountService.createOrUpdateAccount(accountDTORequest);

        //then
        assertThat(actualAccountDTOResponse)
                .usingRecursiveComparison()
                .isEqualTo(expectedAccountDTOResponse);

        verify(userRepository, times(1)).findById(expectedAccount.getUser().getUserId());
        verify(accountRepository, times(1)).save(any(Account.class));
    }

    @Test
    void findAccountByIdShouldReturnAccountDTOResponseWhenAccountExists() {
        //given
        final Long accountId = 1L;
        final Account expectedAccount = new Account(accountId,new User(1L, Role.ADMIN, "jdfal", "fjslfj"),
                AccountType.BUSINESS,
                BigDecimal.valueOf(13441));

        final AccountDTOResponse expectedAccountDTOResponse = AccountHelper.convertToAccountDTOResponse(expectedAccount);

        //mock the calls
        when(accountRepository.findById(accountId)).thenReturn(Optional.of(expectedAccount));

        //when
        final AccountDTOResponse actualAccountDTOResponse = accountService.findAccountById(accountId);

        //then
        assertThat(actualAccountDTOResponse)
                .usingRecursiveComparison()
                .isEqualTo(expectedAccountDTOResponse);

        verify(accountRepository, times(1)).findById(accountId);
    }

    @Test
    void findAccountByIdShouldThrowExceptionWhenAccountNotFound() {
        //given
        final Long id = 1L;

        //mock the calls
        when(accountRepository.findById(anyLong())).thenReturn(Optional.empty());

        //when + then
        assertThrows(NoSuchElementException.class, () -> accountService.findAccountById(id));

        verify(accountRepository, times(1)).findById(id);
    }

    @Test
    void findAllAccountsShouldReturnListOfAccountDTOResponses() {
        //given
        final List<Account> expectedAccountList = List.of(
                new Account(1L, new User(1L, Role.ADMIN, "jdfal", "fjslfj"),
                        AccountType.BUSINESS,
                        BigDecimal.valueOf(13441)),
                new Account(2L, new User(2L, Role.USER, "ero", "toov"),
                        AccountType.CHECKING,
                        BigDecimal.valueOf(13441)),
                new Account(3L, new User(3L, Role.ADMIN, "vooe", "oq"),
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
        assertThat(actualAccountDTOResponses)
                .usingRecursiveComparison()
                .isEqualTo(expectedAccountDTOResponses);

        verify(accountRepository, times(1)).findAll();
    }

    @Test
    void deleteAccountByIdShouldReturnTrueAndDeleteAccountWhenExists() {
        //given
        final Long id = 1L;

        //mock the calls
        when(accountRepository.existsById(anyLong())).thenReturn(true);

        //when
        final boolean isDeleted = accountService.deleteAccountById(id);

        //then
        assertTrue(isDeleted);

        verify(accountRepository, times(1)).existsById(id);
        verify(accountRepository, times(1)).deleteById(id);
    }

    @Test
    void deleteAccountByIdShouldReturnFalseWhenAccountDoesNotExists() {
        //given
        final Long id = 1L;

        //mock the calls
        when(accountRepository.existsById(anyLong())).thenReturn(false);

        //when
        final boolean isDeleted = accountService.deleteAccountById(id);

        //then
        assertFalse(isDeleted);

        verify(accountRepository, times(1)).existsById(id);
        verify(accountRepository, never()).deleteById(id);
    }

    @Test
    void deleteAccountShouldReturnTrueAndDeleteWhenAccountExists() {
        //given
        final Long id = 1L;
        final String password = "some password";
        final Account account = new Account(id, new User(1L, Role.ADMIN, "jdfal", password),
                AccountType.BUSINESS,
                BigDecimal.valueOf(13441));

        final AccountCredentials accountCredentials = new AccountCredentials(id, password);

        //mock the calls
        when(accountRepository.findById(anyLong())).thenReturn(Optional.of(account));
        when(bCryptPasswordEncoder.matches(eq(password), anyString())).thenReturn(true);

        //when
        final boolean isDeleted = accountService.deleteAccount(accountCredentials);

        //then
        assertTrue(isDeleted);

        verify(accountRepository, times(1)).findById(id);
        verify(bCryptPasswordEncoder, times(1)).matches(password, password);
        verify(accountRepository, times(1)).delete(account);
    }

    @Test
    void deleteAccountShouldReturnFalseWhenAccountDoesNotExists() {
        //given
        final Long id = 1L;
        final String password = "some password";
        final Account account = new Account(id, new User(1L, Role.ADMIN, "jdfal", password),
                AccountType.BUSINESS,
                BigDecimal.valueOf(13441));

        final AccountCredentials accountCredentials = new AccountCredentials(id, password);

        //mock the calls
        when(accountRepository.findById(anyLong())).thenReturn(Optional.empty());

        //when
        final boolean isDeleted = accountService.deleteAccount(accountCredentials);

        //then
        assertFalse(isDeleted);

        verify(accountRepository, times(1)).findById(id);
        verify(bCryptPasswordEncoder, never()).matches(password, password);
        verify(accountRepository, never()).delete(account);
    }

    @Test
    void deleteAccountShouldThrowExceptionWhenPasswordIsNotCorrect() {
        //given
        final Long id = 1L;
        final String password = "some password";
        final Account account = new Account(id, new User(1L, Role.ADMIN, "jdfal", password),
                AccountType.BUSINESS,
                BigDecimal.valueOf(13441));

        final AccountCredentials accountCredentials = new AccountCredentials(id, password);

        //mock the calls
        when(accountRepository.findById(anyLong())).thenReturn(Optional.of(account));
        when(bCryptPasswordEncoder.matches(eq(password), anyString())).thenReturn(false);


        //when + then
        assertThrows(PermissionException.class, () -> accountService.deleteAccount(accountCredentials));

        verify(accountRepository, times(1)).findById(id);
        verify(bCryptPasswordEncoder, times(1)).matches(password, password);
        verify(accountRepository, never()).delete(account);
    }
}