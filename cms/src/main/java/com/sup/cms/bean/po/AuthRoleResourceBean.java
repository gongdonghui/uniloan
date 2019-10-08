package com.sup.cms.bean.po;

import com.baomidou.mybatisplus.annotation.IdType;
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
    private Integer roleId;
    private Integer resourceId;
    private Date createTime;
}
