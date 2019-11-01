package com.sup.cms.bean.po;

import lombok.Data;

import java.util.List;

/**
 * @Author: kouichi
 * @Date: 2019/10/13 21:25
 */
@Data
public class DetailsRepayBean {
    private Integer applyId;
    private String productName;
    private String name;
    private String mobile;
    private Integer loanAmount;
    private Integer actAmount;
    private Integer otherAmount;
    /**
     * 期限  1日/期, 共1期  后面的共1期是固定的 我回传 x日
     */
    private String period;
    // 总期数
    private Integer terms;
    private Integer alreadyRepay;

    /**
     * 页面里下面的列表
     */
    private List<DetailsRepayListBean> list;
}
