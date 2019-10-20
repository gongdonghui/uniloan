package com.sup.common.bean;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

/**
 * gongshuai
 * <p>
 * 2019/10/14
 * 信审报表
 */
@Data
@TableName("tb_report_check_daily")
public class CheckReportBean {
    @TableId(type = IdType.AUTO)
    private Integer id;
    private Date data_dt;
    private Integer task_type;
    private Integer total;
    private Integer allocated;
    private Integer checked;
    private Integer denyed;


}
