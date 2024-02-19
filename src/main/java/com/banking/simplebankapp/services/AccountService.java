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
        // This method is used to fetch users account details along with their transaction records.
        Optional<Account> account = accountRepository
                .findByAccountNumberAndSortCode(accountNumber, sortCode);

        account.ifPresent(value ->
                value.setTransactions(transactionRepository
                        .findBySourceAccountIdOrTargetAccountIdOrderByInitiationDate(value.getId(), value.getId())));

        return account.orElse(null);
    }

    public Account getAccount(String accountNumber) {
        // This method is used to fetch the users account details.
        Optional<Account> account = accountRepository
                .findByAccountNumber(accountNumber);

        return account.orElse(null);
    }

    public Account createAccount(String bankName, String ownerName) {
        // This method is used to create the new account of user in the system.
        CodeGenerator codeGenerator = new CodeGenerator();
        Account newAccount = Account.builder().bankName(bankName).ownerName(ownerName).sortCode(codeGenerator.generateSortCode())
                .accountNumber(codeGenerator.generateAccountNumber()).currentBalance(0.00).build();
        return accountRepository.save(newAccount);
    }
}
