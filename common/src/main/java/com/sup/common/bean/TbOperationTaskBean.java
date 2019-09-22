package com.sup.common.bean;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

@Data
@TableName("tb_market_plan")
public class TbOperationTaskBean {
    @TableId(type = IdType.AUTO)
    private Integer id;
    private Integer apply_id;
    private Integer operator_id;
    private Integer distributor_id;
    private Integer task_type;      // 详见OperationTaskTypeEnum
    private Integer status;         // 详见OperationTaskStatusEnum
    private String  comment;
    private Date    create_time;
    private Date    expire_time;
    private Date    update_time;
}
