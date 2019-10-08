package com.sup.cms.bean.dto;

import lombok.Data;

import java.util.Date;

/**
 * @Author: kouichi
 * @Date: 2019/10/3 18:00
 */
@Data
public class AuthRoleList {
    private Integer id;
    private String name;
    private String comment;
    private Integer isValid;
    private Date createTime;
    private Date updateTime;
}
