package com.sup.common.bean.paycenter;

import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * @Author: kouichi
 * @Date: 2020/5/17 16:51
 */
@Data
public class DestroyVCInfo {
    @NotBlank(message = "进件id不能为空")
    private String orderNo;
    @NotBlank(message = "银行卡号不能为空")
    private String accountNo;
}
