package com.sup.cms.bean.vo;

import javax.validation.constraints.NotBlank;
import java.util.Date;

/**
 * @Author: kouichi
 * @Date: 2019/9/24 19:29
 */
public class CollectionMineGetListParams {
    /**
     * 操作人  必填
     * 根据这个来区分是查询谁的催收单子
     */
    @NotBlank
    private String operatorId;
    private String applyId;
    private String name;
    private String mobile;
    private String cidNo;
    private String overdueLevel;
    private Date lastCollectionDateStart;
    private Date lastCollectionDateEnd;
    private Date shouldRepayDateStart;
    private Date shouldRepayDateEnd;
    /**
     * 催收状态
     */
    private String status;
}
