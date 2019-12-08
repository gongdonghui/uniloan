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
    private Date date_dt;  //日期
    private Integer task_type;   //任务类型
    private Integer total;      //总单数
    private Integer allocated;   //分配单数
    private Integer checked;     //已审单数
    private Integer denyed;     //拒绝单数


}
