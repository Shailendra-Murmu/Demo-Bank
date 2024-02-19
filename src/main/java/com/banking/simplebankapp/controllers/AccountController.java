package com.banking.simplebankapp.controllers;

import com.banking.simplebankapp.constants.COMMON_CONSTANT;
import com.banking.simplebankapp.dto.AccountDetails;
import com.banking.simplebankapp.dto.requests.CreateAccountRequest;
import com.banking.simplebankapp.entities.Account;
import com.banking.simplebankapp.services.AccountService;
import com.banking.simplebankapp.utilities.InputValidator;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.requestreply.ReplyingKafkaTemplate;
import org.springframework.kafka.requestreply.RequestReplyFuture;
import org.springframework.web.bind.annotation.*;
import java.util.concurrent.ExecutionException;

@RestController
@RequestMapping("api/v1")
@Slf4j
public class AccountController {
    @Value("${kafka.request.topic.ACCOUNT_CREATE}")
    private String requestTopic_ACCOUNT_CREATE;
    @Value("${kafka.request.topic.ACCOUNT_FETCH}")
    private String requestTopic_ACCOUNT_FETCH;
    @Autowired
    private ReplyingKafkaTemplate<String, CreateAccountRequest, Account> replyingCreateAccountKafkaTemplate;
    @Autowired
    private ReplyingKafkaTemplate<String, AccountDetails, Account> replyingFetchAccountKafkaTemplate;
    @Autowired
    private AccountService accountService;

    @PutMapping(value = "/accounts",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> createAccount(
            @Valid @RequestBody CreateAccountRequest createAccountRequest) throws ExecutionException, InterruptedException {
        log.debug("Triggered AccountRestController.createAccountRequest");

        // Validate input
        if (InputValidator.isCreateAccountCriteriaValid(createAccountRequest)) {
            ProducerRecord<String, CreateAccountRequest> record = new ProducerRecord<>(requestTopic_ACCOUNT_CREATE, null, "STD001", createAccountRequest);
            RequestReplyFuture<String, CreateAccountRequest, Account> future = replyingCreateAccountKafkaTemplate.sendAndReceive(record);
            ConsumerRecord<String, Account> response = future.get();
            Account account = response.value();
            // Return the account details, or warn that no account was found for given input
            if (account == null) {
                return new ResponseEntity<>(COMMON_CONSTANT.CREATE_ACCOUNT_FAILED, HttpStatus.OK);
            } else {
                return new ResponseEntity<>(account, HttpStatus.OK);
            }
        } else {
            return new ResponseEntity<>(COMMON_CONSTANT.INVALID_SEARCH_CRITERIA, HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping(value = "/accounts",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> checkAccountBalance(
            @Valid @RequestBody AccountDetails accountDetails) throws ExecutionException, InterruptedException {
        log.debug("Triggered AccountRestController.accountDetails");

        // Validate input
        if (InputValidator.isSearchCriteriaValid(accountDetails)) {
            // Attempt to retrieve the account information
            Account account = accountService.getAccount(
                    accountDetails.getSortCode(), accountDetails.getAccountNumber());
            // Return the account details, or warn that no account was found for given input
            if (account == null) {
                return new ResponseEntity<>(COMMON_CONSTANT.NO_ACCOUNT_FOUND, HttpStatus.NO_CONTENT);
            } else {
                ProducerRecord<String, AccountDetails> record = new ProducerRecord<>(requestTopic_ACCOUNT_FETCH, null, "STD001", accountDetails);
                RequestReplyFuture<String, AccountDetails, Account> future = replyingFetchAccountKafkaTemplate.sendAndReceive(record);
                ConsumerRecord<String, Account> response = future.get();
                return new ResponseEntity<>(response.value(), HttpStatus.OK);
            }
        } else {
            return new ResponseEntity<>(COMMON_CONSTANT.INVALID_SEARCH_CRITERIA, HttpStatus.BAD_REQUEST);
        }
    }
}
