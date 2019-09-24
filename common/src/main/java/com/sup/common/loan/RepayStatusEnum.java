package com.sup.common.loan;

public enum RepayStatusEnum {
  // 还款状态 0:处理中 1:还款成功 2:还款失败

  REPAY_STATUS_PROCESSING(0, "处理中"),
  REPAY_STATUS_SUCCEED(1, "还款成功"),
  REPAY_STATUS_FAILED(2, "还款失败");

  private int code;
  private String codeDesc;

  RepayStatusEnum(int code, String desc) {
    this.code = code;
    this.codeDesc = desc;
  }
  public int getCode() {
    return this.code;
  }
  public String getCodeDesc() {
    return this.codeDesc;
  }
  public static RepayStatusEnum getStatusByCode(int code) {
    for (RepayStatusEnum responseCode : RepayStatusEnum.values()) {
      if (responseCode.getCode() == code) {
        return responseCode;
      }
    }
    return null;
  }
}
