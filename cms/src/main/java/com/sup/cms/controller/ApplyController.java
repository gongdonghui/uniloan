package com.sup.cms.controller;

import com.google.common.base.Strings;
import com.google.common.collect.Maps;
import com.sup.cms.bean.po.ApplyManagementGetListBean;
import com.sup.cms.bean.po.ApplyOperationTaskBean;
import com.sup.cms.bean.po.ApplyApprovalGetListBean;
import com.sup.cms.bean.vo.*;
import com.sup.cms.mapper.ApplyOperationTaskMapper;
import com.sup.cms.mapper.CrazyJoinMapper;
import com.sup.cms.util.DateUtil;
import com.sup.cms.util.GsonUtil;
import com.sup.cms.util.ResponseUtil;
import com.sup.common.bean.TbApplyInfoBean;
import com.sup.common.loan.ApplyStatusEnum;
import com.sup.common.param.ManualRepayParam;
import com.sup.common.service.CoreService;
import com.sup.common.util.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Date;
import java.util.List;
import java.util.Map;

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
    private CrazyJoinMapper crazyJoinMapper;
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
        sb.append(!Strings.isNullOrEmpty(params.getName()) ? " and e.name=\"" + params.getName() + "\"" : "");
        sb.append(!Strings.isNullOrEmpty(params.getCreditLevel()) ? " and b.credit_class=\"" + params.getCreditLevel() + "\"" : "");
        sb.append(!Strings.isNullOrEmpty(params.getCidNo()) ? " and e.cid_no=\"" + params.getCidNo() + "\"" : "");
        sb.append(!Strings.isNullOrEmpty(params.getMobile()) ? " and f.mobile=\"" + params.getMobile() + "\"" : "");
        //单子是否已领 不管是指派的还是自己领的
        sb.append(" and a.has_owner=" + params.getType1());
        //单子状态 是初审还是终审呢
        sb.append(" and a.task_type=" + (params.getType2() == 0 ? "0" : "2"));
        //审核状态 是审了还是没审呢
        sb.append(" and a.status=0");
        Integer offset = (params.getPage() - 1) * params.getPageSize();
        Integer rows = params.getPageSize();
        log.info(">>> condition: " + sb.toString());
        List<ApplyApprovalGetListBean> list = crazyJoinMapper.applyApprovalGetList(sb.toString(), offset, rows);
        Integer total = crazyJoinMapper.applyApprovalGetListForPaging(sb.toString());
        Map m = Maps.newHashMap();
        m.put("total", total);
        m.put("list", list);
        return ResponseUtil.success(m);
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
        if (params.getType() == 1 && params.getOperatorId().equals(bean.getOperatorId())) {
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
        Result<TbApplyInfoBean> ret = coreService.getApplyInfo(params.getApplyId());
        if (!ret.isSucc()) {
            log.error("Invalid applyId = " + params.getApplyId());
            return ResponseUtil.failed();
        }
        TbApplyInfoBean apply = ret.getData();

        //调用关老师接口改申请单状态
        //todo 和关佬确定一下是不是这么调用 都需要传哪些参数
        // TbApplyInfoBean apply = new TbApplyInfoBean();
        // apply.setId(params.getApplyId());

        apply.setUpdate_time(new Date());
        //如果是取消的话 直接设置取消状态
        if (params.getType().equals(2)) {
            apply.setStatus(9);
        } else {
            //区分信审和终审来分别设定状态
            if (params.getType2().equals(0)) {
                apply.setStatus(params.getType().equals(1) ? 2 : 6);
            } else {
                if (params.getGrantQuota() == null || params.getGrantQuota() < 0) {
                    log.error("Invalid grant quota!");
                }
                apply.setGrant_quota(params.getGrantQuota());
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
        StringBuilder sb = new StringBuilder();
        sb.append(null != params.getStartTime() ? " and a.create_time>=\"" + DateUtil.formatDateTime(params.getStartTime()) + "\"" : "");
        sb.append(null != params.getEndTime() ? " and a.create_time<=\"" + DateUtil.formatDateTime(params.getEndTime()) + "\"" : "");
        sb.append(null != params.getStatus() ? " and a.status=\"" + params.getStatus() + "\"" : "");
        sb.append(null != params.getApplyId() ? " and a.id=\"" + params.getApplyId() + "\"" : "");
        sb.append(null != params.getName() ? " and d.name=\"" + params.getApplyId() + "\"" : "");
        sb.append(null != params.getCidNo() ? " and d.cid_no=\"" + params.getApplyId() + "\"" : "");
        sb.append(null != params.getAppName() ? " and e.app_name=\"" + params.getApplyId() + "\"" : "");
        Integer offset = (params.getPage() - 1) * params.getPageSize();
        Integer rows = params.getPageSize();
        List<ApplyManagementGetListBean> l = crazyJoinMapper.applyManagementGetList(sb.toString(), offset, rows);
        Map m = Maps.newHashMap();
        m.put("total",crazyJoinMapper.applyManagementGetListForPaging(sb.toString()));
        m.put("list",l);
        return ResponseUtil.success(m);
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

        sb.append(!Strings.isNullOrEmpty(params.getName()) ? " and e.name=\"" + params.getName() + "\"" : "");
        sb.append(!Strings.isNullOrEmpty(params.getCid()) ? " and b.credit_class=\"" + params.getCid() + "\"" : "");
        sb.append(!Strings.isNullOrEmpty(params.getMobile()) ? " and f.mobile=\"" + params.getMobile() + "\"" : "");

        sb.append(null != params.getCreateTime() ? " and a.update_time>=\"" + DateUtil.formatDateTime(params.getCreateTime()) + "\"" : "");
        sb.append(null != params.getEndTime() ? " and a.update_time<=\"" + DateUtil.formatDateTime(params.getEndTime()) + "\"" : "");

        sb.append(null != params.getApplyCreateTime() ? " and b.create_time>=\"" + DateUtil.formatDateTime(params.getCreateTime()) + "\"" : "");
        sb.append(null != params.getApplyEndTime() ? " and b.create_time<=\"" + DateUtil.formatDateTime(params.getEndTime()) + "\"" : "");
        Integer offset = (params.getPage() - 1) * params.getPageSize();
        Integer rows = params.getPageSize();
        List<ApplyApprovalGetListBean> list = crazyJoinMapper.applyApprovalGetList(sb.toString(), offset, rows);
        list.forEach(x -> x.setReAllocate(null == x.getOperatorId() ? 0 : 1));
        Map m = Maps.newHashMap();
        m.put("total",crazyJoinMapper.applyApprovalGetListForPaging(sb.toString()));
        m.put("list",list);
        log.info(">>> condition: " + sb.toString());
        return ResponseUtil.success(m);
    }

    /**
     * 指派历史-重新指派
     *
     * @param params
     * @return
     */
    @PostMapping("/allocation/re")
    public String reAllocation(@Valid @RequestBody ApplyApprovalAllocationParams params) {
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

    /**
     * 核销
     * @param applyId
     * @return
     */
    @GetMapping("/writeOff")
    public String writeOff(@RequestParam("applyId") String applyId) {
        Result<TbApplyInfoBean> result = coreService.getApplyInfo(Integer.valueOf(applyId));
        if (!result.isSucc()) {
            return ResponseUtil.failed(result.getMessage());
        }
        TbApplyInfoBean applyInfoBean = result.getData();
        if (applyInfoBean == null) {
            return ResponseUtil.failed("Invalid applyId!");
        }
        applyInfoBean.setStatus(ApplyStatusEnum.APPLY_WRITE_OFF.getCode());
        if (coreService.updateApplyInfo(applyInfoBean).isSucc()) {
            return ResponseUtil.success();
        }
        log.error("Failed to write off apply:" + applyId);
        return ResponseUtil.failed();
    }

    /**
     * 还款
     * @param params
     * @return
     */
    @PostMapping("/repay")
    public String repay(@Valid @RequestBody ApplyRepayParams params) {
        Result<TbApplyInfoBean> result = coreService.getApplyInfo(params.getApplyId());
        if (!result.isSucc()) {
            return ResponseUtil.failed(result.getMessage());
        }
        TbApplyInfoBean applyInfoBean = result.getData();
        if (applyInfoBean == null) {
            return ResponseUtil.failed("Invalid applyId!");
        }
        ManualRepayParam repayParam = new ManualRepayParam();
        repayParam.setAmount(params.getRepayAmount());
        repayParam.setApplyId(String.valueOf(params.getApplyId()));
        repayParam.setOperatorId(String.valueOf(params.getOperatorId()));
        repayParam.setUserId(String.valueOf(params.getUserId()));
        repayParam.setRepayTime(params.getRepayDate());
        if (!coreService.manualRepay(repayParam).isSucc()) {
            return ResponseUtil.failed("Manual repay failed! params=" + GsonUtil.toJson(params));
        }
        return ResponseUtil.success();
    }

    /**
     * 查询借款情况
     * @param params
     * @return
     */
    @PostMapping("/queryLoanInfo")
    public String queryLoanInfo(@Valid @RequestBody ApplyQueryParams params) {
        // TODO
        return ResponseUtil.success();
    }

}