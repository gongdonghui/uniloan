package com.sup.kalapa.bean.kalapa;

import lombok.Data;

import java.util.List;

/**
 * @Author: kouichi
 * @Date: 2020/2/10 11:28
 */
@Data
public class IncomeInfo {
    private String name;
    private String dob;
    private String gender;
    private String phone;
    private String address;
    private List<WorkInfo> workInfos;
}
