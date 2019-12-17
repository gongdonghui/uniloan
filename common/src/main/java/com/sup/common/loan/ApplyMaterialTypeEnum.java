package com.sup.common.loan;

public enum ApplyMaterialTypeEnum {
    // 申请资料类型：0|身份证信息  1|基本信息 2|紧急联系人 3|职业信息 4|银行卡信息

    APPLY_MATERIAL_IDC(0, "身份证信息"),
    APPLY_MATERIAL_BASIC(1, "基本信息"),
    APPLY_MATERIAL_CONTACT(2, "紧急联系人"),
    APPLY_MATERIAL_EMPLOYMENT(3, "职业信息"),
    APPLY_MATERIAL_BANK(4, "银行卡信息"),
    APPLY_MATERIAL_SDK_LOCATION_INFO(5, "SDK location"),
    APPLY_MATERIAL_SDK_CONTACT_LIST(6, "SDK contact"),
    APPLY_MATERIAL_SDK_APP_LIST(7, "SDK_applist");

    private int code;
    private String codeDesc;

    ApplyMaterialTypeEnum(int code, String desc) {
        this.code = code;
        this.codeDesc = desc;
    }

    public int getCode() {
        return this.code;
    }

    public String getCodeDesc() {
        return this.codeDesc;
    }

    public static LoanFeeTypeEnum getStatusByCode(int code) {
        for (LoanFeeTypeEnum responseCode : LoanFeeTypeEnum.values()) {
            if (responseCode.getCode() == code) {
                return responseCode;
            }
        }
        return null;
    }
}
