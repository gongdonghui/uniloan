package com.sup.common.loan;

/**
 * Project:uniloan
 * Class:  AssetLevelEnum
 * <p>
 * Author: guanfeng
 * Create: 2019-09-12
 */

public enum AssetLevelEnum {
    // 资产等级，

    // TODO
    ASSET_LEVEL_0(0, ""),
    ASSET_LEVEL_1(1, ""),
    ASSET_LEVEL_2(2, ""),
    ASSET_LEVEL_3(3, ""),
    ASSET_LEVEL_4(4, "");

    private int code;
    private String codeDesc;

    AssetLevelEnum(int code, String desc) {
        this.code = code;
        this.codeDesc = desc;
    }

    public int getCode() {
        return this.code;
    }

    public String getCodeDesc() {
        return this.codeDesc;
    }

    public static AssetLevelEnum getStatusByCode(int code) {
        for (AssetLevelEnum responseCode : AssetLevelEnum.values()) {
            if (responseCode.getCode() == code) {
                return responseCode;
            }
        }
        return null;
    }
}
