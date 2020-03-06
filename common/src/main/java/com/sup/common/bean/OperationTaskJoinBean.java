package com.sup.common.bean;

import lombok.Data;

import java.util.Date;

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
    private Date    createTime;
    private Integer checkStatus;
    private Integer operatorId;
    private Integer loanAmt;
}
