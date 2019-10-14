package com.sup.cms.bean.po;

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

    private Date registDate;
}
