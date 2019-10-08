package com.sup.common.param;

import lombok.Data;

/**
 * gongshuai
 * <p>
 * 2019/10/5
 */
@Data
public class CreditLevelParam {
    private Integer level;
    private Integer reloan_times;
    private Integer max_overdue_days;
}
