package com.sup.common.bean;

import lombok.Data;

/**
 * gongshuai
 * <p>
 * 2020/1/30
 */
@Data
public class OverallMultiReportBean {

    private Integer loan_amt;// 放款金额
    private Integer apply;   //进件申请数
    private Integer auto_pass;   //机审通过数
    private Integer manual_pass;   //人工审核通过数
    private Integer loan_num;    //放款成功数
    private Double loan_ctr;    //放款转化率

    private Integer repay_amt;       //应还总额
    private Integer repay_actual_amt;   //实还总额
    private Double repay_rate;    //还款比例
    private Integer repay_num;    //应还笔数
    private Integer repay_actual_num;    //实还笔数

    private Integer collection_num;   //催回笔数
    private Integer collection_amt;   //催回金额

    private Integer fr_num;   //首逾笔数
    private Integer fr_amt;   //首逾金额
    private Double fr_rate;  //首逾比例
    //时点口径
    private Integer delinquency_amt;   //逾期金额
    private Integer delinquency_num;    //逾期笔数
    private Double delinquency_rate;   //逾期比例

    private Integer in_loan_num;   //未到期笔数
    private Integer in_loan_amt;   //未到期金额

}
