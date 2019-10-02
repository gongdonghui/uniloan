package com.sup.cms.controller;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.sup.cms.bean.po.ApplyManagementGetListBean;
import com.sup.cms.bean.po.ApplyOperationTaskBean;
import com.sup.cms.bean.po.ApprovalGetListBean;
import com.sup.cms.bean.vo.*;
import com.sup.cms.mapper.ApplyOperationTaskMapper;
import com.sup.cms.util.DateUtil;
import com.sup.cms.util.ResponseUtil;
import com.sup.common.bean.TbApplyInfoBean;
import com.sup.common.service.CoreService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Date;
import java.util.List;

/**
 * 进件管理页面
 *
 * @Author: kouichi
 * @Date: 2019/9/22 11:00
 */
@RequestMapping("/apply")
@RestController
@Slf4j
public class ApplyController {

    @Autowired
    private ApplyOperationTaskMapper applyOperationTaskMapper;
    @Autowired
    private CoreService coreService;

    /**
     * 信审或者终审的待指派或者待领取-列表
     * 根据不同参数 可以展示不同列表
     *
     * @param params
     * @return
     */
    @PostMapping("/approval/getList")
    public String getList1(@Valid @RequestBody ApplyApprovalGetListParams params) {
        StringBuilder sb = new StringBuilder();
        //下面这部分内容为前端查询时的参数
        sb.append(null != params.getApplyId() ? " and a.apply_id=\"" + params.getApplyId() + "\"" : "");
        sb.append(null != params.getStartTime() ? " and b.create_time>=\"" + DateUtil.formatDateTime(params.getStartTime()) + "\"" : "");
        sb.append(null != params.getEndTime() ? " and b.create_time<=\"" + DateUtil.formatDateTime(params.getEndTime()) + "\"" : "");
        sb.append(Strings.isNullOrEmpty(params.getName()) ? " and d.name=\"" + params.getName() + "\"" : "");
        sb.append(Strings.isNullOrEmpty(params.getCreditLevel()) ? " and b.credit_class=\"" + params.getCreditLevel() + "\"" : "");
        sb.append(Strings.isNullOrEmpty(params.getCidNo()) ? " and d.cid_no=\"" + params.getCidNo() + "\"" : "");
        sb.append(Strings.isNullOrEmpty(params.getMobile()) ? " and e.mobile=\"" + params.getMobile() + "\"" : "");
        //单子是否已领 不管是指派的还是自己领的
        sb.append(" and a.has_owner=\"" + params.getType1() + "\"");
        //单子状态 是初审还是终审呢
        sb.append(" and a.task_type=\"" + (params.getType2() == 0 ? "0" : "2") + "\"");
        //审核状态 是审了还是没审呢
        sb.append(" and a.status=\"0\"");
        Integer offset = (params.getPage() - 1) * params.getPageSize();
        Integer rows = params.getPageSize();
        List<ApprovalGetListBean> list = applyOperationTaskMapper.search(sb.toString(), offset, rows);
        return ResponseUtil.success(list);
    }

    /**
     * 信审或者终审的待指派或者待领取-按钮
     * 根据不同参数 既可以是主动认领也可以是被指派
     *
     * @param params
     * @return
     */
    @PostMapping("/approval/allocation")
    public String allocation(@Valid @RequestBody ApplyApprovalAllocationParams params) {
        //先查询是否指派或认领过 如有指派或认领 直接返回失败
        ApplyOperationTaskBean bean = applyOperationTaskMapper.selectById(params.getId());
        if (bean == null || (!bean.getTaskType().equals(0) && !bean.getTaskType().equals(2))) {
            return ResponseUtil.failed("任务不存在或任务状态异常");
        }
        if (bean.getHasOwner().equals(1)) {
            return ResponseUtil.failed("该任务已被认领，操作失败");
        }
        //如果是终审 查询一下和信审是否为同一个人 同一个人直接拒绝
        if (params.getType() == 1 && bean.getOperatorId().equals(params.getOperatorId())) {
            return ResponseUtil.failed("终审和信审的审核人不能为同一人，操作失败");
        }
        bean.setOperatorId(params.getOperatorId());
        if (params.getDistributorId() != null) {
            bean.setDistributorId(params.getDistributorId());
        }
        bean.setHasOwner(1);
        if (applyOperationTaskMapper.updateById(bean) > 0) {
            return ResponseUtil.success();
        } else {
            return ResponseUtil.failed();
        }
    }

