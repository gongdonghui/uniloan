package com.sup.paycenter.bean.funpay;

import lombok.Data;

import java.util.List;

/**
 * @Author: kouichi
 * @Date: 2019/9/11 22:14
 */
@Data
public class BankListBean {
    private int count;
    private String merchantID;
    private String businessID;
    private String bankID;
    private List<BankInfoBean> bankList;
}
