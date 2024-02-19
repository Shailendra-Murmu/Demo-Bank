package com.banking.simplebankapp.utilities;

import com.banking.simplebankapp.constants.COMMON_CONSTANT;
import com.banking.simplebankapp.dto.AccountDetails;
import com.banking.simplebankapp.dto.requests.CreateAccountRequest;
import com.banking.simplebankapp.dto.requests.TransactionRequest;

public class InputValidator {
    public static boolean isSearchCriteriaValid(AccountDetails accountInput) {
        return COMMON_CONSTANT.SORT_CODE_PATTERN.matcher(accountInput.getSortCode()).find() &&
                COMMON_CONSTANT.ACCOUNT_NUMBER_PATTERN.matcher(accountInput.getAccountNumber()).find();
    }

    public static boolean isAccountNoValid(String accountNo) {
        return COMMON_CONSTANT.ACCOUNT_NUMBER_PATTERN.matcher(accountNo).find();
    }

    public static boolean isCreateAccountCriteriaValid(CreateAccountRequest createAccountRequest) {
        return (!createAccountRequest.getBankName().isBlank() && !createAccountRequest.getOwnerName().isBlank());
    }

    public static boolean isSearchTransactionValid(TransactionRequest transactionRequest) {
        if (!isSearchCriteriaValid(transactionRequest.getSourceAccount()))
            return false;

        if (!isSearchCriteriaValid(transactionRequest.getTargetAccount()))
            return false;

        if (transactionRequest.getSourceAccount().equals(transactionRequest.getTargetAccount()))
            return false;

        return true;
    }
}
