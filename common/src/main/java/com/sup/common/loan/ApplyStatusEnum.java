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
    //           10:自动放款中，11:自动放款失败，12:已放款/还款中，13:未还清，14:已还清，
    //           15:逾期，16:核销

    APPLY_INIT(0, "进件初始化"),
    APPLY_AUTO_PASS(1, "自动审核通过"),
    APPLY_FIRST_PASS(2, "初审通过"),
    APPLY_SECOND_PASS(3, "复审通过"),
    APPLY_FINAL_PASS(4, "终审通过"),
    APPLY_AUTO_DENY(5, "自动审核拒绝"),
    APPLY_FIRST_DENY(6, "初审拒绝"),
    APPLY_SECOND_DENY(7, "复审拒绝"),
    APPLY_FINAL_DENY(8, "终审拒绝"),
    APPLY_CANCEL(9, "取消"),
    APPLY_AUTO_LOANING(10, "自动放款中"),
    APPLY_AUTO_LOAN_FAILED(11, "自动放款失败"),
    APPLY_LOAN_SUCC(12, "已放款"),
    APPLY_REPAY_PART(13, "未还清"),
    APPLY_REPAY_ALL(14, "已还清"),
    APPLY_OVERDUE(15, "逾期"),
    APPLY_WRITE_OFF(16, "核销");


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
