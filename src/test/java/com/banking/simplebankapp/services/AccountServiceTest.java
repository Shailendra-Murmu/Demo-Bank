package com.banking.simplebankapp.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.anyDouble;
import static org.mockito.Mockito.anyLong;
import static org.mockito.Mockito.atLeast;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.banking.simplebankapp.entities.Account;
import com.banking.simplebankapp.entities.Transaction;
import com.banking.simplebankapp.repositories.AccountRepository;
import com.banking.simplebankapp.repositories.TransactionRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.aot.DisabledInAotMode;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ContextConfiguration(classes = {AccountService.class})
@ExtendWith(SpringExtension.class)
@DisabledInAotMode
class AccountServiceTest {
    @MockBean
    private AccountRepository accountRepository;

    @Autowired
    private AccountService accountService;

    @MockBean
    private TransactionRepository transactionRepository;

    /**
     * Method under test: {@link AccountService#getAccount(String)}
     */
    @Test
    void testGetAccount() {
        // Arrange
        Account account = new Account();
        account.setAccountNumber("42");
        account.setBankName("Bank Name");
        account.setCurrentBalance(10.0d);
        account.setId(1L);
        account.setOwnerName("Owner Name");
        account.setSortCode("Sort Code");
        account.setTransactions(new ArrayList<>());
        Optional<Account> ofResult = Optional.of(account);
        when(accountRepository.findByAccountNumber(Mockito.<String>any())).thenReturn(ofResult);

        // Act
        Account actualAccount = accountService.getAccount("42");

        // Assert
        verify(accountRepository).findByAccountNumber(eq("42"));
        assertSame(account, actualAccount);
    }

    /**
     * Method under test: {@link AccountService#getAccount(String, String)}
     */
    @Test
    void testGetAccount2() {
        // Arrange
        Account account = new Account();
        account.setAccountNumber("42");
        account.setBankName("Bank Name");
        account.setCurrentBalance(10.0d);
        account.setId(1L);
        account.setOwnerName("Owner Name");
        account.setSortCode("Sort Code");
        ArrayList<Transaction> transactions = new ArrayList<>();
        account.setTransactions(transactions);
        Optional<Account> ofResult = Optional.of(account);
        when(accountRepository.findByAccountNumberAndSortCode(Mockito.<String>any(), Mockito.<String>any()))
                .thenReturn(ofResult);
        when(transactionRepository.findBySourceAccountIdOrTargetAccountIdOrderByInitiationDate(anyLong(), anyLong()))
                .thenReturn(new ArrayList<>());

        // Act
        Account actualAccount = accountService.getAccount("Sort Code", "42");

        // Assert
        verify(accountRepository).findByAccountNumberAndSortCode(eq("42"), eq("Sort Code"));
        verify(transactionRepository).findBySourceAccountIdOrTargetAccountIdOrderByInitiationDate(eq(1L), eq(1L));
        assertEquals(transactions, actualAccount.getTransactions());
        assertSame(account, actualAccount);
    }

    /**
     * Method under test: {@link AccountService#getAccount(String, String)}
     */
    @Test
    void testGetAccount3() {
        // Arrange
        Account account = mock(Account.class);
        when(account.getId()).thenReturn(1L);
        doNothing().when(account).setAccountNumber(Mockito.<String>any());
        doNothing().when(account).setBankName(Mockito.<String>any());
        doNothing().when(account).setCurrentBalance(anyDouble());
        doNothing().when(account).setId(anyLong());
        doNothing().when(account).setOwnerName(Mockito.<String>any());
        doNothing().when(account).setSortCode(Mockito.<String>any());
        doNothing().when(account).setTransactions(Mockito.<List<Transaction>>any());
        account.setAccountNumber("42");
        account.setBankName("Bank Name");
        account.setCurrentBalance(10.0d);
        account.setId(1L);
        account.setOwnerName("Owner Name");
        account.setSortCode("Sort Code");
        account.setTransactions(new ArrayList<>());
        Optional<Account> ofResult = Optional.of(account);
        when(accountRepository.findByAccountNumberAndSortCode(Mockito.<String>any(), Mockito.<String>any()))
                .thenReturn(ofResult);
        when(transactionRepository.findBySourceAccountIdOrTargetAccountIdOrderByInitiationDate(anyLong(), anyLong()))
                .thenReturn(new ArrayList<>());

        // Act
        accountService.getAccount("Sort Code", "42");

        // Assert
        verify(account, atLeast(1)).getId();
        verify(account).setAccountNumber(eq("42"));
        verify(account).setBankName(eq("Bank Name"));
        verify(account).setCurrentBalance(eq(10.0d));
        verify(account).setId(eq(1L));
        verify(account).setOwnerName(eq("Owner Name"));
        verify(account).setSortCode(eq("Sort Code"));
        verify(account, atLeast(1)).setTransactions(Mockito.<List<Transaction>>any());
        verify(accountRepository).findByAccountNumberAndSortCode(eq("42"), eq("Sort Code"));
        verify(transactionRepository).findBySourceAccountIdOrTargetAccountIdOrderByInitiationDate(eq(1L), eq(1L));
    }

    /**
     * Method under test: {@link AccountService#getAccount(String, String)}
     */
    @Test
    void testGetAccount4() {
        // Arrange
        Optional<Account> emptyResult = Optional.empty();
        when(accountRepository.findByAccountNumberAndSortCode(Mockito.<String>any(), Mockito.<String>any()))
                .thenReturn(emptyResult);

        // Act
        Account actualAccount = accountService.getAccount("Sort Code", "42");

        // Assert
        verify(accountRepository).findByAccountNumberAndSortCode(eq("42"), eq("Sort Code"));
        assertNull(actualAccount);
    }

    /**
     * Method under test: {@link AccountService#createAccount(String, String)}
     */
    @Test
    void testCreateAccount() {
        // Arrange
        Account account = new Account();
        account.setAccountNumber("42");
        account.setBankName("Bank Name");
        account.setCurrentBalance(10.0d);
        account.setId(1L);
        account.setOwnerName("Owner Name");
        account.setSortCode("Sort Code");
        account.setTransactions(new ArrayList<>());
        when(accountRepository.save(Mockito.<Account>any())).thenReturn(account);

        // Act
        Account actualCreateAccountResult = accountService.createAccount("Bank Name", "Owner Name");

        // Assert
        verify(accountRepository).save(Mockito.<Account>any());
        assertSame(account, actualCreateAccountResult);
    }
}
