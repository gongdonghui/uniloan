package com.sup.core.bean;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

/**
 * Project:uniloan
 * Class:  UserBankInfoBean
 * <p>
 * Author: guanfeng
 * Create: 2019-09-06
 */

@Data
@TableName("tb_user_bank_account_info")
public class UserBankInfoBean {
    @TableId(type = IdType.AUTO)
    private Integer id;
    private String  info_id;
    private Integer user_id;
    private Integer account_type;
    private String  name;
    private Integer bank;       // bank id for banks
    private String  account_id;
    private Date    create_time;
    private Date    expire_time;

}
