package com.banking.simplebankapp.kafka.listener;

import com.banking.simplebankapp.constants.ACTION;
import com.banking.simplebankapp.dto.requests.DepositRequest;
import com.banking.simplebankapp.dto.requests.TransactionRequest;
import com.banking.simplebankapp.dto.requests.WithdrawRequest;
import com.banking.simplebankapp.entities.Account;
import com.banking.simplebankapp.services.AccountService;
import com.banking.simplebankapp.services.TransactionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class TransactionListener {
    @Autowired
    private AccountService accountService;
    @Autowired
    private TransactionService transactionService;

    @KafkaListener(topics = "${kafka.request.topic.FUND_TRANSFER}", groupId = "${kafka.group.id}")
    @SendTo
    public boolean handleFundTransfer(TransactionRequest transactionRequest) {
        return transactionService.makeTransfer(transactionRequest);
    }

    @KafkaListener(topics = "${kafka.request.topic.FUND_DEPOSIT}", groupId = "${kafka.group.id}")
    @SendTo
    public boolean handleFundDeposit(DepositRequest depositRequest) {
        Account account = accountService.getAccount(depositRequest.getTargetAccountNo());
        double initialBal = account.getCurrentBalance();
        final Account updatedAccount = transactionService.updateAccountBalance(account, depositRequest.getAmount(), ACTION.DEPOSIT);
        log.info("Initial Balance: {}, Closing Balance: {}, Deposit Amount: {}", initialBal, updatedAccount.getCurrentBalance(), depositRequest.getAmount());
        return updatedAccount.getCurrentBalance() == initialBal + depositRequest.getAmount();
    }

    @KafkaListener(topics = "${kafka.request.topic.FUND_WITHDRAW}", groupId = "${kafka.group.id}")
    @SendTo
    public boolean handleFundWithdraw(WithdrawRequest withdrawRequest) {
        Account account = accountService.getAccount(withdrawRequest.getSortCode(), withdrawRequest.getAccountNumber());
        double initialBal = account.getCurrentBalance();
        Account updatedAccount = transactionService.updateAccountBalance(account, withdrawRequest.getAmount(), ACTION.WITHDRAW);
        log.info("Initial Balance: {}, Closing Balance: {}, Withdrawal Amount: {}", initialBal, updatedAccount.getCurrentBalance(), withdrawRequest.getAmount());
        return updatedAccount.getCurrentBalance() == initialBal - withdrawRequest.getAmount();
    }
}
