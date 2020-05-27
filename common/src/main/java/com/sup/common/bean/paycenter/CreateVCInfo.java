package com.sup.common.bean.paycenter;

import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;

/**
 * @Author: kouichi
 * @Date: 2020/5/17 16:23
 */
@Data
public class CreateVCInfo {
    @NotBlank(message = "用户ID不能为空")
    private String userId;
    @NotBlank(message = "进件id不能为空")
    private String orderNo;

    @Min(value = 0, message = "订单金额不能小于0")
    private Integer amount;

    /**
     * 用于生成虚拟银行卡卡号(要九位以下的 去除开头0和+84一般为九位)
     */
    @NotBlank(message = "手机号不能为空")
    private String mobile;
    @NotBlank(message = "用户姓名不能为空")
    private String userName;

}
