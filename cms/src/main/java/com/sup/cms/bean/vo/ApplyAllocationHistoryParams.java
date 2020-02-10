package com.sup.cms.bean.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import javax.validation.constraints.Min;
import java.util.Date;

/**
 * @Author: kouichi
 * @Date: 2019/9/22 19:04
 */
@Data
public class ApplyAllocationHistoryParams {
    private Integer applyId;
    private Integer operatorId;
    private Integer distributorId;
    /**
     * 前面显示的是任务名称 传过来的时候传 枚举值
     */
    private Integer taskType;
    /**
     * 前面显示的执行结果 传过来的是传任务状态的枚举值
     */
    private Integer status;
    /**
     * 前面显示任务名称 传过来的是产品id
     */
    private Integer productId;
    private String name;
    private String cidNo;
    private String mobile;
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss", timezone = "GMT+7")
    private Date createTime;
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss", timezone = "GMT+7")
    private Date endTime;
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss", timezone = "GMT+7")
    private Date applyCreateTime;
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss", timezone = "GMT+7")
    private Date applyEndTime;
    @Min(0)
    private Integer pageSize;
    @Min(0)
    private Integer page;
}
