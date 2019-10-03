package com.sup.cms.bean.vo;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * @Author: kouichi
 * @Date: 2019/10/3 17:24
 */
@Data
public class AuthModifyUserParams {
    @NotNull
    private Integer userId;
    private String name;
    private String mobile;
    private String email;
    private String comment;
}
