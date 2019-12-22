package com.sup.common.loan;

/**
 * Project:uniloan
 * Class:  OperationTaskTypeEnum
 * <p>
 * Author: guanfeng
 * Create: 2019-09-12
 */

public enum OperationLogTypeEnum {
    // 任务类型：0:初审，1:复审，2:终审，3:催收

    TASK_FIRST_AUDIT(0, "初审"),
    TASK_SECOND_AUDIT(1, "复审"),
    TASK_FINAL_AUDIT(2, "终审"),
    TASK_OVERDUE(3, "催收");

    private int code;
    private String codeDesc;

    OperationLogTypeEnum(int code, String desc) {
        this.code = code;
        this.codeDesc = desc;
    }

    public int getCode() {
        return this.code;
    }

    public String getCodeDesc() {
        return this.codeDesc;
    }

    public static OperationLogTypeEnum getStatusByCode(int code) {
        for (OperationLogTypeEnum responseCode : OperationLogTypeEnum.values()) {
            if (responseCode.getCode() == code) {
                return responseCode;
            }
        }
        return null;
    }
}
