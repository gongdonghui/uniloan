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
public class CustomerGetListParams {
    private String name;

    private String cidNo;

    private String mobile;

    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss", timezone = "GMT+7")
    private Date registStartDate;
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss", timezone = "GMT+7")
    private Date registEndDate;

    @Min(0)
    private Integer page;

    @Min(0)
    private Integer pageSize;

}
