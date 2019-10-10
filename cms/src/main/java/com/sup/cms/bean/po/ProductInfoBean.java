package com.sup.cms.bean.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
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
    @TableField(value = "product_desc")
    private String productDesc;
    private Integer status;
    private Float rate;
    @TableField(value = "min_period")
    private Integer minPeriod;
    @TableField(value = "max_period")
    private Integer maxPeriod;
    @TableField(value = "min_quota")
    private Integer minQuota;
    @TableField(value = "max_quota")
    private Integer maxQuota;
    @TableField(value = "period_type")
    private Integer periodType;
    @TableField(value = "value_date_type")
    private Integer valueDateType;
    private Float fee;
    @TableField(value = "fee_type")
    private Integer feeType;
    @TableField(value = "overdue_rate")
    private Float overdueRate;
    @TableField(value = "grace_period")
    private Integer gracePeriod;
    @TableField(value = "product_order")
    private Integer productOrder;
    @TableField(value = "credit_level")
    private Integer creditLevel;
    @TableField(value = "material_needed")
    private String materialNeeded;
    @TableField(value = "create_time")
    private Date createTime;
}
