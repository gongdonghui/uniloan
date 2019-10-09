package com.sup.cms.bean.vo;

import lombok.Data;

import javax.validation.constraints.Min;

/**
 * @Author: kouichi
 * @Date: 2019/9/24 19:02
 */
@Data
public class CollectionAllocateGetListParams {
    /**
     * 前端复选框选完之后能确定到唯一的产品id
     */
    private Integer productId;
    private Integer applyId;
    private String name;
    private String mobile;
    private String cidNo;
    private String overdueDays;
    private String overdueLevel;
    /**
     * 业务类型对应的id
     */
    private String businessId;
    /**
     * 催收状态
     */
    private String status;

    @Min(0)
    private Integer page;
    @Min(0)
    private Integer pageSize;
}
