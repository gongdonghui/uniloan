package com.sup.common.bean;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

/**
 * gongshuai
 * <p>
 * 2020/2/5
 */
@Data
@TableName("tb_report_check_operator_daily")
public class TbReportCheckOperatorDaily {
    @TableId(type = IdType.AUTO)
    private Integer id;
    private Integer operator;
    private Integer passed;
    private Integer checked;
    private Integer allocated;
    private Integer loan_num;
    private Integer loan_amt;
    private Double fpd;
    private Double pd3;
    private Double pd7;
    private Double pass_rate;
    private Double loan_rate;
    private Date data_dt;
    private Date update_time;

}
