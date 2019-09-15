package com.sup.common.loan;

public enum RepayPlanOverdueEnum {
  PLAN_NOT_OVER_DUE(0, " 未逾期"),
  PLAN_OVER_DUE(1, "逾期");

  private int code;
  private String codeDesc;

  RepayPlanOverdueEnum(int code, String desc) {
    this.code = code;
    this.codeDesc = desc;
  }
  public int getCode() {
    return this.code;
  }
  public String getCodeDesc() {
    return this.codeDesc;
  }
  public static RepayPlanOverdueEnum getStatusByCode(int code) {
    for (RepayPlanOverdueEnum responseCode : RepayPlanOverdueEnum.values()) {
      if (responseCode.getCode() == code) {
        return responseCode;
      }
    }
    return null;
  }
}
