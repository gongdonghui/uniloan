package com.sup.cms.bean.vo;

import lombok.Data;

import javax.validation.constraints.Min;
import java.util.Date;

/**
 * @Author: kouichi
 * @Date: 2019/10/13 19:49
 */
@Data
public class CustomerGetListParams {
    private String name;

    private String cidNo;

    private String mobile;

    private Date registStartDate;

    private Date registEndDate;

    @Min(0)
    private Integer page;

    @Min(0)
    private Integer pageSize;

}
