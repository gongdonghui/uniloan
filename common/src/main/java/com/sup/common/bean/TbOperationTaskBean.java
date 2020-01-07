package com.sup.common.bean;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.util.Date;

@Data
@TableName("tb_operation_task")
public class TbOperationTaskBean {
    @TableId(type = IdType.AUTO)
    private Integer id;
    private Integer apply_id;

    @TableField(strategy= FieldStrategy.IGNORED)
    private Integer operator_id;    // 任务回收时忽略null判断，即可设置为null
    private Integer distributor_id;
    private Integer task_type;      // 详见OperationTaskTypeEnum
    private Integer status;         // 详见OperationTaskStatusEnum
    private Integer has_owner;      // 是否已分配（被指派或者领取） 0:不是，1:是
    private String  comment;
    private Date    create_time;
    private Date    expire_time;
    private Date    update_time;
}
