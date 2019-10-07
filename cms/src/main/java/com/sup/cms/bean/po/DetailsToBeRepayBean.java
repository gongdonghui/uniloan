package com.sup.cms.bean.po;

import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * 待还信息
 *
 * @Author: kouichi
 * @Date: 2019/10/7 17:57
 */
@Data
public class DetailsToBeRepayBean {
    /**
     * 还款方式
     */
    private Integer feeType;
    /**
     * 贷款利率
     */
    private Double rate;
    /**
     * 总到期日
     */
    private Date endDate;
    /**
     * 列表
     */
    private List<DetailsToBeRepayList> list;
}
