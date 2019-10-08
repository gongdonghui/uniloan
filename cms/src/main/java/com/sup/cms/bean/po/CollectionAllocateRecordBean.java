package com.sup.cms.bean.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

/**
 * @Author: kouichi
 * @Date: 2019/10/2 16:35
 */
@Data
@TableName("tb_cms_collection_allocate_record")
public class CollectionAllocateRecordBean {
    @TableId(type = IdType.AUTO)
    private Integer id;
    private Date actionTime;
    private Integer applyId;
    private String distributorName;
    private String collectorName;
    private Integer distributorId;
    private Integer collectorId;
    private Date createTime;
}
