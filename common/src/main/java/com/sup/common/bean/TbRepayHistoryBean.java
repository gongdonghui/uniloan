package com.sup.common.bean;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;

/**
 * Project:uniloan
 * Class:  TbRepayHistoryBean
 * <p>
 * Author: guanfeng
 * Create: 2019-09-06
 */

@Data
@TableName("tb_repay_history")
public class TbRepayHistoryBean {
    @TableId(type = IdType.AUTO)
    private Integer id;
    private Integer user_id;
    private Integer apply_id;
    private Integer repay_plan_id;

    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private Date repay_time;

    private Long repay_amount;
    private Integer operator_id = 0;
    private Integer repay_status;   // 还款状态 0:处理中 1:还款成功 2:还款失败 3:费用减免

    private String  repay_code;     // 自动还款交易码
    private String  repay_location; // 自动还款地址
    private String  trade_number;   // 自动还款流水号
    private String  comment;

    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private Date expire_time;
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private Date create_time;
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private Date update_time;
}
