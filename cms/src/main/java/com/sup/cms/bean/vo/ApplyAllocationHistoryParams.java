package com.sup.cms.bean.vo;

import lombok.Data;

import java.util.Date;

/**
 * @Author: kouichi
 * @Date: 2019/9/22 19:04
 */
@Data
public class ApplyAllocationHistoryParams {
    private Integer applyId;
    private Integer operatorId;
    private Integer distributorId;
    /**
     * 前面显示的是任务名称 传过来的时候传 枚举值
     */
    private Integer taskType;
    /**
     * 前面显示的执行结果 传过来的是传任务状态的枚举值
     */
    private Integer status;
    /**
     * 前面显示任务名称 传过来的是产品id
     */
    private Integer productId;
    private String name;
    private String cid;
    private String mobile;
    private Date createTime;
    private Date endTime;
    private Date applyCreateTime;
    private Date applyEndTime;
}
