package com.sup.common.param;

import lombok.Data;

import java.util.Date;

/**
 * gongshuai
 * <p>
 * 2019/10/14
 */
@Data
public class TaskLabelParam {
    private String label;
    private String description;
    private String scene;
    private String creator;
    private Date create_time;
}
