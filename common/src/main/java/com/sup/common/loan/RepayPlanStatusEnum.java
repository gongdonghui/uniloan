package com.sup.common.loan;

public enum RepayPlanStatusEnum {
  PLAN_NOT_PAID(0, "未还"),
  PLAN_PAID_PART(1, "未还清"),
  PLAN_PAID_ALL(1, "已还清");

  private int code;
  private String codeDesc;

  RepayPlanStatusEnum(int code, String desc) {
    this.code = code;
    this.codeDesc = desc;
  }
  public int getCode() {
    return this.code;
  }
  public String getCodeDesc() {
    return this.codeDesc;
  }
  public static RepayPlanStatusEnum getStatusByCode(int code) {
    for (RepayPlanStatusEnum responseCode : RepayPlanStatusEnum.values()) {
      if (responseCode.getCode() == code) {
        return responseCode;
      }
    }
    return null;
  }
}
