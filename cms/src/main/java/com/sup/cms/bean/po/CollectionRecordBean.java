package com.sup.cms.bean.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

/**
 * @Author: kouichi
 * @Date: 2019/10/2 16:07
 */
@Data
public class CollectionRecordBean {
    private String  mobile;
    private String  status;
    private Date    alertDate;
    private String  comment;
    private Integer applyId;
    private String  periods;
    private Integer operatorId;
    private String  operatorName;
    private Date    createTime;
}
