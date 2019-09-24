package com.sup.cms.bean.vo;

import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @Author: kouichi
 * @Date: 2019/9/24 19:47
 */
@Data
public class CollectionArchivesReAllocateParams {
    /**
     * 任务id
     */
    @NotNull
    private Integer id;
    /**
     * 被派人
     */
    @NotNull
    private Integer operatorId;
    /**
     * 谁派的
     */
    private Integer distributorId;
}
