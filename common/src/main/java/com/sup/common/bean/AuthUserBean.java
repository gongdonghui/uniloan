package com.sup.common.bean;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

/**
 * @Author: kouichi
 * @Date: 2019/10/3 16:35
 */
@Data
@TableName("tb_cms_auth_user")
public class AuthUserBean {
    @TableId(type = IdType.AUTO)
    private Integer id;
    @TableField(value = "user_name")
    private String userName;
    private String password;
    private String name;
    private String mobile;
    private String email;
    private String comment;
    @TableField(value = "group_id")
    private Integer  groupId;
    @TableField(value = "is_valid")
    private Integer isValid;
    @TableField(value = "create_time")
    private Date createTime;
    @TableField(value = "update_time")
    private Date updateTime;
}
