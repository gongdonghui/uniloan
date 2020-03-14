package com.sup.kalapa.bean.kalapa;

import lombok.Data;

/**
 * @Author: kouichi
 * @Date: 2020/2/10 11:34
 */
@Data
public class WorkInfo {
    private String position;
    private String startDate;
    private String endDate;
    private String companyName;
    private String companyAddress;
    private String companyRegistration;
    private String companySizeCode;
    private Double salaryPercentile;
    private HRInfo hrinfo;
}
