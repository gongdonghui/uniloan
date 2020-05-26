package com.sup.common.bean.paycenter;

import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;

/**
 * @Author: kouichi
 * @Date: 2020/5/24 17:53
 */
@Data
public class UpdateVCInfo {
    @NotBlank(message = "用户ID不能为空")
    private String userId;
    @NotBlank(message = "进件id不能为空")
    private String orderNo;

    @Min(value = 0, message = "订单金额不能小于0")
    private Integer amount;

    @NotBlank(message = "银行卡号不可为空")
    private String accountNo;
}
