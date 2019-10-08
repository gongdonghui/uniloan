package com.sup.cms.bean.po;

import lombok.Data;

import java.util.Date;

/**
 * @Author: kouichi
 * @Date: 2019/10/7 18:01
 */
@Data
public class DetailsToBeRepayList {
    //    贷款期次
    private Integer seqNo;
    //    应收时间
    private Date startDate;
    //            应收总额
    private Integer needTotal;
    //    剩余应换本金
    private Integer remainPrincipal;
    //            剩余应还利息
    private Integer remainInterest;
    //    剩余罚息
    private Integer remainPenaltyInterest;
    //    其他费用
    private Integer other;
}
