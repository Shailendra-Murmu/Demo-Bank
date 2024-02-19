package com.banking.simplebankapp.repositories;

import com.banking.simplebankapp.entities.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {
    Optional<Account> findByAccountNumber(String accountNumber);
    Optional<Account> findByAccountNumberAndSortCode(String accountNumber, String sortCode);
}
