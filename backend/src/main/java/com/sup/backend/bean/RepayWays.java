package com.sup.backend.bean;

import com.alibaba.fastjson.JSONArray;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * Created by xidongzhou1 on 2020/5/22.
 */
@Data
@Accessors(chain = true)
public class RepayWays {
  JSONArray manual_repay_configs;
  JSONArray auto_repay_configs;
  JSONArray vc_repay_configs;
}
