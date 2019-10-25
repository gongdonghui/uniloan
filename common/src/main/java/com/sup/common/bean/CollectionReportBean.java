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
    private Date data_dt;   //日期
    private Integer in_apply;   // 在库合同数
    private Long in_amt;  //在库金额
    private Long repay_amt;   //当日 还款金额
    private Integer repay_apply;   //当日还款合同数
    private Integer ptp_apply;   //ptp合同数
    private Integer tracked_apply;     //跟进合同数
    private Double repay_amt_rate;     //还款金额占比
    private Double repay_apply_rate;   //还款申请占比


}
