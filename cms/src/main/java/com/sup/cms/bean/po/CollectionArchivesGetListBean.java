package com.sup.cms.bean.po;

import lombok.Data;

import java.util.Date;

/**
 * @Author: kouichi
 * @Date: 2019/10/6 17:40
 */
@Data
public class CollectionArchivesGetListBean {
    private Integer applyId;
    private String mobile;
    private Date lastAllocateDate;
    private String periodStatus;
    /**
     * 催收状态
     */
    private String status;
    private Integer partialRepay;
    private String appName;
    private String productName;
    /**
     * 期次  示例 7日/期, 共1期
     */
    private String period;
    private String name;
    private Date shouldRepayDate;
    private Integer overdueDays;
    private String overdueLevel;
    private Integer shouldRepayAmount;
    private String collector;
    private Date alarmDate;
    private Date lastCollectDate;
}
