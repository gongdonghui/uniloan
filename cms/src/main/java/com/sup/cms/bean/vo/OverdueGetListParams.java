package com.sup.cms.bean.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import javax.validation.constraints.Min;
import java.util.Date;

/**
 * @Author: kouichi
 * @Date: 2019/10/13 19:49
 */
@Data
public class OverdueGetListParams {

    private Integer applyId;    // 订单id
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss", timezone = "GMT+7")
    private Date    startDate;  // 到期时间
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss", timezone = "GMT+7")
    private Date    endDate;    // 到期时间
    private String  name;       // 姓名
    private String  mobile;     // 手机号
    private String  cidNo;      // 身份证
    private Integer operatorId; // 催收员id


    /**
     * 逾期天数
     */
    private Integer overdueDays;
    private Integer productId;

    @Min(0)
    private Integer page;
    @Min(0)
    private Integer pageSize;
}
