package com.sup.common.loan;

public enum RepayHistoryStatusEnum {
  // 还款状态 0:处理中 1:还款成功 2:还款失败 3:费用减免

  REPAY_STATUS_PROCESSING(0, "处理中"),
  REPAY_STATUS_SUCCEED(1, "还款成功"),
  REPAY_STATUS_FAILED(2, "还款失败"),
  REPAY_STATUS_REDUCTION(3, "费用减免");

  private int code;
  private String codeDesc;

  RepayHistoryStatusEnum(int code, String desc) {
    this.code = code;
    this.codeDesc = desc;
  }
  public int getCode() {
    return this.code;
  }
  public String getCodeDesc() {
    return this.codeDesc;
  }
  public static RepayHistoryStatusEnum getStatusByCode(int code) {
    for (RepayHistoryStatusEnum responseCode : RepayHistoryStatusEnum.values()) {
      if (responseCode.getCode() == code) {
        return responseCode;
      }
    }
    return null;
  }
}
