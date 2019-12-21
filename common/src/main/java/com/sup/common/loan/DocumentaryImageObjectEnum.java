package com.sup.common.loan;

/**
 * Project:uniloan
 * Class:  AssetLevelEnum
 * <p>
 * Author: guanfeng
 * Create: 2019-09-12
 */

public enum DocumentaryImageObjectEnum {
    IDC_HEAD(0, "身份证正面"),
    IDC_TAIL(1, "身份证反面"),
    OWNER_WITH_IDC(2, "手持身份证"),
    WORK_ENV(3, "工作环境"),
    DRIVER_LICENSE(4, "驾驶证"),
    SOCIAL_CARD(5, "医保证"),
    REPAY_RECORED(6, "还款记录"),
    OTHERS(7, "其他");

    private int code;
    private String codeDesc;

    DocumentaryImageObjectEnum(int code, String desc) {
        this.code = code;
        this.codeDesc = desc;
    }

    public int getCode() {
        return this.code;
    }

    public String getCodeDesc() {
        return this.codeDesc;
    }

    public static DocumentaryImageObjectEnum getStatusByCode(int code) {
        for (DocumentaryImageObjectEnum responseCode : DocumentaryImageObjectEnum.values()) {
            if (responseCode.getCode() == code) {
                return responseCode;
            }
        }
        return null;
    }
}
