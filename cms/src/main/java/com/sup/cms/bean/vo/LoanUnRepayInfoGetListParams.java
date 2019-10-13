package com.sup.cms.bean.vo;

import lombok.Data;

import javax.validation.constraints.Min;

/**
 * @Author: kouichi
 * @Date: 2019/10/13 17:46
 */
@Data
public class LoanUnRepayInfoGetListParams {
    @Min(0)
    private Integer page;
    @Min(0)
    private Integer pageSize;
}
