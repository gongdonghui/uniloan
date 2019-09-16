package com.sup.common.loan;

/**
 * Project:uniloan
 * Class:  LoanFeeTypeEnum
 * <p>
 * Author: guanfeng
 * Create: 2019-09-12
 */

public enum LoanValueDateTypeEnum {
    // 起息日方式，0:到账后计息，1:终审通过后计息

    LOAN_VALUE_DATE_AFTER_LOAN(0, "到账后计息"),
    LOAN_VALUE_DATE_AFTER_PASS(1, "终审通过后计息");

    private int code;
    private String codeDesc;

    LoanValueDateTypeEnum(int code, String desc) {
        this.code = code;
        this.codeDesc = desc;
    }

    public int getCode() {
        return this.code;
    }

    public String getCodeDesc() {
        return this.codeDesc;
    }

    public static LoanValueDateTypeEnum getStatusByCode(int code) {
        for (LoanValueDateTypeEnum responseCode : LoanValueDateTypeEnum.values()) {
            if (responseCode.getCode() == code) {
                return responseCode;
            }
        }
        return null;
    }
}
