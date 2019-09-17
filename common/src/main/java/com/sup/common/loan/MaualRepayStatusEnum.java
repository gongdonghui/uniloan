package com.sup.common.loan;

public enum MaualRepayStatusEnum {
  TO_BE_CONFIRM(0, "待确认"),
  MANUAL_REPAY_SUCC(1, "手动还款成功"),
  MANUAL_REPAY_FAIL(2, "手动还款失败");

  private int code;
  private String codeDesc;

  MaualRepayStatusEnum(int code, String desc) {
    this.code = code;
    this.codeDesc = desc;
  }
  public int getCode() {
    return this.code;
  }
  public String getCodeDesc() {
    return this.codeDesc;
  }
  public static MaualRepayStatusEnum getStatusByCode(int code) {
    for (MaualRepayStatusEnum responseCode : MaualRepayStatusEnum.values()) {
      if (responseCode.getCode() == code) {
        return responseCode;
      }
    }
    return null;
  }
}
