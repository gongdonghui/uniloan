package com.sup.cms.bean.vo;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * @Author: kouichi
 * @Date: 2019/10/3 17:21
 */
@Data
public class AuthAddUserParams {
    @NotBlank
    private String userName;
    @NotBlank
    private String password;
    @NotBlank
    private String name;
    @NotBlank
    private String mobile;
    @NotBlank
    private String email;
    private String comment;
    /**
     * 角色列表  选好了角色之后 会得到对应的id 把这个id列表传过来 比如 [1,2,3]
     */
    @NotNull
    private List<Integer> roleList;
}
