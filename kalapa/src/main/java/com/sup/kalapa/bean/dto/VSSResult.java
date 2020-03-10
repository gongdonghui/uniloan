package com.sup.kalapa.bean.dto;

import com.sup.kalapa.bean.kalapa.FamilyInfo;
import com.sup.kalapa.bean.kalapa.IncomeInfo;
import lombok.Data;

/**
 * @Author: kouichi
 * @Date: 2020/2/10 12:59
 */
@Data
public class VSSResult {
    private IncomeInfo incomeInfo;
    private FamilyInfo familyInfo;
}
