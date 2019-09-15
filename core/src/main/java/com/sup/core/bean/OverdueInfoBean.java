package com.sup.core.bean;

import lombok.Data;

/**
 * gongshuai
 * <p>
 * 2019/9/15
 */
@Data
public class OverdueInfoBean {
    private int times;
    private int max_days;
    private int latest_days;
}
