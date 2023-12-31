package com.sup.common.bean;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

/**
 * gongshuai
 * <p>
 * 2019/9/21
 */
@Data
@TableName("tb_core_assets_level_ruels")
public class AssetsLevelRuleBean {
    @TableId(type = IdType.AUTO)
    private Integer id;
    private Integer between_paydays;
    private Integer level;
    private String level_name;
    private Date create_time;
}
