package com.sup.cms.bean.vo;

import lombok.Data;

import javax.validation.constraints.Min;
import java.util.Date;

/**
 * @Author: kouichi
 * @Date: 2019/9/22 18:50
 */
@Data
public class ApplyManagementGetListParams {
    private Date startTime;
    private Date endTime;
    private Integer stage;
    private Integer status;
    private Integer applyId;
    private String name;
    private String cidNo;
    private String mobile;
    private String appName;
    private Integer merchant;
    @Min(0)
    private Integer pageSize;
    @Min(0)
    private Integer page;
}
