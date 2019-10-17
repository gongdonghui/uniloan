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
    @NotBlank(message = "进件id不能为空")
    private String applyId;
    @NotBlank(message = "姓名不能为空")
    private String name;
    @NotBlank(message = "手机不能为空")
    private String phone;

    // @NotBlank(message = "回款时orderNo不能为空")
    private String orderNo;
}
