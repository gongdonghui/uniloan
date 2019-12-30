package com.sup.common.param;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.List;

@Data
public class ApplyRetryLoanParam {

    @NotNull
    private Integer applyId;

    @NotNull
    private Integer operatorId;
}
