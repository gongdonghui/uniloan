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
    private String  idCardInfoId;       // 身份证infoId
    private String  basicInfoId;        // 基本信息infoId
    private String  contactInfoId;      // 联系人infoId
    private String  employmentInfoId;   // 职业信息infoId
    private String  bankInfoId;         // 银行卡infoId
}
