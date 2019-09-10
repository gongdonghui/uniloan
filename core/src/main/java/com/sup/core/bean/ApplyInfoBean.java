package com.sup.core.bean;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

/**
 * Project:uniloan
 * Class:  ApplyInfoBean
 * <p>
 * Author: guanfeng
 * Create: 2019-09-05
 */

@Data
@TableName("apply_info")
public class ApplyInfoBean {
    @TableId(type = IdType.AUTO)
    private Integer id;         // apply_id
    private Integer user_id;
    private Integer product_id;
    private Integer channel_id;
    private Integer app_id;
    private Integer credit_type;
    private Float   rate;
    private Integer period;
    private Float   quota;
    private Integer status;     // 进件最新状态:  0:待审核, 1:自动审核通过, 2:初审通过, 3:复审通过, 4:终审通过,
                                //              5:自动审核拒绝, 6:初审拒绝, 7:复审拒绝, 8:终审拒绝, 9:取消或异常

    private Integer operator_id;
    private Float   apply_quota;
    private Float   grant_quota;
    private Float   remain_quota;
    private String  credit_class;
    private String  deny_code;
    private String  comment;
    private Date    create_time;
    private Date    expire_time;
    private Date    apply_time;
    private Date    update_time;
}
