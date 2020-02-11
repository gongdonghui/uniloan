package com.sup.common.param;

import lombok.Data;

/**
 * gongshuai
 * <p>
 * 2019/10/14
 */
@Data
public class TaskLabelParam {
    private String label_name;   //标签
    private String content;   //标签内容
    private String scene;    //标签场景
    private Integer creator;    //创建者ID
}
