package com.sup.core.bean;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * Project:uniloan
 * Class:  UserEmploymentInfoBean
 * <p>
 * Author: guanfeng
 * Create: 2019-09-06
 */

@Data
@TableName("tb_user_employment_info")
public class UserEmploymentInfoBean {
    @TableId(type = IdType.AUTO)
    private Integer id;

}
