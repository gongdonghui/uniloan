package com.sup.cms.bean.po;

import lombok.Data;

import java.util.Date;

/**
 * @Author: kouichi
 * @Date: 2019/10/2 15:51
 */
@Data
public class ApplyManagementGetListBean {
    private Integer applyId;
    private Integer status;
    private String name;
    private String productName;
    private Integer amount;
    private String jieKuanQiXian;
    private String huanKuanFangShi;
    private String shangHuMingCheng;
    private String appName;
    private Date dealDate;
    private Date createTime;
    private Date updateTime;
}