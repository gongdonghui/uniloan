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
  @TableId(type = IdType.AUTO)
  private Integer id;
  private String total_amount_to_be_repaid;
  private String curr_amount_to_be_repaid;
  private String period;
  private Integer is_overdue;
  private String latest_repay_date;
}
