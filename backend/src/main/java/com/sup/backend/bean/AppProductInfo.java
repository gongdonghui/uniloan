package com.sup.backend.bean;

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
public class AppProductInfo {
  @TableId(type = IdType.AUTO)
  private Integer id;
  private String name;
  private String desc;
  private Integer status;
  private String   rate;
  private Integer min_quota;
  private Integer max_quota;
  private Integer min_period;
  private Integer max_period;
  private Integer period_type;  // 0:天，1:月，2:年
  private String material_needed;
}
