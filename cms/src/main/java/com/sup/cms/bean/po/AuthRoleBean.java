package com.sup.cms.bean.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

/**
 * @Author: kouichi
 * @Date: 2019/10/3 16:36
 */
@Data
@TableName("tb_cms_auth_role")
public class AuthRoleBean {
    @TableId(type = IdType.AUTO)
    private Integer id;
    private String name;
    private String comment;
    @TableField(value = "is_valid")
    private Integer isValid;
    @TableField(value = "create_time")
    private Date createTime;
    @TableField(value = "update_time")
    private Date updateTime;
}
