package com.sup.cms.bean.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import javax.validation.constraints.Min;
import java.util.Date;

/**
 * gongshuai
 * <p>
 * 2019/10/5
 */
@Data
public class CollectorReportParam {
    @JsonFormat(pattern="yyyy-MM-dd")
    private Date startDate;

    @JsonFormat(pattern="yyyy-MM-dd")
    private Date endDate;

    private Integer operatorId;

    @Min(0)
    private Integer page;

    @Min(0)
    private Integer pageSize;
}
