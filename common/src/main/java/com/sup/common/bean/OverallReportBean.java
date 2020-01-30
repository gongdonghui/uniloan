package com.sup.common.bean;

import lombok.Data;

/**
 * gongshuai
 * <p>
 * 2020/1/29
 */
@Data
public class OverallReportBean {
    private String data_dt;    //数据日期
    private Integer register;    // 注册数
    private Integer apply_num;    //进件申请数
    private Integer auto_pass;     //  自动审核通过数
    private Integer first_pass;   //初审通过数
    private Integer final_pass;   //终审通过数
    private Integer loan_num;   //放款数
    private Integer loan_failed;//放款失败
    private Integer loan_pending;   //放款中　
    private Integer first_check;    //初审已审
    private Integer first_pending;   //初审待审
    private Integer final_check;    //终审已审
    private Integer final_pending;   //终审待审
    private Integer first_total;    //初审总单数
    private Integer final_total;   //终审总单数
    private Double cps_apply;    //进件成本单价
    private Double cps_autopass;   //机审通过成本单价
    private Double cps_finalpass;   //终审通过成本单价
    private Double cps_loan;    //放款成功成本单价


}
