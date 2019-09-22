package com.sup.cms.bean.vo;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * @Author: kouichi
 * @Date: 2019/9/22 18:25
 */
@Data
public class ApplyApprovalActionParams {
    /**
     * 审核结果类型
     */
    @NotNull
    private Integer type;
    /**
     * 审批意见
     */
    @NotBlank
    private String comment;
}
