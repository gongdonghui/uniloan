package com.sup.cms.bean.vo;

import lombok.Data;

import java.util.Date;

/**
 * @Author: kouichi
 * @Date: 2019/9/24 19:49
 */
@Data
public class CollectionAddAllocateRecordParams {
    private String mobile;
    private String status;
    private Date alertDate;
    private String comment;
    private String applyId;
    private String operatorId;
}
