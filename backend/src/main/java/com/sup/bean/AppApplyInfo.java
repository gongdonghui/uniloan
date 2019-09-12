package com.sup.bean;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.util.Date;

/**
 * Created by xidongzhou1 on 2019/9/11.
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
public class AppApplyInfo {
  private Integer order_id;    // 订单号
  private Integer period_id;   // 期数 Id
  private String total_amount_to_be_repaid;  // 总计要还
  private String curr_amount_to_be_repaid;  // 这期要换
  private String period; // 当前期数
  private Integer is_overdue; // 是否逾期
  private String latest_repay_date; // 最近还款日
  private String dest_account_no;  // 打款账号
}
