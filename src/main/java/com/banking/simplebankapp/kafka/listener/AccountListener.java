package com.banking.simplebankapp.kafka.listener;

import com.banking.simplebankapp.dto.AccountDetails;
import com.banking.simplebankapp.dto.requests.CreateAccountRequest;
import com.banking.simplebankapp.entities.Account;
import com.banking.simplebankapp.services.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Component;

@Component
public class AccountListener {
    @Autowired
    private AccountService accountService;

    @KafkaListener(topics = "${kafka.request.topic.ACCOUNT_CREATE}", groupId = "${kafka.group.id}")
    @SendTo
    public Account handleCreateAccount(CreateAccountRequest createAccountRequest) {
        return accountService.createAccount(
                createAccountRequest.getBankName(), createAccountRequest.getOwnerName());
    }

    @KafkaListener(topics = "${kafka.request.topic.ACCOUNT_FETCH}", groupId = "${kafka.group.id}")
    @SendTo
    public Account handleFetchAccount(AccountDetails accountDetails) {
        return accountService.getAccount(
                accountDetails.getSortCode(), accountDetails.getAccountNumber());
    }
}
