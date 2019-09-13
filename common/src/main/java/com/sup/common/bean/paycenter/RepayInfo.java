package com.sup.common.bean.paycenter;

import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;

/**
 * @Author: kouichi
 * @Date: 2019/9/10 22:19
 */
@Data
public class RepayInfo {
    @NotBlank(message = "用户ID不能为空")
    private String userId;
    @Min(value = 0, message = "打款金额不能小于0")
    private Integer amount;
    @NotBlank(message = "订单号不能为空")
    private String orderId;
    @NotBlank(message = "过期时间不能为空")
    private String expireDate;
    @NotBlank(message = "姓名不能为空")
    private String name;
    @NotBlank(message = "手机不能为空")
    private String phone;
}
