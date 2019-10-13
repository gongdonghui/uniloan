package com.sup.cms.bean.vo;

import lombok.Data;

import javax.validation.constraints.Min;
import java.util.Date;

/**
 * @Author: kouichi
 * @Date: 2019/10/13 17:46
 */
@Data
public class LoanUnRepayInfoGetListParams {
    private Date shouldRepayDateStart;
    private Date shouldRepayDateEnd;
    private Integer productId;
    private String cidNo;
    private Integer applyId;
    private String mobile;
    @Min(0)
    private Integer page;
    @Min(0)
    private Integer pageSize;
}
