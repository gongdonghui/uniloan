package com.sup.core.bean;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

/**
 * Project:uniloan
 * Class:  ApplyMaterialInfoBean
 * <p>
 * Author: guanfeng
 * Create: 2019-09-05
 */

@Data
@TableName("apply_material_info")
public class ApplyMaterialInfoBean {
    @TableId(type = IdType.AUTO)
    private Integer id;
    private Integer apply_id;
    private String  info_id;
    private Integer info_type;      // 0|身份证信息  1|基本信息 2|紧急联系人 3|职业信息 4|银行卡信息
    private Date    create_time;
}
