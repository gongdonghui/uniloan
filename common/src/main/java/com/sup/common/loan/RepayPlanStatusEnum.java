package com.sup.common.loan;

public enum RepayPlanStatusEnum {
  PLAN_NOT_PAID(0, "未还"),
  PLAN_PAID_PART(1, "未还清"),
  PLAN_PAID_ALL(2, "已还清"),
  PLAN_PAID_PROCESSING(3, "自助还款处理中"),
  PLAN_PAID_ERROR(4, "自助还款处理失败"),     // 无需自助还款成功状态：还款成功时会更新还款计划，状态变更为1或者2
  PLAN_PAID_WRITE_OFF(5, "核销");

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
