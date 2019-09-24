package com.sup.common.loan;

public enum RepayTypeEnum {
  // 还款类型 0:自助还款 1:手动还款 2:催收回款

  REPAY_TYPE_AUTO(0, "自助还款"),
  REPAY_TYPE_MANUAL(1, "手动还款"),
  REPAY_TYPE_OVERDUE(2, "催收回款");

  private int code;
  private String codeDesc;

  RepayTypeEnum(int code, String desc) {
    this.code = code;
    this.codeDesc = desc;
  }
  public int getCode() {
    return this.code;
  }
  public String getCodeDesc() {
    return this.codeDesc;
  }
  public static RepayTypeEnum getStatusByCode(int code) {
    for (RepayTypeEnum responseCode : RepayTypeEnum.values()) {
      if (responseCode.getCode() == code) {
        return responseCode;
      }
    }
    return null;
  }
}
