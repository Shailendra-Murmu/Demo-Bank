package com.banking.simplebankapp.controllers;

import com.banking.simplebankapp.dto.requests.DepositRequest;
import com.banking.simplebankapp.dto.requests.TransactionRequest;
import com.banking.simplebankapp.dto.requests.WithdrawRequest;
import com.banking.simplebankapp.entities.Account;
import com.banking.simplebankapp.services.AccountService;
import com.banking.simplebankapp.services.TransactionService;
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

import static com.banking.simplebankapp.constants.COMMON_CONSTANT.*;

@RestController
@RequestMapping("api/v1")
@Slf4j
public class TransactionController {
    @Value("${kafka.request.topic.FUND_TRANSFER}")
    private String requestTopic_FUND_TRANSFER;
    @Value("${kafka.request.topic.FUND_DEPOSIT}")
    private String requestTopic_FUND_DEPOSIT;
    @Value("${kafka.request.topic.FUND_WITHDRAW}")
    private String requestTopic_FUND_WITHDRAW;
    @Autowired
    private ReplyingKafkaTemplate<String, TransactionRequest, Boolean> replyingFundTransferKafkaTemplate;
    @Autowired
    private ReplyingKafkaTemplate<String, DepositRequest, Boolean> replyingFundDepositKafkaTemplate;
    @Autowired
    private ReplyingKafkaTemplate<String, WithdrawRequest, Boolean> replyingFundWithdrawKafkaTemplate;

    @Autowired
    private AccountService accountService;
    @Autowired
    private TransactionService transactionService;

    @PostMapping(value = "/transactions",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> makeTransfer(
            @Valid @RequestBody TransactionRequest transactionRequest) throws ExecutionException, InterruptedException {
        // This controller is used for making fund transfer from one account to another account.
        if (InputValidator.isSearchTransactionValid(transactionRequest)) {
            ProducerRecord<String, TransactionRequest> record = new ProducerRecord<>(requestTopic_FUND_TRANSFER, null, "STD001", transactionRequest);
            RequestReplyFuture<String, TransactionRequest, Boolean> future = replyingFundTransferKafkaTemplate.sendAndReceive(record);
            ConsumerRecord<String, Boolean> response = future.get();
            log.info("Transfer result: {}", response.value());
            if(response.value()) return new ResponseEntity<>(SUCCESS, HttpStatus.OK);
            else return new ResponseEntity<>(TRANSFER_FAILED, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(INVALID_TRANSACTION, HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping(value = "/deposit",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> deposit(
            @Valid @RequestBody DepositRequest depositRequest) throws ExecutionException, InterruptedException {
        log.debug("Triggered AccountRestController.depositRequest");
        // This controller is used to deposit amount in bank account of a user.
        // Validate input
        if (InputValidator.isAccountNoValid(depositRequest.getTargetAccountNo())) {
            // Attempt to retrieve the account information
            Account account = accountService.getAccount(depositRequest.getTargetAccountNo());

            // Return the account details, or warn that no account was found for given input
            if (account == null) {
                return new ResponseEntity<>(NO_ACCOUNT_FOUND, HttpStatus.NO_CONTENT);
            } else {
                ProducerRecord<String, DepositRequest> record = new ProducerRecord<>(requestTopic_FUND_DEPOSIT, null, "STD001", depositRequest);
                RequestReplyFuture<String, DepositRequest, Boolean> future = replyingFundDepositKafkaTemplate.sendAndReceive(record);
                ConsumerRecord<String, Boolean> response = future.get();
                log.info("Deposit result: {}", response.value());
                if(response.value()) return new ResponseEntity<>(SUCCESS, HttpStatus.OK);
                else return new ResponseEntity<>(DEPOSIT_FAILED, HttpStatus.OK);
            }
        } else {
            return new ResponseEntity<>(INVALID_SEARCH_CRITERIA, HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping(value = "/withdraw",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> withdraw(
            @Valid @RequestBody WithdrawRequest withdrawRequest) throws ExecutionException, InterruptedException {
        log.debug("Triggered AccountRestController.withdrawRequest");
        // This controller is used to withdraw fund from the bank account of a user.
        // Validate input
        if (InputValidator.isSearchCriteriaValid(withdrawRequest)) {
            // Attempt to retrieve the account information
            Account account = accountService.getAccount(
                    withdrawRequest.getSortCode(), withdrawRequest.getAccountNumber());

            // Return the account details, or warn that no account was found for given input
            if (account == null) {
                return new ResponseEntity<>(NO_ACCOUNT_FOUND, HttpStatus.NO_CONTENT);
            } else {
                if (transactionService.isAmountAvailable(withdrawRequest.getAmount(), account.getCurrentBalance())) {
                    ProducerRecord<String, WithdrawRequest> record = new ProducerRecord<>(requestTopic_FUND_WITHDRAW, null, "STD001", withdrawRequest);
                    RequestReplyFuture<String, WithdrawRequest, Boolean> future = replyingFundWithdrawKafkaTemplate.sendAndReceive(record);
                    ConsumerRecord<String, Boolean> response = future.get();
                    log.info("Withdraw result: {}", response.value());
                    if(response.value()) return new ResponseEntity<>(SUCCESS, HttpStatus.OK);
                    else return new ResponseEntity<>(WITHDRAW_FAILED, HttpStatus.OK);
                }
                return new ResponseEntity<>(INSUFFICIENT_ACCOUNT_BALANCE, HttpStatus.OK);
            }
        } else {
            return new ResponseEntity<>(INVALID_SEARCH_CRITERIA, HttpStatus.BAD_REQUEST);
        }
    }
}
