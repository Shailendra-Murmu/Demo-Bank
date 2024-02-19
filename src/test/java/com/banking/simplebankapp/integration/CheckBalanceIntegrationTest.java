package com.banking.simplebankapp.integration;

import com.banking.simplebankapp.controllers.AccountController;
import com.banking.simplebankapp.dto.AccountDetails;
import com.banking.simplebankapp.entities.Account;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.concurrent.ExecutionException;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles(value = "local")
class CheckBalanceIntegrationTest {

    @Autowired
    private AccountController accountController;

    @Test
    void givenAccountDetails_whenCheckingBalance_thenVerifyAccountCorrect() throws ExecutionException, InterruptedException {
        // given
        var input = new AccountDetails();
        input.setSortCode("53-68-92");
        input.setAccountNumber("73084635");

        // when
        var body = accountController.getAccountDetails(input).getBody();

        // then
        var account = (Account) body;
        assertThat(account).isNotNull();
        assertThat(account.getOwnerName()).isEqualTo("Paul Dragoslav");
        assertThat(account.getSortCode()).isEqualTo("53-68-92");
        assertThat(account.getAccountNumber()).isEqualTo("73084635");
    }
}
