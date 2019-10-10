package com.sup.cms.bean.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

/**
 * @Author: kouichi
 * @Date: 2019/9/22 11:13
 */
@Data
@TableName("tb_operation_task")
public class ApplyOperationTaskBean {
    @TableId(type = IdType.AUTO)
    private Integer id;
    @TableField(value = "apply_id")
    private Integer applyId;
    @TableField(value = "operator_id")
    private Integer operatorId;
    @TableField(value = "distributor_id")
    private Integer distributorId;
    @TableField(value = "task_type")
    private Integer taskType;
    private Integer status;
    private String comment;
    @TableField(value = "has_owner")
    private Integer hasOwner;
    @TableField(value = "create_time")
    private Date createTime;
    @TableField(value = "expire_time")
    private Date expireTime;
    @TableField(value = "update_time")
    private Date updateTime;

}
