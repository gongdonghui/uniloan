package com.sup.common.bean.paycenter;

import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * @Author: kouichi
 * @Date: 2019/9/14 16:08
 */
@Data
public class RepayStatusInfo {
    @NotBlank(message = "交易流水号不能为空")
    private String tradeNo;
    @NotBlank(message = "进件id不能为空")
    private String applyId;
}
