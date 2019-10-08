package com.sup.cms.bean.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

/**
 * @Author: kouichi
 * @Date: 2019/10/2 16:07
 */
@Data
@TableName("tb_cms_collection_record")
public class CollectionRecordBean {
    @TableId(type = IdType.AUTO)
    private Integer id;
    private String mobile;
    private String status;
    private Date alertDate;
    private String comment;
    private Integer applyId;
    private String periods;
    private Integer operatorId;
    private Date createTime;
}
