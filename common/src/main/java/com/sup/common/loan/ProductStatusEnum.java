package com.sup.common.loan;

/**
 * Project:uniloan
 * Class:  ProductStatusEnum
 * <p>
 * Author: guanfeng
 * Create: 2019-09-12
 */

public enum ProductStatusEnum {
    // 产品状态, 0:offline, 1:online

    PRODUCT_STATUS_ONLINE(0, "下线状态"),
    PRODUCT_STATUS_OFFLINE(1, "上线状态");

    private int code;
    private String codeDesc;

    ProductStatusEnum(int code, String desc) {
        this.code = code;
        this.codeDesc = desc;
    }

    public int getCode() {
        return this.code;
    }

    public String getCodeDesc() {
        return this.codeDesc;
    }

    public static ProductStatusEnum getStatusByCode(int code) {
        for (ProductStatusEnum responseCode : ProductStatusEnum.values()) {
            if (responseCode.getCode() == code) {
                return responseCode;
            }
        }
        return null;
    }
}
