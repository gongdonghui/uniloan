package com.sup.cms.bean.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

/**
 * @Author: kouichi
 * @Date: 2019/10/2 16:35
 */
@Data
@TableName("tb_cms_collection_allocate_record")
public class CollectionAllocateRecordBean {
    @TableId(type = IdType.AUTO)
    private Integer id;
    @TableField(value = "action_time")
    private Date actionTime;
    @TableField(value = "apply_id")
    private Integer applyId;
    @TableField(value = "distributor_name")
    private String distributorName;
    @TableField(value = "collector_name")
    private String collectorName;
    @TableField(value = "distributor_id")
    private Integer distributorId;
    @TableField(value = "collector_id")
    private Integer collectorId;
    @TableField(value = "create_time")
    private Date createTime;
}
