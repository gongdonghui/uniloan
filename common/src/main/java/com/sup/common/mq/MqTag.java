package com.sup.common.mq;

/**
 * Created by xidongzhou1 on 2019/9/18.
 */
public class MqTag {
  // system_notify
  public static final String ISSUE_VERIFY_CODE = "issue_verify_code";
  public static final String ONEDAY_OVERDUE_NOTIFY = "oneday_overdue_notify";
  public static final String ONEDAY_TO_REPAY_NOTIFY = "oneday_to_repay_notify";

  public static final String DAY_TO_REPAY_NOTIFY = "day_to_repay_notify";
  public static final String ONE_DAY_OVERDUE_NOTIFY = "one_day_overdue_notify";
  public static final String TWO_DAY_OVERDUE_NOTIFY = "two_days_overdue_notify";

  public static final String APPLY_STATUS_CHANGE = "apply_status_change";
  public static final String REPAY_SUCC_NOTIFY = "repay_succ_notify";
  public static final String REPAY_FAIL_NOTIFY = "repay_fail_notify";
  public static final String REPAY_CLEAR_NOTIFY = "repay_clear_notify";
}
