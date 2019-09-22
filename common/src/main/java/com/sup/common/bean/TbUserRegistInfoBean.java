package com.sup.common.bean;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
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
@TableName("tb_user_regist_info")
public class TbUserRegistInfoBean {
    @TableId(type = IdType.AUTO)
    private Integer id;
    private String mobile;
    private String name;
    private Date create_time;
    private Integer credit_level;
}
