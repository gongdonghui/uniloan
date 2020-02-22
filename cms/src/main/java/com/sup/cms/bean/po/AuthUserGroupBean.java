package com.sup.cms.bean.po;

import com.baomidou.mybatisplus.annotation.IdType;
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
@TableName("tb_cms_auth_user_group")
public class AuthUserGroupBean {

    @TableId(type = IdType.AUTO)
    private Integer id;
    private String name;
    private String type;
    private Date  create_time;

}
