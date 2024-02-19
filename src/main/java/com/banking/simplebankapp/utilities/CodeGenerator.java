package com.banking.simplebankapp.utilities;

import com.mifmif.common.regex.Generex;

import static com.banking.simplebankapp.constants.COMMON_CONSTANT.ACCOUNT_NUMBER_PATTERN_STRING;
import static com.banking.simplebankapp.constants.COMMON_CONSTANT.SORT_CODE_PATTERN_STRING;

public class CodeGenerator {
    private final Generex sortCodeGenerex = new Generex(SORT_CODE_PATTERN_STRING);
    private final Generex accountNumberGenerex = new Generex(ACCOUNT_NUMBER_PATTERN_STRING);

    public CodeGenerator(){}

    public String generateSortCode() {
        return sortCodeGenerex.random();
    }

    public String generateAccountNumber() {
        return accountNumberGenerex.random();
    }
}
