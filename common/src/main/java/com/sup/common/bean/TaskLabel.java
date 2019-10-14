package com.sup.common.bean;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

/**
 * gongshuai
 * <p>
 * 2019/10/14
 */
@Data
@TableName("tb_cms_task_label")
public class TaskLabel {
    @TableId(type = IdType.AUTO)
    private Integer id;         // apply_id
    private String label;
    private String description;
    private String scene;
    private Date create_time;
    private String creator;
}
