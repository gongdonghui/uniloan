package com.sup.cms.bean.po;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;

/**
 * @Author: kouichi
 * @Date: 2019/10/13 19:52
 */
@Data
public class CustomerInfoBean {
    private Integer userId;
    private String name;
    private String mobile;
    private String cidNo;

    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss", timezone = "GMT+7")
    private Date registDate;
}
