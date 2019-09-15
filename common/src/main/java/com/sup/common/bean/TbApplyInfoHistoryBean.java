package com.sup.common.bean;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

/**
 * Project:uniloan
 * Class:  TbApplyInfoHistoryBean
 * <p>
 * Author: guanfeng
 * Create: 2019-09-05
 */

@Data
@TableName("tb_apply_info_history")
public class TbApplyInfoHistoryBean {
    @TableId(type = IdType.AUTO)
    private Integer id;
    private Integer apply_id;
    private Integer user_id;
    private Integer product_id;
    private Integer channel_id;
    private Integer app_id;
    private Integer status;     // 进件状态:  0:待审核, 1:自动审核通过, 2:初审通过, 3:复审通过, 4:终审通过,
                                //           5:自动审核拒绝, 6:初审拒绝, 7:复审拒绝, 8:终审拒绝, 9:取消或异常
                                //           10:自动放款中，11:自动放款失败，12:已放款/还款中，13:未还清，14:已还清，
                                //           15:逾期，16:核销

    private Integer operator_id;
    private String  deny_code;
    private String  comment;
    private Date    create_time;
    private Date    expire_time;
    private Date    apply_time;

    public TbApplyInfoHistoryBean() {}

    public TbApplyInfoHistoryBean(TbApplyInfoBean bean) {
        this.app_id = bean.getId();
        this.user_id = bean.getUser_id();
        this.product_id = bean.getProduct_id();
        this.channel_id = bean.getChannel_id();
        this.app_id = bean.getApp_id();
        this.status = bean.getStatus();
        this.operator_id = bean.getOperator_id();
        this.deny_code = bean.getDeny_code();
        this.comment = bean.getComment();
        this.apply_time = bean.getApply_time();
        this.expire_time = bean.getExpire_time();

        this.create_time = new Date();
    }
}
