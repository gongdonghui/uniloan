package com.sup.cms.bean.po;

import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;

import java.util.Date;

/**
 * @Author: kouichi
 * @Date: 2019/9/22 13:38
 */
@Data
public class ApplyApprovalGetListBean {
    /**
     * 任务编号
     */
    private Integer id;
    /**
     * 订单号
     */
    private Integer applyId;
    /**
     * 任务名称
     * 我返回 数字 你mapping成中文
     * 0信审
     * 2终审
     */
    private Integer taskType;
    /**
     * 执行结果
     * 我返回数字 你mapping成中文
     * 0 未审核
     * 1 已审核
     */
    private Integer status;
    /**
     * 审批人
     */
    private Integer operatorId;
    /**
     * 指派时间
     */
    private Date updateTime;
    /**
     * 可重新指派
     * 0 不可 1 可以
     */
    private Integer reAllocate;
    /**
     * 申请产品
     */
    private String productName;
    /**
     * 客户姓名
     */
    private String name;
    /**
     * 信用等级
     */
    private String creditClass;
    /**
     * 申请金额
     */
    private Integer applyQuota;
    /**
     * 进件添加时间
     */
    private Date applyCreateTime;
    /**
     * 任务生成时间
     */
    private Date createTime;
    /**
     * 身份证号
     */
    private String cidNo;
    /**
     * 电话
     */
    private String mobile;
    /**
     * 进件失效时间
     */
    private Date applyExpireTime;
}
