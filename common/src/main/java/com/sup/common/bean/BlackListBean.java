package com.sup.common.bean;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

/**
 * gongshuai
 * <p>
 * 2019/11/3
 */
@Data
@TableName("tb_blacklist")
public class BlackListBean {
    @TableId(type = IdType.AUTO)
    private Integer id;
    private String  cid_no;
    private String  name;
    private String  mobile;
    private Integer apply_id;
    private Integer status;     // 0:正常，1:灰名单, 2:黑名单
    private String  reason;     // 非正常原因
    private String  platform;   // 第三方平台标识
    private String  origin_message;  // 三方服务返回的原始信息
//    private String transactionId;
//    private String hitResult;
//    private String message;
//    private String pricingStrategy;
    private Date expire_time;
    private Date create_time;
}
