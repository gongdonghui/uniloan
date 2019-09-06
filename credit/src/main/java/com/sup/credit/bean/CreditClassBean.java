package com.sup.credit.bean;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * Project:uniloan
 * Class:  CreditClassBean
 * <p>
 * Author: guanfeng
 * Create: 2019-09-06
 */

@Data
@TableName("apply_info")
public class CreditClassBean {
    @TableId(type = IdType.AUTO)
    private Integer id;

}
