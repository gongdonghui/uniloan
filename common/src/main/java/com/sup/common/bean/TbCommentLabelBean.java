package com.sup.common.bean;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

/**
 * gongshuai
 * <p>
 * 2019/9/22
 */
@Data
@TableName("tb_core_comment_label")
public class TbCommentLabelBean{
    @TableId(type = IdType.AUTO)
    private Integer id;
    private String content;
    private String label_name;
    private Integer creator;
    private String scene;
    private Date create_time;
}
