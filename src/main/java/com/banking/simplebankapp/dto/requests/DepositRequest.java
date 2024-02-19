package com.banking.simplebankapp.dto.requests;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DepositRequest {
    @NotBlank(message = "Target account no is mandatory")
    private String targetAccountNo;

    @Positive(message = "Transfer amount must be positive")
    private double amount;
}
