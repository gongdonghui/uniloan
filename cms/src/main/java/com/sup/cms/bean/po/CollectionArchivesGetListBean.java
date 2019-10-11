package com.sup.cms.bean.po;

import lombok.Data;

import java.util.Date;

/**
 * @Author: kouichi
 * @Date: 2019/10/6 17:40
 */
@Data
public class CollectionArchivesGetListBean {
    /**
     * 任务id
     */
    private Integer id;
    private Integer applyId;
    private String mobile;
    private Date lastAllocateDate;
    /**
     * 只有2的时候代表已还清 其余都是逾期中
     */
    private Integer periodStatus;
    /**
     * 催收状态
     */
    private String status;
    /**
     * 为1的时候代表部分还清
     */
    private Integer partialRepay;
    private String appName;
    private String productName;
    /**
     * 每期的天数
     */
    private String period;
    /**
     * 当前期数
     */
    private String currentTerm;
    /**
     * 总期数
     */
    private String totalTerms;
    private String name;
    private Date shouldRepayDate;
    private Integer overdueDays;
    private Integer shouldRepayAmount;
    private String collector;
    private Date lastCollectDate;
}
