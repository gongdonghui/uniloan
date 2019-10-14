package com.sup.cms.bean.vo;

import lombok.Data;
import lombok.NonNull;

import java.util.Date;

/**
 * Project:uniloan
 * Class:  RepayMaterialParams
 * <p>
 * Author: guanfeng
 * Create: 2019-10-12
 */

@Data
public class RepayMaterialParams {

    @NonNull
    private String applyId;

    @NonNull
    private String userId;
}
