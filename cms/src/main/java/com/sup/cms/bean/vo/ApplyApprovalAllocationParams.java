package com.sup.cms.bean.vo;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * @Author: kouichi
 * @Date: 2019/9/22 18:31
 */
@Data
public class ApplyApprovalAllocationParams {
    /**
     * 信审0 终审1
     */
    @NotNull
    private Integer type;


    /**
     * 批量处理时的多个任务id
     */
    private List<Integer> ids;

    /**
     *
     */
    private Integer id;

    /**
     *
     */
    private Integer applyId;

    /**
     * 领任务或者被指派任务 填这个参数
     */
    @NotNull
    private Integer operatorId;
    /**
     * 只有指派任务才填这个参数 领任务这个参数为null
     */
    private Integer distributorId;
}
