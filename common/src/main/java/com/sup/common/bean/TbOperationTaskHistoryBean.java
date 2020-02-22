package com.sup.common.bean;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.util.Date;

@Data
@TableName("tb_operation_task_history")
public class TbOperationTaskHistoryBean {
    @TableId(type = IdType.AUTO)
    private Integer id;
    private Integer apply_id;

    private Integer operator_id;
    private Integer distributor_id;
    private Integer task_type;      // 详见OperationTaskTypeEnum
    private Integer status;         // 详见OperationTaskStatusEnum
    private Integer has_owner;      // 是否已分配（被指派或者领取） 0:不是，1:是
    private String  comment;
    private Date    create_time;
    private Date    expire_time;
    private Date    update_time;

    /**
     * copy data except id
     * @param bean
     */
    public void copy(TbOperationTaskBean bean) {
        apply_id = bean.getApply_id();
        operator_id = bean.getOperator_id();
        distributor_id = bean.getDistributor_id();
        task_type = bean.getTask_type();
        status = bean.getStatus();
        has_owner = bean.getHas_owner();
        comment = bean.getComment();
        create_time = bean.getCreate_time();
        expire_time = bean.getExpire_time();
        update_time = bean.getUpdate_time();
    }
}
