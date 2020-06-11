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
@TableName("tb_sdk_dial_history")
public class TbSdkDialHistoryBean {
    @TableId(type = IdType.AUTO)
    Integer id;
    Integer user_id;
    String counterpart_number;
    String location;
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
    Date call_time;
    Integer duration;
    Integer call_type;
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
    Date create_time;
}
