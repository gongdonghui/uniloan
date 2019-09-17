package com.sup.common.bean;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.math.BigInteger;
import java.util.Date;

/**
 * Created by xidongzhou1 on 2019/9/11.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
@TableName("tb_manual_repay")
public class TbManualRepayBean {
  @TableId(type = IdType.AUTO)
  private Integer id;
  private Integer plan_id;
  private Integer apply_id;
  private Integer user_id;
  private Integer seq_no;
  private Date repay_start_date;
  private Date repay_end_date;
  private Integer is_overdue;
  private BigInteger need_total;
  private BigInteger act_total;
  private String repay_image;
  private String trade_no;
  private Integer status;
  private Date create_time;
  private Date update_time;
}
