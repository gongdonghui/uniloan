package com.sup.common.param;

import lombok.Data;

import java.util.Date;

/**
 * gongshuai
 * <p>
 * 2019/10/5
 */
@Data
public class OperationReportParam {
    private Date start_date;
    private Date end_date;
    private Integer channel_id;
    private Integer type;   //0  渠道日报   2   运营日报   3     审批日报，  4  催收日报
}
