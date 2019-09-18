package com.sup.core.status;

/**
 * Project:uniloan
 * Class:  DecisionEngineStatusEnum
 * <p>
 * Author: guanfeng
 * Create: 2019-09-12
 */

public enum DecisionEngineStatusEnum {
    // 自动审核状态:  0:通过, 1:拒绝

    APPLY_DE_AUTO_PASS(0, "自动审核通过"),
    APPLY_DE_AUTO_DENY(1, "自动审核拒绝");


    private int code;
    private String codeDesc;

    DecisionEngineStatusEnum(int code, String desc) {
        this.code = code;
        this.codeDesc = desc;
    }

    public int getCode() {
        return this.code;
    }

    public String getCodeDesc() {
        return this.codeDesc;
    }

    public static DecisionEngineStatusEnum getStatusByCode(int code) {
        for (DecisionEngineStatusEnum responseCode : DecisionEngineStatusEnum.values()) {
            if (responseCode.getCode() == code) {
                return responseCode;
            }
        }
        return null;
    }
}
