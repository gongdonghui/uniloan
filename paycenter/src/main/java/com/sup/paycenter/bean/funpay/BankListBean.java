package com.sup.paycenter.bean.funpay;

import lombok.Data;

import java.util.List;

/**
 * @Author: kouichi
 * @Date: 2019/9/11 22:14
 */
@Data
public class BankListBean {
    private Integer count;
    private Integer merchantID;
    private Integer businessID;
    private Integer bankID;
    private List<BankInfoBean> bankList;
}
