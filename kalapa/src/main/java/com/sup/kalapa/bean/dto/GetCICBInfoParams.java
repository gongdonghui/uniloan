package com.sup.kalapa.bean.dto;

import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

/**
 * @Author: kouichi
 * @Date: 2020/2/10 14:19
 */
@Data
public class GetCICBInfoParams {
    @NotEmpty
    private String id;
    private Integer applyId;
    @NotNull
    private Integer userId;
}
