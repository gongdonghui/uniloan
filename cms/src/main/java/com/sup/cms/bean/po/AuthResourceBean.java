package com.sup.cms.bean.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.Date;

/**
 * @Author: kouichi
 * @Date: 2019/10/3 16:36
 */
@Data
@TableName("tb_cms_auth_resource")
public class AuthResourceBean {
    @TableId(type = IdType.AUTO)
    private Integer id;
    private String level1;
    private String level2;
    private String level3;
    @NotNull
    private String name;
    @NotNull
    private String url;
    private String comment;
    @TableField(value = "create_time")
    private Date createTime;
}
