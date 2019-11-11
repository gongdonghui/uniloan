package com.sup.core.bean;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

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
    private String info_id;
    private Integer user_id;
    private String name;
    private String cid_no;
    private Integer gender;
    private String pic_1;
    private String pic_2;
    private String pic_3;
    private String pic_4;
    Date create_time;
    Date expire_time;

}
