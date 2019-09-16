package com.sup.common.loan;

/**
 * Project:uniloan
 * Class:  LoanValueDateTypeEnum
 * <p>
 * Author: guanfeng
 * Create: 2019-09-12
 */

public enum LoanFeeTypeEnum {
    // 服务费收取方式，0:先扣除服务费，1:先扣除服务费和利息，2:到期扣除服务费和利息

    LOAN_PRE_FEE(0, "先扣除服务费"),
    LOAN_PRE_FEE_PRE_INTEREST(1, "先扣除服务费和利息"),
    LOAN_POST_FEE_POST_INTEREST(2, "到期扣除服务费和利息");

    private int code;
    private String codeDesc;

    LoanFeeTypeEnum(int code, String desc) {
        this.code = code;
        this.codeDesc = desc;
    }

    public int getCode() {
        return this.code;
    }

    public String getCodeDesc() {
        return this.codeDesc;
    }

    public static LoanFeeTypeEnum getStatusByCode(int code) {
        for (LoanFeeTypeEnum responseCode : LoanFeeTypeEnum.values()) {
            if (responseCode.getCode() == code) {
                return responseCode;
            }
        }
        return null;
    }
}
