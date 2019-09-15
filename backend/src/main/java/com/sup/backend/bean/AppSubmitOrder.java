package com.sup.backend.bean;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Map;

/**
 * Created by xidongzhou1 on 2019/8/30.
 */
@Data
@TableName("activity_info")
public class AppSubmitOrder {
  private Integer product_id;
  private Map<Integer, String> material_ids;
}
