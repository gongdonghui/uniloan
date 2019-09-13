package com.sup.common.loan;

/**
 * Project:uniloan
 * Class:  ApplyStatusEnum
 * <p>
 * Author: guanfeng
 * Create: 2019-09-12
 */

public enum  ApplyStatusEnum {
    // 进件状态:  0:待审核, 1:自动审核通过, 2:初审通过, 3:复审通过, 4:终审通过,
    //           5:自动审核拒绝, 6:初审拒绝, 7:复审拒绝, 8:终审拒绝, 9:取消或异常

    APPLY_INIT(0x00000000, "进件初始化"),
    APPLY_AUTO_PASS(0x00000001, "自动审核通过"),
    APPLY_FIRST_PASS(0x00000002, "初审通过"),
    APPLY_SECOND_PASS(0x00000003, "复审通过"),
    APPLY_FINAL_PASS(0x00000004, "终审通过"),
    APPLY_AUTO_DENY(0x00000005, "自动审核拒绝"),
    APPLY_FIRST_DENY(0x00000006, "初审拒绝"),
    APPLY_SECOND_DENY(0x00000007, "复审拒绝"),
    APPLY_FINAL_DENY(0x00000008, "终审拒绝"),
    APPLY_CANCEL(0x00000009, "取消");

    private int code;
    private String codeDesc;

    ApplyStatusEnum(int code, String desc) {
        this.code = code;
        this.codeDesc = desc;
    }

    public int getCode() {
        return this.code;
    }

    public String getCodeDesc() {
        return this.codeDesc;
    }

    public static ApplyStatusEnum getStatusByCode(int code) {
        for (ApplyStatusEnum responseCode : ApplyStatusEnum.values()) {
            if (responseCode.getCode() == code) {
                return responseCode;
            }
        }
        return null;
    }
}
