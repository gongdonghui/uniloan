package com.sup.core.bean;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * gongshuai
 * <p>
 * 2019/11/3
 */
@Data
@TableName("tb_core_jr_response")
public class JirongBLResponse {
    @TableId(type = IdType.AUTO)
    private Integer id;
    private String apply_id;
    private String status;
    private String transactionId;
    private String hitResult;
    private String message;
    private String pricingStrategy;
}
