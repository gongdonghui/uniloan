package com.sup.cms.bean.vo;

import lombok.Data;

import java.util.Date;

/**
 * @Author: kouichi
 * @Date: 2019/9/24 19:42
 */
@Data
public class CollectionArchivesGetListParams {
    private String applyId;
    private String name;
    private String mobile;
    private String cidNo;
    private String appName;
    private Date shouldRepayDateStart;
    private Date shouldRepayDateEnd;
    private String overdueLevel;
    /**
     * 期次状态
     */
    private String qiciStatus;
    private String operatorId;
    private String operatorStatus;
    /**
     * 催收状态
     */
    private String status;
}
