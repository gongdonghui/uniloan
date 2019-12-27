package com.sup.common.bean;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;

/**
 * Project:uniloan
 * Class:  TbApplyInfoBean
 * <p>
 * Author: guanfeng
 * Create: 2019-09-05
 */

@Data
@TableName("tb_channel_info")
public class TbChannelInfoBean {
    @TableId(type = IdType.AUTO)
    private Integer id;
    private Integer channelId;  // 自定义渠道id
    private String  name;   // 渠道名称
    private String  type;   // 渠道类型
    private Integer status; // 渠道状态, 0:offline 1:online

    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private Date    create_time;
}
