package com.sup.paycenter.bean;

import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * @Author: kouichi
 * @Date: 2019/9/10 22:12
 */
@Data
public class PayInfo {
    @NotBlank(message = "用户ID不能为空")
    private String userId;
    @NotBlank(message = "订单号不能为空")
    private String orderId;
    @Min(value = 0, message = "打款金额不能小于0")
    private Integer amount;
    @NotBlank(message = "附言不能为空")
    private String remark="   ";
    private String transferTime = "";

    @NotBlank(message = "银行编号不能为空")
    private String bankNo;
    @NotBlank(message = "银行账户不能为空")
    private String accountNo;
    @NotNull(message = "账户类型必须为0或1")
    private Integer accountType;
    @NotBlank(message = "用户姓名不能为空")
    private String accountName;

}