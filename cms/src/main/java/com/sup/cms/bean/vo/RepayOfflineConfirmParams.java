package com.sup.cms.bean.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.Date;

/**
 * @Author: kouichi
 * @Date: 2019/10/13 17:46
 */
@Data
public class RepayOfflineConfirmParams {
    @NotNull
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss", timezone = "GMT+7")
    private Date    repayDate;      // 还款时间
    @NotNull
    private Integer repayAmount;    // 还款金额

    @NotNull
    private Integer applyId;        //
    @NotNull
    private Integer userId;         //
    @NotNull
    private Integer operatorId;     //

    private String  repayImg;       // 还款凭证截图文件key（图片在SSDB中的key)
    private String  comment;
}
