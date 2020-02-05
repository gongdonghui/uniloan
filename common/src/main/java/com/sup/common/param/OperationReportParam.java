package com.sup.common.param;

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
public class OperationReportParam {
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss", timezone = "GMT+7")
    private Date start_date;

    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss", timezone = "GMT+7")
    private Date end_date;

    private Integer channel_id;
    private Integer product_id;
    private Integer type;   //0  渠道日报   2   运营日报   3     审批日报，  4  催收日报

    @Min(0)
    private Integer page;

    @Min(0)
    private Integer pageSize;
}
