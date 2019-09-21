package com.sup.common.loan;

/**
 * Project:uniloan
 * Class:  OperationTaskStatusEnum
 * <p>
 * Author: guanfeng
 * Create: 2019-09-12
 */

public enum OperationTaskStatusEnum {
    // 任务状态：0:未审，1:已审，2:回收

    TASK_STATUS_NEW(0, "未审核"),
    TASK_STATUS_DONE(1, "已审核"),
    TASK_STATUS_CANCEL(2, "任务已回收");


    private int code;
    private String codeDesc;

    OperationTaskStatusEnum(int code, String desc) {
        this.code = code;
        this.codeDesc = desc;
    }

    public int getCode() {
        return this.code;
    }

    public String getCodeDesc() {
        return this.codeDesc;
    }

    public static OperationTaskStatusEnum getStatusByCode(int code) {
        for (OperationTaskStatusEnum responseCode : OperationTaskStatusEnum.values()) {
            if (responseCode.getCode() == code) {
                return responseCode;
            }
        }
        return null;
    }
}
