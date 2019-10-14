package com.sup.cms.bean.po;

import lombok.Data;

import java.util.Date;

/**
 * @Author: kouichi
 * @Date: 2019/10/13 21:48
 */
@Data
public class DetailsRepayListBean {
    private Integer seqNo;
    private Integer shouldRepayAmount;
    private Integer remainShouldRepayAmount;
    private Integer remainPrincipal;
    private Integer remainInterest;
    private Integer actRepayAmount;
    private Date shouldRepayDate;
    private Date actRepayDate;
    private Integer remainPenaltyInterestAmount;
    private Integer remainBreachFeeAmount;
    private Integer status;
}
