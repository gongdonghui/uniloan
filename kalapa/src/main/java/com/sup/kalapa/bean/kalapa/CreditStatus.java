package com.sup.kalapa.bean.kalapa;

import lombok.Data;

/**
 * @Author: kouichi
 * @Date: 2020/2/10 11:52
 */
@Data
public class CreditStatus {
    private String creditInfo;
    private String name;
    private String address;
    private String checkTime;
    private String brief;
    private String mobile;
    private Integer numberOfLoans;
    private Boolean hasBadDebt;
    private Boolean hasLatePayment;
}
