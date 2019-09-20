package com.sup.common.bean;

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
@TableName("tb_product_info")
public class TbProductInfoBean {
  @TableId(type = IdType.AUTO)
  private Integer id;
  private String name;
  private String product_desc;
  private Integer status;
  private Float   rate;
  private Integer min_quota;
  private Integer max_quota;
  private Integer min_period;
  private Integer max_period;
  private Integer period_type;  // 0:天，1:月，2:年
  private Integer value_date_type; // 起息日方式，0:到账后计息，1:终审通过后计息
  private Float   fee;
  private Integer fee_type;     // 服务费收取方式，0:先扣除服务费，1:先扣除服务费和利息，2:到期扣除服务费和利息
  private Float   overdue_rate;  // 逾期日费率
  private Integer grace_period;  // 宽限期
  private Integer product_order;        // 排列顺序
  private Integer credit_class_id;        // 信用等级id
  private String material_needed;
  private Date create_time;
}
