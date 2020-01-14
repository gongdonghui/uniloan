package com.sup.cms.bean.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import javax.validation.constraints.Min;
import java.util.Date;

/**
 * @Author: kouichi
 * @Date: 2019/10/13 17:46
 */
@Data
public class RepayOfflineConfirmParams {

    @JsonFormat(pattern="yyyy-MM-dd")
    private Date    repayDate;      // 还款时间
    private Integer repayAmount;    // 还款金额

    private Integer applyId;        //
    private Integer userId;         //
    private Integer operatorId;     //

    // 流水号、凭证截图不可都为空
    private String  orderNo;        // 交易流水号
    private String  repayImg;       // 还款凭证截图文件key（图片在SSDB中的key)
}
