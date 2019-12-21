package com.sup.common.loan;

/**
 * Project:uniloan
 * Class:  AssetLevelEnum
 * <p>
 * Author: guanfeng
 * Create: 2019-09-12
 */

public enum DocumentaryImageCategoryEnum {
    // 资产等级，

    // TODO
    NORMAL(0, "常规"),
    EXTEND(1, "扩展凭证"),
    REPAY(2, "还款使用");

    private int code;
    private String codeDesc;

    DocumentaryImageCategoryEnum(int code, String desc) {
        this.code = code;
        this.codeDesc = desc;
    }

    public int getCode() {
        return this.code;
    }

    public String getCodeDesc() {
        return this.codeDesc;
    }

    public static DocumentaryImageCategoryEnum getStatusByCode(int code) {
        for (DocumentaryImageCategoryEnum responseCode : DocumentaryImageCategoryEnum.values()) {
            if (responseCode.getCode() == code) {
                return responseCode;
            }
        }
        return null;
    }
}
