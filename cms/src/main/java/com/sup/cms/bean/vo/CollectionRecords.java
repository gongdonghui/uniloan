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
    private Integer applyId;        // 进件id

    private Integer applyStatus;    // 进件状态

    private Integer grantQuota;     // 合同金额

    private Integer inhandQuota;    // 到手金额

    private String  mobile;         // 电话

    private Integer status;         // 催收状态

    private Date    alertDate;      // 提醒日期

    private String  comment;        // 催收备注

    private String  periods;        // 期次

    private Integer operatorId;     // 操作人id

    private String  operatorName;   // 操作人姓名
}
