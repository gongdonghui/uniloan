package com.sup.common.bean;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

@Data
@TableName("tb_operation_log")
public class TbOperationLogBean {
    @TableId(type = IdType.AUTO)
    private Integer id;
    private Integer apply_id;
    private Integer operator_id;
    private String  operator_name;
    private Integer operation_type;      // 操作类型，0:初审，1:复审，2:终审，3:催收, 详见OperationLogTypeEnum
    private String  comment;
    private Date    create_time;
}
