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
@TableName("tb_channel_product")
public class TbChannelProductBean {
    @TableId(type = IdType.AUTO)
    private Integer id;
    private Integer channel_id;
    private Integer app_id;
    private Integer product_id;

    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private Date    create_time;
}
