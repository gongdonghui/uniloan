package com.sup.common.loan;

/**
 * Project:uniloan
 * Class:  OperationTaskTypeEnum
 * <p>
 * Author: guanfeng
 * Create: 2019-09-12
 */

public enum OperationTaskTypeEnum {
    // 任务类型：0:初审，1:复审，2:终审，3:逾期（未还）

    TASK_FIRST_AUDIT(0, "初审任务"),
    TASK_SECOND_AUDIT(1, "复审任务"),
    TASK_FINAL_AUDIT(2, "终审任务"),
    TASK_OVERDUE(3, "催收任务");    // 包含预催收、逾期催收

    private int code;
    private String codeDesc;

    OperationTaskTypeEnum(int code, String desc) {
        this.code = code;
        this.codeDesc = desc;
    }

    public int getCode() {
        return this.code;
    }

    public String getCodeDesc() {
        return this.codeDesc;
    }

    public static OperationTaskTypeEnum getStatusByCode(int code) {
        for (OperationTaskTypeEnum responseCode : OperationTaskTypeEnum.values()) {
            if (responseCode.getCode() == code) {
                return responseCode;
            }
        }
        return null;
    }
}
