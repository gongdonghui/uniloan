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

    @NotBlank(message = "用户身份证号不能为空")
    private String id;
    @NotBlank(message = "用户姓名不能为空")
    private String userName;

}
