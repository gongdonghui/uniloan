package com.sup.common.loan;

/**
 * Project:uniloan
 * Class:  AssetLevelEnum
 * <p>
 * Author: guanfeng
 * Create: 2019-09-12
 */

public enum DocumentaryImageUploadTypeEnum {
    UPLOAD_BY_USER_NORMAL(0, "用户正常提交"),
    UPLAOD_BY_CREDIT(1, "信审人员补充提交"),
    UPLOAD_BY_USER_AFTER_APPLY(2, "用户后提交");

    private int code;
    private String codeDesc;

    DocumentaryImageUploadTypeEnum(int code, String desc) {
        this.code = code;
        this.codeDesc = desc;
    }

    public int getCode() {
        return this.code;
    }

    public String getCodeDesc() {
        return this.codeDesc;
    }

    public static DocumentaryImageUploadTypeEnum getStatusByCode(int code) {
        for (DocumentaryImageUploadTypeEnum responseCode : DocumentaryImageUploadTypeEnum.values()) {
            if (responseCode.getCode() == code) {
                return responseCode;
            }
        }
        return null;
    }
}
