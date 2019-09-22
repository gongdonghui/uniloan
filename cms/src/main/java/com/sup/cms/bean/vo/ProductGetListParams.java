package com.sup.cms.bean.vo;

import lombok.Data;

import javax.validation.constraints.Min;

/**
 * @Author: kouichi
 * @Date: 2019/9/18 16:40
 */
@Data
public class ProductGetListParams {
    private String status;
    private String name;
    @Min(0)
    private Integer pageSize;
    @Min(0)
    private Integer page;
}
