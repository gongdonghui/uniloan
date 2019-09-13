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
@TableName("tb_product_info")
public class TbProductInfoBean {
  @TableId(type = IdType.AUTO)
  private Integer id;
  private String name;
  private String type;
  private String status;
  private String rate;
  private String period;
  private String quota;
  private String material_needed;
  private Date create_time;
}
