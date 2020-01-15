package com.sup.common.param;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;

/**
 * Project:uniloan
 * Class:  ManualRepayParam
 * <p>
 * Author: guanfeng
 * Create: 2019-09-17
 */

@Data
public class ManualRepayParam {
    private String  userId;
    private String  applyId;
    private String  operatorId;
    private Integer amount;     // 还款金额
    private String  repayTime;  // 还款时间 yyyy-MM-dd HH:mm:ss
    private String  repayImg;
    private String  comment;
}
