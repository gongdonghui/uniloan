package com.sup.common.bean;

import lombok.Data;

/**
 * gongshuai
 * <p>
 * 2020/2/5
 */
@Data
public class OperationStatBean {
    private Integer allocated;
    private Integer checked;
    private Integer passed;
    private Integer denyed;
    private Integer loan_num;
    private Integer loan_amt;
    private Double  pass_rate ;
    private Double  loan_rate;
    private Integer  pending;
}
