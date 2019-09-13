package com.sup.backend.bean;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * Created by xidongzhou1 on 2019/8/30.
 */
@Data
@TableName("activity_info")
public class ActivityInfoBean {
  @TableId(type = IdType.AUTO)
  private Integer id;
  private String market_way;
}
