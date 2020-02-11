package com.sup.common.param;

import lombok.Data;

import javax.validation.constraints.Min;

/**
 * gongshuai
 * <p>
 * 2019/10/14
 */
@Data
public class GetLabelParam {



    @Min(0)
    private Integer page;

    @Min(0)
    private Integer pageSize;


}
