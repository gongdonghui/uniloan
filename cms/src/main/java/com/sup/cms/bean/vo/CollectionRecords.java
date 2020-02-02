package com.sup.cms.bean.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;

/**
 * @Author: kouichi
 * @Date: 2019/10/2 16:16
 */
@Data
public class CollectionRecords {
    /**
     * 催收电话
     */
    private String mobile;
    /**
     * 催收状态
     */
    private String status;
    /**
     * 提醒日期
     */
    private Date alertDate;
    /**
     * 催收备注
     */
    private String comment;
    /**
     * 进件id
     */
    private Integer applyId;
    /**
     * 期次
     */
    private String periods;
    /**
     * 操作人
     */
    private Integer operatorId;
    /**
     * 添加时间
     */
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss", timezone = "GMT+7")
    private Date createTime;
}
