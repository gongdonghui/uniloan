package com.sup.common.bean;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

/**
 * Project:uniloan
 * Class:  TbApplyInfoBean
 * <p>
 * Author: guanfeng
 * Create: 2019-09-05
 */

@Data
@TableName("tb_market_plan")
public class TbMarketPlanBean {
  @TableId(type = IdType.AUTO)
  private Integer id;         // apply_i
  private String topic;
  private String tag;
  private String market_way;
  private String market_ext;
  private Integer priority;
  private Integer status;
  private Date create_time;
  private Date update_time;
}
