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
public class AppApplyOverView {
  private Integer apply_id;    // 订单号
  private String status;
  private Boolean need_to_repay;
  private String amount;
  private String period;
  private String rate;
  private String apply_time;
  private String jieqing_amount;
  private String jieqing_date;
}
