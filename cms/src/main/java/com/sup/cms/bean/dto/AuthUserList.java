package com.sup.cms.bean.dto;

import lombok.Data;

/**
 * @Author: kouichi
 * @Date: 2019/10/3 17:42
 */
@Data
public class AuthUserList {
    private Integer id;
    private String userName;
    private String name;
    private String mobile;
    private String email;
    private String comment;
    private Integer isValid;
}
