package com.sup.common.param;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import javax.validation.constraints.Min;
import java.util.Date;

/**
 * gongshuai
 * <p>
 * 2020/2/5
 */
@Data
public class CheckOverviewParam {
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss", timezone = "GMT+7")
    private Date start_date;

    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss", timezone = "GMT+7")
    private Date end_date;

    private  String  type;  // first， final
    private  String  operator_id;   //信审员id

    @Min(0)
    private Integer page;

    @Min(0)
    private Integer pageSize;
}
