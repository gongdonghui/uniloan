package com.sup.core.status;

/**
 * Project:uniloan
 * Class:  ApplyStatusEnum
 * <p>
 * Author: guanfeng
 * Create: 2019-09-12
 */

public enum BlackListStatusEnum {
    // 黑名单状态:  0:正常，1:灰名单, 2:黑名单

    BL_NORMAL(0, "正常"),
    BL_GRAY(1, "灰名单"),
    BL_BLACK(2, "黑名单");


    private int code;
    private String codeDesc;

    BlackListStatusEnum(int code, String desc) {
        this.code = code;
        this.codeDesc = desc;
    }

    public int getCode() {
        return this.code;
    }

    public String getCodeDesc() {
        return this.codeDesc;
    }

    public static BlackListStatusEnum getStatusByCode(int code) {
        for (BlackListStatusEnum responseCode : BlackListStatusEnum.values()) {
            if (responseCode.getCode() == code) {
                return responseCode;
            }
        }
        return null;
    }
}
