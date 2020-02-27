package com.sup.common.bean;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

/**
 * gongshuai
 * <p>
 * 2020/2/22
 */
@Data
@TableName("tb_cms_operation_task_config")
public class OperationTaskConfigBean {
    @TableId
    private Integer id;
    private Integer group_id;
    private Integer credit_level;
    private String strategy;
    private boolean enabled;
    private Date create_time;

}
