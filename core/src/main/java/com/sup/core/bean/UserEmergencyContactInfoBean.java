package com.sup.core.bean;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

/**
 * Project:uniloan
 * Class:  UserEmergencyContactInfoBean
 * <p>
 * Author: guanfeng
 * Create: 2019-09-06
 */

@Data
@TableName("tb_user_emergency_contact")
public class UserEmergencyContactInfoBean {
    @TableId(type = IdType.AUTO)
    private Integer id;
    private String info_id;
    private Integer user_id;
    private Integer relationship;
    private String name;
    private String mobile;
    private Date create_time;
    private Date expire_time;

}
