package com.banking.simplebankapp.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "account")
// This entity is used to store the account details of a customer/user.
public class Account {
    @Id
    @GeneratedValue
    private long id;

    private String sortCode; // random code to sort the account

    private String accountNumber;

    private double currentBalance;

    private String bankName;

    private String ownerName;

    private transient List<Transaction> transactions;
}
