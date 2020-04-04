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
@TableName("tb_install_click_info")
public class TbInstallClickInfoBean {
    @TableId(type = IdType.AUTO)
    private Integer id;         // apply_id
    private String install_referrer;
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private Date referrer_click_date;
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private Date install_begin_date;
    private String deviceid;
    private String mobile;
}
