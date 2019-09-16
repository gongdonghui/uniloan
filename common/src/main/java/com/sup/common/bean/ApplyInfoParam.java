package com.sup.common.bean;

import com.sup.common.loan.ApplyMaterialTypeEnum;
import lombok.Data;

import java.util.Map;

/**
 * Project:uniloan
 * Class:  ApplyInfoParam
 * <p>
 * Author: guanfeng
 * Create: 2019-09-16
 */

@Data
public class ApplyInfoParam {

    private Integer user_id;
    private Integer product_id;
    private Integer channel_id;
    private Integer app_id;
    private Integer apply_quota;
    private Integer period;
    private Map<Integer, String> infoIdMap; // info id type参见ApplyMaterialTypeEnum
}
