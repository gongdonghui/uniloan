package com.sup.kalapa.bean.dto;

import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

/**
 * @Author: kouichi
 * @Date: 2020/2/10 12:23
 */
@Data
public class GetVSSInfoParams {
    @NotEmpty
    private String id;
    private Boolean getMaxLatestJobs;
    private Boolean getHRInfo;
    private Integer applyId;
    @NotNull
    private Integer userId;
}
