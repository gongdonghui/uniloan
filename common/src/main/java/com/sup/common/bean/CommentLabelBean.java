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
@TableName("tb_core_comment_label")
public class CommentLabelBean {
    @TableId(type = IdType.AUTO)
    private Integer id;
    private String label_name;
    private String content;
    private String scene;
    private Date creat_time;
    private Integer creator;
}
