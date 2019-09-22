package com.sup.cms.bean.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

/**
 * @Author: kouichi
 * @Date: 2019/9/18 16:47
 */
@Data
@TableName("tb_product_info")
public class ProductInfoBean {
    @TableId(type = IdType.AUTO)
    private Integer id;
    private String name;
    private String productDesc;
    private Integer status;
    private Float rate;
    private Integer minPeriod;
    private Integer maxPeriod;
    private Integer minQuota;
    private Integer MaxQuota;
    private Integer periodType;
    private Integer valueDateType;
    private Float fee;
    private Integer feeType;
    private Float overdueRate;
    private Integer gracePeriod;
    private Integer productOrder;
    private Integer creditClassId;
    private String materialNeeded;
    private Date createTime;
}
