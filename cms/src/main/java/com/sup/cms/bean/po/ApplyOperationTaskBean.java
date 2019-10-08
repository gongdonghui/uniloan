package com.sup.cms.bean.po;

import com.baomidou.mybatisplus.annotation.IdType;
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
    private Integer applyId;
    private Integer operatorId;
    private Integer distributorId;
    private Integer taskType;
    private Integer status;
    private String comment;
    private Integer hasOwner;
    private Date createTime;
    private Date expireTime;
    private Date updateTime;

}
