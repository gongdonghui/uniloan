package com.sup.kalapa.bean.dto;

import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @Author: kouichi
 * @Date: 2020/2/10 14:29
 */
@Data
public class GetFBInfoParams {
    private String fbid;
    private String mobile;
    private Integer applyId;
    @NotNull
    private Integer userId;
}
