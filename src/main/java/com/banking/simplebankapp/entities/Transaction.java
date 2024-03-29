package com.banking.simplebankapp.entities;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@Entity
@Table(name = "transaction")
@SequenceGenerator(name = "transaction_seq", sequenceName = "transaction_sequence")
// This entity is used to store the transaction details when user makes fund transfer.
public class Transaction {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "transaction_seq")
    private long id;

    private long sourceAccountId;

    private long targetAccountId;

    private String targetOwnerName;

    private double amount;

    private LocalDateTime initiationDate;

    private LocalDateTime completionDate;

    private String reference; // reference number

    private Double latitude;

    private Double longitude;
}
