package com.sup.cms.bean.po;

import lombok.Data;

import java.util.Date;

/**
 * @Author: kouichi
 * @Date: 2019/10/6 17:31
 */
@Data
public class CollectionAllocateGetListBean {
    /**
     * 任务id
     */
    private Integer id;

    private Integer applyId;
    private Date lastAllocateDate;
    private String name;
    private String mobile;
    /**
     * 催收状态
     */

    private String status;
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
    /**
     * 业务类型
     */
    // private String type;
    // private String overdueLevel;
    private Integer overdueDays;
    private Integer shouldRepayAmount;
    private Date shouldRepayDate;
    /**
     * 放款时间
     */
    private Date payDate;

    /**
     * 最后催收时间
     */
    private Date update;

}
