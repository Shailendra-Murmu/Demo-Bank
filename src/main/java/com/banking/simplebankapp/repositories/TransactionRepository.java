package com.banking.simplebankapp.repositories;

import com.banking.simplebankapp.entities.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    List<Transaction> findBySourceAccountIdOrTargetAccountIdOrderByInitiationDate(long sourceId, long targetId);
}
