package com.sup.credit.bean;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * Project:uniloan
 * Class:  UserIdCardInfoBean
 * <p>
 * Author: guanfeng
 * Create: 2019-09-06
 */

@Data
@TableName("tb_user_citizen_identity_card_info")
public class UserIdCardInfoBean {
    @TableId(type = IdType.AUTO)
    private Integer id;

}
