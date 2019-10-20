package com.sup.core.bean;

import lombok.Data;

/**
 * gongshuai
 * <p>
 * 2019/10/14
 */
@Data
public class OperationTaskJoinBean {
    private Integer applyId;
    private Integer applyStatus;
    private Integer id;
    private Integer taskStatus;
    private Integer hasOwner;
    private Integer createTime;
    private Integer updateTime;
}
