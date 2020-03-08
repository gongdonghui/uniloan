package com.sup.common.bean;

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
    private String name;   // 组名
    private String type;    //0 表示催收 1 表示信审
    private Date  create_time;

}
