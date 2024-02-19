package com.banking.simplebankapp.unit;

import com.banking.simplebankapp.dto.AccountDetails;
import com.banking.simplebankapp.dto.requests.TransactionRequest;
import com.banking.simplebankapp.entities.Account;
import com.banking.simplebankapp.repositories.AccountRepository;
import com.banking.simplebankapp.repositories.TransactionRepository;
import com.banking.simplebankapp.services.TransactionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@RunWith(SpringRunner.class)
@SpringBootTest
class TransactionServiceTest {
    @Autowired
    private TransactionService transactionService;

    @MockBean
    private AccountRepository accountRepository;

    @MockBean
    private TransactionRepository transactionRepository;

    @BeforeEach
    void setUp() {
        var sourceAccount = new Account(1L, "53-68-92", "78901234", 458.1, "Some Bank", "John", null);
        var targetAccount = new Account(2L, "67-41-18", "48573590", 64.9, "Some Other Bank", "Major", null);

        when(accountRepository.findByAccountNumberAndSortCode("78901234", "53-68-92"))
                .thenReturn(Optional.of(sourceAccount));
        when(accountRepository.findByAccountNumberAndSortCode("48573590", "67-41-18"))
                .thenReturn(Optional.of(targetAccount));
    }

    @Test
    void whenTransactionDetails_thenTransferShouldBeDenied() {
        var sourceAccount = new AccountDetails();
        sourceAccount.setSortCode("53-68-92");
        sourceAccount.setAccountNumber("78901234");

        var targetAccount = new AccountDetails();
        targetAccount.setSortCode("67-41-18");
        targetAccount.setAccountNumber("48573590");

        var input = new TransactionRequest();
        input.setSourceAccount(sourceAccount);
        input.setTargetAccount(targetAccount);
        input.setAmount(50);
        input.setReference("My reference");

        boolean isComplete = transactionService.makeTransfer(input);

        assertThat(isComplete).isTrue();
    }

    @Test
    void whenTransactionDetailsAndAmountTooLarge_thenTransferShouldBeDenied() {
        var sourceAccount = new AccountDetails();
        sourceAccount.setSortCode("53-68-92");
        sourceAccount.setAccountNumber("78901234");

        var targetAccount = new AccountDetails();
        targetAccount.setSortCode("67-41-18");
        targetAccount.setAccountNumber("48573590");

        var input = new TransactionRequest();
        input.setSourceAccount(sourceAccount);
        input.setTargetAccount(targetAccount);
        input.setAmount(10000);
        input.setReference("My reference");

        boolean isComplete = transactionService.makeTransfer(input);

        assertThat(isComplete).isFalse();
    }
}
