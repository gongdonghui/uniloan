package com.sup.cms.bean.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import javax.validation.constraints.Min;
import java.util.Date;

/**
 * @Author: kouichi
 * @Date: 2019/10/13 19:49
 */
@Data
public class OverdueRecallListParams {
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss", timezone = "GMT+7")
    private Date    allocStartDate; // 任务分配开始时间
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss", timezone = "GMT+7")
    private Date    allocEndDate;   // 任务分配结束时间
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss", timezone = "GMT+7")
    private Date    repayStartDate; // 还款开始时间
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss", timezone = "GMT+7")
    private Date    repayEndDate;   // 还款结束时间

    private Integer operatorId;     // 催收员id

    private Date    loanStartDate; // 放款开始时间
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss", timezone = "GMT+7")
    private Date    loanEndDate;   // 放款结束时间

    @Min(0)
    private Integer page;
    @Min(0)
    private Integer pageSize;
}
