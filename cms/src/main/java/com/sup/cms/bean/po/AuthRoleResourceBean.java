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
@TableName("tb_cms_auth_role_resource")
public class AuthRoleResourceBean {
    @TableId(type = IdType.AUTO)
    private Integer id;
    @TableField(value = "role_id")
    private Integer roleId;
    @TableField(value = "resource_id")
    private Integer resourceId;
    @TableField(value = "create_time")
    private Date createTime;
}
