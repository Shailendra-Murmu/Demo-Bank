package com.banking.simplebankapp.services;

import com.banking.simplebankapp.constants.ACTION;
import com.banking.simplebankapp.dto.requests.TransactionRequest;
import com.banking.simplebankapp.entities.Account;
import com.banking.simplebankapp.entities.Transaction;
import com.banking.simplebankapp.repositories.AccountRepository;
import com.banking.simplebankapp.repositories.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class TransactionService {
    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private TransactionRepository transactionRepository;

    public boolean makeTransfer(TransactionRequest transactionRequest) {
        // This method is used for making fund transfer from source account to target account.
        // This also validates that the source account should have sufficient balance for making fund transfer.
        String sourceSortCode = transactionRequest.getSourceAccount().getSortCode();
        String sourceAccountNumber = transactionRequest.getSourceAccount().getAccountNumber();
        Optional<Account> sourceAccount = accountRepository
                .findByAccountNumberAndSortCode(sourceAccountNumber, sourceSortCode);

        String targetSortCode = transactionRequest.getTargetAccount().getSortCode();
        String targetAccountNumber = transactionRequest.getTargetAccount().getAccountNumber();
        Optional<Account> targetAccount = accountRepository
                .findByAccountNumberAndSortCode(targetAccountNumber, targetSortCode);

        if (sourceAccount.isPresent() && targetAccount.isPresent()) {
            if (isAmountAvailable(transactionRequest.getAmount(), sourceAccount.get().getCurrentBalance())) {
                var transaction = new Transaction();

                transaction.setAmount(transactionRequest.getAmount());
                transaction.setSourceAccountId(sourceAccount.get().getId());
                transaction.setTargetAccountId(targetAccount.get().getId());
                transaction.setTargetOwnerName(targetAccount.get().getOwnerName());
                transaction.setInitiationDate(LocalDateTime.now());
                transaction.setCompletionDate(LocalDateTime.now());
                transaction.setReference(transactionRequest.getReference());
                transaction.setLatitude(transactionRequest.getLatitude());
                transaction.setLongitude(transactionRequest.getLongitude());

                updateAccountBalance(sourceAccount.get(), transactionRequest.getAmount(), ACTION.WITHDRAW);
                updateAccountBalance(targetAccount.get(), transactionRequest.getAmount(), ACTION.DEPOSIT);
                transactionRepository.save(transaction);

                return true;
            }
        }
        return false;
    }

    public Account updateAccountBalance(Account account, double amount, ACTION action) {
        // This method is used to update the balance of account using action type(DEPOSIT OR WITHDRAW).
        if (action == ACTION.WITHDRAW) {
            account.setCurrentBalance((account.getCurrentBalance() - amount));
        } else if (action == ACTION.DEPOSIT) {
            account.setCurrentBalance((account.getCurrentBalance() + amount));
        }
        return accountRepository.save(account);
    }

    // TODO support overdrafts or credit account
    public boolean isAmountAvailable(double amount, double accountBalance) {
        // This validation is used before doing any withdrawal from the account.
        return (accountBalance - amount) > 0;
    }
}
