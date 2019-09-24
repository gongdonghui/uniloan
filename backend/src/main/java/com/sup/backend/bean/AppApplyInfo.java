package com.sup.backend.bean;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * Created by xidongzhou1 on 2019/9/11.
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
public class AppApplyInfo {
  private Integer apply_id;    // 订单号
  private Integer plan_id;   // 期数 Id
  private Integer contract_amount;
  private String total_amount_to_be_repaid;  // 总计要还
  private String curr_amount_to_be_repaid;  // 这期要换
  private String total_period; // 总期数
  private String period; // 当前期数
  private Integer is_overdue; // 是否逾期
  private String latest_repay_date; // 最近还款日
  private String dest_account_no;  // 打款账号
  private String trade_no;
  private String repay_img;   // 手动上传的打款凭证，需要人工审核
}