    /**
     * 审批按钮
     *
     * @param params
     * @return
     */
    @PostMapping("/approval/action")
    public String action(@Valid @RequestBody ApplyApprovalActionParams params) {
        ApplyOperationTaskBean bean = applyOperationTaskMapper.selectById(params.getId());
        //检查当前人和审批人是不是一个人
        if (!bean.getOperatorId().equals(params.getOperatorId())) {
            return ResponseUtil.failed("当前用户不能审批此单");
        }
        //修改task表审批状态
        bean.setStatus(1);
        bean.setComment(params.getComment());
        if (applyOperationTaskMapper.updateById(bean) <= 0) {
            return ResponseUtil.failed();
        }
        //调用关老师接口改申请单状态
        //todo 和关佬确定一下是不是这么调用 都需要传哪些参数
        TbApplyInfoBean apply = new TbApplyInfoBean();
        apply.setId(params.getId());
        apply.setUpdate_time(new Date());
        //如果是取消的话 直接设置取消状态
        if (params.getType().equals(2)) {
            apply.setStatus(9);
        } else {
            //区分信审和终审来分别设定状态
            if (params.getType2().equals(0)) {
                apply.setStatus(params.getType().equals(1) ? 2 : 6);
            } else {
                apply.setStatus(params.getType().equals(1) ? 4 : 8);
            }
        }
        if (coreService.updateApplyInfo(apply).getStatus() == 0) {
            return ResponseUtil.success();
        } else {
            return ResponseUtil.failed();
        }
    }

    /**
     * 进件管理页面-列表
     *
     * @param params
     * @return
     */
    @PostMapping("/management/getList")
    public String getList2(@Valid @RequestBody ApplyManagementGetListParams params) {
        //todo 不知道字段都在哪 待开发
        List<ApplyManagementGetListBean> l = Lists.newArrayList();
        return ResponseUtil.success(l);
    }

    /**
     * 指派历史-列表
     *
     * @param params
     * @return
     */
    @PostMapping("/allocation/history")
    public String history(@Valid @RequestBody ApplyAllocationHistoryParams params) {
        StringBuilder sb = new StringBuilder();
        //下面这部分内容为前端查询时的参数
        sb.append(null != params.getApplyId() ? " and a.apply_id=\"" + params.getApplyId() + "\"" : "");
        sb.append(null != params.getOperatorId() ? " and a.operator_id=\"" + params.getOperatorId() + "\"" : "");
        sb.append(null != params.getDistributorId() ? " and a.distributor_id=\"" + params.getDistributorId() + "\"" : "");
        sb.append(null != params.getTaskType() ? " and a.task_type=\"" + params.getTaskType() + "\"" : "");
        sb.append(null != params.getStatus() ? " and a.status=\"" + params.getStatus() + "\"" : "");
        sb.append(null != params.getProductId() ? " and c.id=\"" + params.getProductId() + "\"" : "");

        sb.append(Strings.isNullOrEmpty(params.getName()) ? " and d.name=\"" + params.getName() + "\"" : "");
        sb.append(Strings.isNullOrEmpty(params.getCid()) ? " and b.credit_class=\"" + params.getCid() + "\"" : "");
        sb.append(Strings.isNullOrEmpty(params.getMobile()) ? " and e.mobile=\"" + params.getMobile() + "\"" : "");

        sb.append(null != params.getCreateTime() ? " and a.update_time>=\"" + DateUtil.formatDateTime(params.getCreateTime()) + "\"" : "");
        sb.append(null != params.getEndTime() ? " and a.update_time<=\"" + DateUtil.formatDateTime(params.getEndTime()) + "\"" : "");

        sb.append(null != params.getApplyCreateTime() ? " and b.create_time>=\"" + DateUtil.formatDateTime(params.getCreateTime()) + "\"" : "");
        sb.append(null != params.getApplyEndTime() ? " and b.create_time<=\"" + DateUtil.formatDateTime(params.getEndTime()) + "\"" : "");
        Integer offset = (params.getPage() - 1) * params.getPageSize();
        Integer rows = params.getPageSize();
        List<ApprovalGetListBean> list = applyOperationTaskMapper.search(sb.toString(), offset, rows);
        list.forEach(x -> x.setReAllocate(null == x.getOperatorId() ? 0 : 1));
        return ResponseUtil.success(list);
    }

    /**
     * 指派历史-重新指派
     *
     * @param params
     * @return
     */
    @PostMapping("/allocation/re")
    public String reAllocation(@Valid @RequestBody ApplyApprovalAllocationParams params) {
        //todo 重新指派的时候 是否只可以 重新指派自己曾经指派过的  还是胡乱的指派
        ApplyOperationTaskBean bean = new ApplyOperationTaskBean();
        bean.setId(params.getId());
        bean.setOperatorId(params.getOperatorId());
        if (params.getDistributorId() != null) {
            bean.setDistributorId(params.getDistributorId());
        }
        if (applyOperationTaskMapper.updateById(bean) > 0) {
            return ResponseUtil.success();
        } else {
            return ResponseUtil.failed();
        }
    }

    /**
     * 挂起按钮
     *
     * @param applyId
     * @return
     */
    @GetMapping("/approval/hangUp")
    public String hangUp(@RequestParam("applyId") String applyId) {
        //todo 不知道挂起是要干啥 看看能不能这期先不做
        return ResponseUtil.success();
    }

}