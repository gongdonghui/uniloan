package com.sup.cms.bean.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
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
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss", timezone = "GMT+7")
    private Date alertDate;
    private String comment;
    private String applyId;
    private String operatorId;
}
