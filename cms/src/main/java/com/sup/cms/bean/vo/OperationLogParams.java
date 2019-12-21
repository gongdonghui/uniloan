package com.sup.cms.bean.vo;

import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

/**
 * @Author: kouichi
 * @Date: 2019/9/18 16:40
 */
@Data
public class OperationLogParams {
    @NotNull
    private Integer applyId;

    private Integer operationType;  // 操作类型，0:初审，1:复审，2:终审，3:催收, 详见OperationLogTypeEnum
}
