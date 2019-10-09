package com.sup.cms.bean.vo;

import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Date;

/**
 * @Author: kouichi
 * @Date: 2019/9/24 19:29
 */
@Data
public class CollectionMineGetListParams {
    /**
     * 操作人  必填
     * 根据这个来区分是查询谁的催收单子
     */
    @NotNull
    private Integer operatorId;
    private Integer applyId;
    private String name;
    private String mobile;
    private String cidNo;
    private String overdueLevel;
    private Date lastCollectionDateStart;
    private Date lastCollectionDateEnd;
    private Date shouldRepayDateStart;
    private Date shouldRepayDateEnd;
    /**
     * 催收状态
     */
    private String status;
    @Min(0)
    private Integer page;
    @Min(0)
    private Integer pageSize;
}
