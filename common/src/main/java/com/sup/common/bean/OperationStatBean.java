package com.sup.common.bean;

import lombok.Data;

/**
 * gongshuai
 * <p>
 * 2020/2/5
 */
@Data
public class OperationStatBean {
    private Integer allocated;//分配数
    private Integer checked;//已审核数
    private Integer passed; //通过数
    private Integer denyed;//拒绝数
    private Integer loan_num;  //放款数
    private Integer loan_amt;   //放款金额
    private Double  pass_rate ;//通过率
    private Double  loan_rate;   //放款转化率
    private Integer  pending;   //待审核数
}
