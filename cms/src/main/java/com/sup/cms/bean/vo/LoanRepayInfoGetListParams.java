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
public class LoanRepayInfoGetListParams {
    @JsonFormat(pattern="yyyy-MM-dd")
    private Date shouldRepayDateStart;

    @JsonFormat(pattern="yyyy-MM-dd")
    private Date shouldRepayDateEnd;

    @JsonFormat(pattern="yyyy-MM-dd")
    private Date actualRepayDateStart;

    @JsonFormat(pattern="yyyy-MM-dd")
    private Date actualRepayDateEnd;

    private Integer productId;
    private String cidNo;
    private Integer applyId;
    private String mobile;
    /**
     * 是否核销
     * 0不是 1是
     */
    private Integer status;

    /**
     * 手动还款待确认
     * 0 无手动还款
     * 1 已手动还款，待确认
     */
    private Integer repayNeedConfirm;

    @Min(0)
    private Integer page;
    @Min(0)
    private Integer pageSize;

}
