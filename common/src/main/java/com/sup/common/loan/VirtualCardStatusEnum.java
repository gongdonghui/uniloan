package com.sup.common.loan;

/**
 * Project:uniloan
 * Class:  ProductStatusEnum
 * <p>
 * Author: guanfeng
 * Create: 2019-09-12
 */

public enum VirtualCardStatusEnum {
    // 虚拟卡状态, 0:已销毁, 1:可用

    VC_STATUS_INVALID(0, "不可用"),
    VC_STATUS_VALID(1, "可用");

    private int code;
    private String codeDesc;

    VirtualCardStatusEnum(int code, String desc) {
        this.code = code;
        this.codeDesc = desc;
    }

    public int getCode() {
        return this.code;
    }

    public String getCodeDesc() {
        return this.codeDesc;
    }

    public static VirtualCardStatusEnum getStatusByCode(int code) {
        for (VirtualCardStatusEnum responseCode : VirtualCardStatusEnum.values()) {
            if (responseCode.getCode() == code) {
                return responseCode;
            }
        }
        return null;
    }
}
