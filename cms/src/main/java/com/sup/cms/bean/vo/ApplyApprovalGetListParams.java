package com.sup.cms.bean.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.util.Date;

/**
 * @Author: kouichi
 * @Date: 2019/9/22 11:12
 */
@Data
public class ApplyApprovalGetListParams {
    private Integer applyId;
    private Integer operatorId;     // 操作人ID
    private String name;
    private String creditLevel;
    private String cidNo;
    private String mobile;
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss", timezone = "GMT+7")
    private Date startTime;
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss", timezone = "GMT+7")
    private Date endTime;
    /**
     * 0 未指派未领取的
     * 1 待审批的
     * 2 可重新指派的
     */
    @NotNull
    private Integer type1;
    /**
     * 0 信审
     * 1 终审
     */
    @NotNull
    private Integer type2;
    @Min(0)
    private Integer pageSize;
    @Min(0)
    private Integer page;
}
