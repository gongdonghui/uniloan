package com.sup.core.bean;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

/**
 * Project:uniloan
 * Class:  UserBasicInfoBean
 * <p>
 * Author: guanfeng
 * Create: 2019-09-06
 */

@Data
@TableName("tb_user_basic_info")
public class UserBasicInfoBean {
    @TableId(type = IdType.AUTO)
    private Integer id;
    private String info_id;
    private Integer user_id;
    private Integer education;
    private Integer marriage;
    private Integer child_count;
    private Integer residence_city;
    private String residence_addr;
    private Integer residen_duration;
    private Integer purpose;
    private String purpose_other;
    private String zolo_id;
    private Integer age;
    private Float longitude;
    private Float latitude;
    private Date create_time;
    private Date expire_time;
}
