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
    private Integer operator;   //操作员id
    private String operator_name;   //操作员姓名
    private Integer passed;   //通过数
    private Integer checked;   //已审核数
    private Integer allocated;   //分配数
    private Integer loan_num;    //放款数
    private Integer loan_amt;    //放款金额
    private Double fpd;    //首逾率
    private Double pd3;    //逾期率D#
    private Double pd7;     //逾期率D7
    private Double pass_rate;   //通过率
    private Double loan_rate;   //放款转化率
    private Date data_dt;      //数据日期
    private Date update_time;    //更新时间

}
