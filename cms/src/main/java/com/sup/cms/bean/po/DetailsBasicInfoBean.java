package com.sup.cms.bean.po;

import lombok.Data;

import java.util.List;

/**
 * 申请人详细信息-基本信息
 *
 * @Author: kouichi
 * @Date: 2019/10/7 18:30
 */
@Data
public class DetailsBasicInfoBean {
    //    姓名
    private String name;
    //            证件号
    private String cidNo;
    //    性别
    private Integer gender;
    //            所在地
    private Integer city;
    //    居住地址
    private String addr;
    //            居住时长居住时长 0|3个月  1|6个月
    private Integer duration;
    //    最高学历 学历   0|小学 1|初中 2|高中 3|中间 4|学院 5|综合性大学 6|大学后
    private Integer education;
    //            婚姻状况 婚姻状态    0|已婚 1|单身 2|离异 3|丧偶
    private Integer marriage;
    //    贷款目的 purpose：用途 0|旅游  1|买车
    private Integer purpose;
    //            子女数
    private Integer childrenCount;
    //    公司名称
    private String company;
    //            公司所在地
    private Integer companyCity;
    //    单位地址
    private String companyAddr;
    //            办公电话
    private String phone;
    //    职业 职业类型 0|工程师  1|服务行业
    private Integer job;
    //            薪资范围 收入状态 0|1~100  1|100~1000
    private Integer income;
    // 紧急联系人
    private List<DetailsEmergencyContact> list;
}
