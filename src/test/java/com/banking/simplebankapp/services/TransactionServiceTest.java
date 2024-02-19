package com.banking.simplebankapp.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.anyDouble;
import static org.mockito.Mockito.anyLong;
import static org.mockito.Mockito.atLeast;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.banking.simplebankapp.constants.ACTION;
import com.banking.simplebankapp.dto.AccountDetails;
import com.banking.simplebankapp.dto.requests.TransactionRequest;
import com.banking.simplebankapp.entities.Account;
import com.banking.simplebankapp.entities.Transaction;
import com.banking.simplebankapp.repositories.AccountRepository;
import com.banking.simplebankapp.repositories.TransactionRepository;

import java.time.LocalDate;
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

@ContextConfiguration(classes = {TransactionService.class})
@ExtendWith(SpringExtension.class)
@DisabledInAotMode
class TransactionServiceTest {
    @MockBean
    private AccountRepository accountRepository;

    @MockBean
    private TransactionRepository transactionRepository;

    @Autowired
    private TransactionService transactionService;

    /**
     * Method under test:
     * {@link TransactionService#makeTransfer(TransactionRequest)}
     */
    @Test
    void testMakeTransfer() {
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

        Account account2 = new Account();
        account2.setAccountNumber("42");
        account2.setBankName("Bank Name");
        account2.setCurrentBalance(10.0d);
        account2.setId(1L);
        account2.setOwnerName("Owner Name");
        account2.setSortCode("Sort Code");
        account2.setTransactions(new ArrayList<>());
        when(accountRepository.save(Mockito.<Account>any())).thenReturn(account2);
        when(accountRepository.findByAccountNumberAndSortCode(Mockito.<String>any(), Mockito.<String>any()))
                .thenReturn(ofResult);

        Transaction transaction = new Transaction();
        transaction.setAmount(10.0d);
        transaction.setCompletionDate(LocalDate.of(1970, 1, 1).atStartOfDay());
        transaction.setId(1L);
        transaction.setInitiationDate(LocalDate.of(1970, 1, 1).atStartOfDay());
        transaction.setLatitude(10.0d);
        transaction.setLongitude(10.0d);
        transaction.setReference("Reference");
        transaction.setSourceAccountId(1L);
        transaction.setTargetAccountId(1L);
        transaction.setTargetOwnerName("Target Owner Name");
        when(transactionRepository.save(Mockito.<Transaction>any())).thenReturn(transaction);

        TransactionRequest transactionRequest = new TransactionRequest();
        transactionRequest.setTargetAccount(new AccountDetails("Sort Code", "42"));
        transactionRequest.setSourceAccount(new AccountDetails());

        // Act
        boolean actualMakeTransferResult = transactionService.makeTransfer(transactionRequest);

        // Assert
        verify(accountRepository, atLeast(1)).findByAccountNumberAndSortCode(Mockito.<String>any(), Mockito.<String>any());
        verify(transactionRepository).save(Mockito.<Transaction>any());
        verify(accountRepository, atLeast(1)).save(Mockito.<Account>any());
        assertTrue(actualMakeTransferResult);
    }

    /**
     * Method under test:
     * {@link TransactionService#makeTransfer(TransactionRequest)}
     */
    @Test
    void testMakeTransfer2() {
        // Arrange
        Account account = mock(Account.class);
        when(account.getCurrentBalance()).thenReturn(-0.5d);
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

        TransactionRequest transactionRequest = new TransactionRequest();
        transactionRequest.setTargetAccount(new AccountDetails("Sort Code", "42"));
        transactionRequest.setSourceAccount(new AccountDetails());

        // Act
        boolean actualMakeTransferResult = transactionService.makeTransfer(transactionRequest);

        // Assert
        verify(account).getCurrentBalance();
        verify(account).setAccountNumber(eq("42"));
        verify(account).setBankName(eq("Bank Name"));
        verify(account).setCurrentBalance(eq(10.0d));
        verify(account).setId(eq(1L));
        verify(account).setOwnerName(eq("Owner Name"));
        verify(account).setSortCode(eq("Sort Code"));
        verify(account).setTransactions(Mockito.<List<Transaction>>any());
        verify(accountRepository, atLeast(1)).findByAccountNumberAndSortCode(Mockito.<String>any(), Mockito.<String>any());
        assertFalse(actualMakeTransferResult);
    }

    /**
     * Method under test:
     * {@link TransactionService#makeTransfer(TransactionRequest)}
     */
    @Test
    void testMakeTransfer3() {
        // Arrange
        Optional<Account> emptyResult = Optional.empty();
        when(accountRepository.findByAccountNumberAndSortCode(Mockito.<String>any(), Mockito.<String>any()))
                .thenReturn(emptyResult);

        TransactionRequest transactionRequest = new TransactionRequest();
        transactionRequest.setTargetAccount(new AccountDetails("Sort Code", "42"));
        transactionRequest.setSourceAccount(new AccountDetails());

        // Act
        boolean actualMakeTransferResult = transactionService.makeTransfer(transactionRequest);

        // Assert
        verify(accountRepository, atLeast(1)).findByAccountNumberAndSortCode(Mockito.<String>any(), Mockito.<String>any());
        assertFalse(actualMakeTransferResult);
    }

    /**
     * Method under test:
     * {@link TransactionService#updateAccountBalance(Account, double, ACTION)}
     */
    @Test
    void testUpdateAccountBalance() {
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

        Account account2 = new Account();
        account2.setAccountNumber("42");
        account2.setBankName("Bank Name");
        account2.setCurrentBalance(10.0d);
        account2.setId(1L);
        account2.setOwnerName("Owner Name");
        account2.setSortCode("Sort Code");
        account2.setTransactions(new ArrayList<>());

        // Act
        Account actualUpdateAccountBalanceResult = transactionService.updateAccountBalance(account2, 10.0d, ACTION.DEPOSIT);

        // Assert
        verify(accountRepository).save(Mockito.<Account>any());
        assertEquals(20.0d, account2.getCurrentBalance());
        assertSame(account, actualUpdateAccountBalanceResult);
    }

    /**
     * Method under test:
     * {@link TransactionService#updateAccountBalance(Account, double, ACTION)}
     */
    @Test
    void testUpdateAccountBalance2() {
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

        Account account2 = new Account();
        account2.setAccountNumber("42");
        account2.setBankName("Bank Name");
        account2.setCurrentBalance(10.0d);
        account2.setId(1L);
        account2.setOwnerName("Owner Name");
        account2.setSortCode("Sort Code");
        account2.setTransactions(new ArrayList<>());

        // Act
        Account actualUpdateAccountBalanceResult = transactionService.updateAccountBalance(account2, 10.0d,
                ACTION.WITHDRAW);

        // Assert
        verify(accountRepository).save(Mockito.<Account>any());
        assertEquals(0.0d, account2.getCurrentBalance());
        assertSame(account, actualUpdateAccountBalanceResult);
    }

    /**
     * Method under test:
     * {@link TransactionService#isAmountAvailable(double, double)}
     */
    @Test
    void testIsAmountAvailable() {
        // Arrange, Act and Assert
        assertFalse(transactionService.isAmountAvailable(10.0d, 10.0d));
        assertTrue(transactionService.isAmountAvailable(0.0d, 10.0d));
    }
}
