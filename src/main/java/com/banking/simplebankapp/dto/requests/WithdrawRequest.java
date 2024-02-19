package com.banking.simplebankapp.dto.requests;

import com.banking.simplebankapp.dto.AccountDetails;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
public class WithdrawRequest extends AccountDetails {
    @Positive(message = "Transfer amount must be positive")
    private double amount;
}
