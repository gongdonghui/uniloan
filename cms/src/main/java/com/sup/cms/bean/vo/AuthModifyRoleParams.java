package com.sup.cms.bean.vo;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * @Author: kouichi
 * @Date: 2019/10/3 18:09
 */
@Data
public class AuthModifyRoleParams {
    @NotNull
    private Integer roleId;

    @NotNull
    private String  name;  // role name
    @NotBlank
    private String comment;
    /**
     * 资源列表
     */
    @NotNull
    private List<Integer> resourceList;
}
