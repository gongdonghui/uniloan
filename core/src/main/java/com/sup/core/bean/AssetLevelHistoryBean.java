package com.sup.core.bean;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

/**
 * gongshuai
 * <p>
 * 2020/2/29
 */
@Data
@TableName(value = "tb_asset_level_history")
public class AssetLevelHistoryBean {
    @TableId
    private Integer id;
    private Date data_dt;
    private Integer asset_level;
    private String apply_list;   //  apply_id,apply_id.....
}
