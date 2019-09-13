package com.sup.common.bean.paycenter;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * @Author: kouichi
 * @Date: 2019/9/10 21:54
 */
@Data
public class BankInfo {
    @NotBlank(message = "银行编号不能为空")
    private String bankNo;
    @NotBlank(message = "银行账户不能为空")
    private String accountNo;
    @NotNull(message = "账户类型必须为0或1")
    private Integer accountType;
    @NotBlank(message = "用户姓名不能为空")
    private String accountName;
}
