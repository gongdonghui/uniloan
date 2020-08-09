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
public class LoanUnRepayInfoGetListParams {
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss", timezone = "GMT+7")
    private Date shouldRepayDateStart;
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss", timezone = "GMT+7")
    private Date shouldRepayDateEnd;

    private Integer productId;
    private String cidNo;
    private Integer applyId;
    private String mobile;

    private  String name;
    @Min(0)
    private Integer page;
    @Min(0)
    private Integer pageSize;
}
