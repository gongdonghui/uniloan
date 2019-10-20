package com.sup.common.bean;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

/**
 * gongshuai
 * <p>
 * 2019/10/20
 */
@Data
@TableName("tb_report_collection_daily")
public class CollectionReportBean {
    @TableId(type = IdType.AUTO)
    private Integer id;
    private Date data_dt;
    private Integer in_apply;
    private Long in_amt;
    private Long repay_amt;
    private Integer repay_apply;
    private Integer ptp_apply;
    private Integer tracked_apply;
    private Double repay_amt_rate;
    private Double repay_apply_rate;


}
