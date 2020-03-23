package com.sup.kalapa.bean.dto;

import com.sup.kalapa.bean.kalapa.CreditScore;
import com.sup.kalapa.bean.kalapa.CreditStatus;
import lombok.Data;

/**
 * @Author: kouichi
 * @Date: 2020/2/10 14:24
 */
@Data
public class CICBResult {
    private CreditScore creditScore;
    private CreditStatus creditStatus;
}
