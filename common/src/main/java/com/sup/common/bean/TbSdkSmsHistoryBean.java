package com.sup.common.bean;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.util.Date;

/**
 * Created by xidongzhou1 on 2019/9/5.
 */
@Data
@NoArgsConstructor
@Accessors(chain = true)
@TableName("tb_sdk_sms_history")
public class TbSdkSmsHistoryBean {
    @TableId(type = IdType.AUTO)
    Integer id;
    Integer user_id;
    String name;
    String address;
    String body;
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
    Date sms_time;
    Integer readed;
    Integer type;
    Integer status;
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
    Date create_time;
}
