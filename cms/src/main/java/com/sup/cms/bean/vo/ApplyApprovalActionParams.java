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
     * 任务id
     */
    @NotNull
    private Integer id;
    /**
     * 进件id
     */
    @NotNull
    private Integer applyId;
    /**
     * 审批人id
     */
    @NotNull
    private Integer operatorId;
    /**
     * 审核结果类型 0不通过 1通过 2取消
     */
    @NotNull
    private Integer type;
    /**
     * 0 信审
     * 1 终审
     */
    @NotNull
    private Integer type2;
    /**
     * 审批意见
     */
    @NotBlank
    private String comment;

    /**
     * 审批授予额度
     */
    private Integer grantQuota;
}
