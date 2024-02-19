package com.banking.simplebankapp.services;

import com.banking.simplebankapp.entities.Account;
import com.banking.simplebankapp.repositories.AccountRepository;
import com.banking.simplebankapp.repositories.TransactionRepository;
import com.banking.simplebankapp.utilities.CodeGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AccountService {
    @Autowired
    private AccountRepository accountRepository;
    @Autowired
    private TransactionRepository transactionRepository;

    public Account getAccount(String sortCode, String accountNumber) {
        Optional<Account> account = accountRepository
                .findByAccountNumberAndSortCode(accountNumber, sortCode);

        account.ifPresent(value ->
                value.setTransactions(transactionRepository
                        .findBySourceAccountIdOrTargetAccountIdOrderByInitiationDate(value.getId(), value.getId())));

        return account.orElse(null);
    }

    public Account getAccount(String accountNumber) {
        Optional<Account> account = accountRepository
                .findByAccountNumber(accountNumber);

        return account.orElse(null);
    }

    public Account createAccount(String bankName, String ownerName) {
        CodeGenerator codeGenerator = new CodeGenerator();
        Account newAccount = Account.builder().bankName(bankName).ownerName(ownerName).sortCode(codeGenerator.generateSortCode())
                .accountNumber(codeGenerator.generateAccountNumber()).currentBalance(0.00).build();
        return accountRepository.save(newAccount);
    }
}
