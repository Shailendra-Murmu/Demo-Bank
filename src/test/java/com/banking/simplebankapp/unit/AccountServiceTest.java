package com.banking.simplebankapp.unit;

import com.banking.simplebankapp.entities.Account;
import com.banking.simplebankapp.entities.Transaction;
import com.banking.simplebankapp.repositories.AccountRepository;
import com.banking.simplebankapp.repositories.TransactionRepository;
import com.banking.simplebankapp.services.AccountService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@RunWith(SpringRunner.class)
@SpringBootTest
class AccountServiceTest {
    @MockBean
    private AccountRepository accountRepository;
    @MockBean
    private TransactionRepository transactionRepository;
    @Autowired
    private AccountService accountService;

    @Test
    void shouldReturnAccountBySortCodeAndAccountNumberWhenPresent() {
        var account = Account.builder()
                .id(1L).sortCode("53-68-92").accountNumber("78901234")
                .currentBalance(10.1).bankName("Some Bank").ownerName("John").build();
        when(accountRepository.findByAccountNumberAndSortCode("78901234", "53-68-92"))
                .thenReturn(Optional.of(account));

        var result = accountService.getAccount("53-68-92", "78901234");

        assertThat(result.getOwnerName()).isEqualTo(account.getOwnerName());
        assertThat(result.getSortCode()).isEqualTo(account.getSortCode());
        assertThat(result.getAccountNumber()).isEqualTo(account.getAccountNumber());
    }

    @Test
    void shouldReturnTransactionsForAccount() {

        var account = Account.builder().id(1L).sortCode("53-68-92").accountNumber("78901234")
                .currentBalance(10.1).bankName("Some Bank").ownerName("John").build();
        when(accountRepository.findByAccountNumberAndSortCode( "78901234", "53-68-92"))
                .thenReturn(Optional.of(account));
        var transaction1 = new Transaction();
        var transaction2 = new Transaction();
        transaction1.setReference("a");
        transaction2.setReference("b");
        when(transactionRepository.findBySourceAccountIdOrTargetAccountIdOrderByInitiationDate(account.getId(), account.getId()))
                .thenReturn(List.of(transaction1, transaction2));

        var result = accountService.getAccount("53-68-92", "78901234");

        assertThat(result.getTransactions()).hasSize(2);
        assertThat(result.getTransactions()).extracting("reference").containsExactly("a", "b");
    }

    @Test
    void shouldReturnNullWhenAccountBySortCodeAndAccountNotFound() {
        when(accountRepository.findByAccountNumberAndSortCode("78901234", "53-68-92"))
                .thenReturn(Optional.empty());

        var result = accountService.getAccount("53-68-92", "78901234");

        assertThat(result).isNull();
    }

    @Test
    void shouldReturnAccountByAccountNumberWhenPresent() {
    }

    @Test
    void shouldReturnNullWhenAccountByAccountNotFound() {
    }

    @Test
    void shouldCreateAccount() {
    }
}
