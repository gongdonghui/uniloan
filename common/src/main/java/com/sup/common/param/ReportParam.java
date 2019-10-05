package com.sup.common.param;

import lombok.Data;

import java.util.Date;

/**
 * gongshuai
 * <p>
 * 2019/10/5
 */
@Data
public class ReportParam {
    private Date start_date;
    private Date end_date;
    private  Integer   type;   //0  渠道日报   2   运营日报   3     审批日报，  4  催收日报
}
