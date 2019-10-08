package com.sup.cms.bean.po;

import com.baomidou.mybatisplus.annotation.IdType;
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
    private String userName;
    private String password;
    private String name;
    private String mobile;
    private String email;
    private String comment;
    private Integer isValid;
    private Date createTime;
    private Date updateTime;
}
