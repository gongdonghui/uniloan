package com.sup.cms.bean.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import javax.validation.constraints.Min;
import java.util.Date;

/**
 * @Author: kouichi
 * @Date: 2019/9/22 18:50
 */
@Data
public class ApplyManagementGetListParams {
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss", timezone = "GMT+7")
    private Date startTime;
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss", timezone = "GMT+7")
    private Date endTime;
    private Integer stage;
    private Integer status;
    private Integer applyId;
    private Integer appId;
    private String name;
    private String cidNo;
    private String mobile;
    private String appName;
    private Integer channelId;

    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss", timezone = "GMT+7")
    private Date loan_startTime;
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss", timezone = "GMT+7")
    private Date loan_endTime;
    @Min(0)
    private Integer pageSize;
    @Min(0)
    private Integer page;
}
