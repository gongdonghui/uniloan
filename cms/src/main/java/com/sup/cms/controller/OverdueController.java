package com.sup.cms.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.google.common.base.Strings;
import com.google.common.collect.Maps;
import com.sup.cms.bean.po.ApplyAssetJoinBean;
import com.sup.cms.bean.po.OverdueGetListBean;
import com.sup.cms.bean.vo.OverdueGetListParams;
import com.sup.cms.bean.vo.OverdueRecallListParams;
import com.sup.cms.bean.vo.OverdueTaskAllocateParams;
import com.sup.cms.bean.vo.OverdueTaskRecycleParams;
import com.sup.cms.mapper.CrazyJoinMapper;
import com.sup.cms.mapper.OperationTaskConfigBeanMapper;
import com.sup.cms.mapper.OperationTaskHistoryMapper;
import com.sup.cms.mapper.OperationTaskMapper;
import com.sup.common.bean.OperationTaskConfigBean;
import com.sup.common.bean.TbOperationTaskBean;
import com.sup.common.bean.TbOperationTaskHistoryBean;
import com.sup.common.loan.OperationTaskStatusEnum;
import com.sup.common.loan.OperationTaskTypeEnum;
import com.sup.common.util.DateUtil;
import com.sup.common.util.GsonUtil;
import com.sup.common.util.ResponseUtil;
import lombok.extern.log4j.Log4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * @Author: kouichi
 * @Date: 2019/10/13 19:47
 */
@Log4j
@RestController
@RequestMapping("/overdue")
public class OverdueController {

    @Autowired
    private CrazyJoinMapper crazyJoinMapper;

    @Autowired
    private OperationTaskMapper operationTaskMapper;
    @Autowired
    private OperationTaskHistoryMapper operationTaskHistoryMapper;

    @Autowired
    private OperationTaskConfigBeanMapper operationTaskConfigBeanMapper;


    /**
     * 催收待指派任务列表（查询所有非完成状态的任务）
     * 催收列表查询（查询个人催收任务，包含已完成任务）
     *
     * @param params
     * @return
     */
    @PostMapping("/pool/getList")
    public String getPool(@RequestBody @Valid OverdueGetListParams params) {
        StringBuilder sb = new StringBuilder();
        if (params.getApplyId() != null) {
            sb.append(" and ai.id=" + params.getApplyId());
        }
        if (params.getStartDate() != null) {
            sb.append(" and rp.repay_end_date>='" + DateUtil.startOf(params.getStartDate()) + "'");
        }
        if (params.getEndDate() != null) {
            sb.append(" and rp.repay_end_date<='" + DateUtil.endOf(params.getEndDate()) + "'");
        }
        if (params.getOverdueDays() != null) {
            Date repay_dt = DateUtil.getDate(new Date(), -1 * params.getOverdueDays());
            sb.append(" and rp.repay_end_date>='" + DateUtil.startOf(repay_dt) + "'");
            sb.append(" and rp.repay_end_date<='" + DateUtil.endOf(repay_dt) + "'");
        }
        if (params.getProductId() != null) {
            sb.append(" and pi.id=" + params.getProductId());
        }
        if (!Strings.isNullOrEmpty(params.getName())) {
            sb.append(" and b.name='" + params.getName() + "'");
        }
        if (!Strings.isNullOrEmpty(params.getMobile())) {
            sb.append(" and uri.mobile='" + params.getMobile() + "'");
        }
        if (!Strings.isNullOrEmpty(params.getCidNo())) {
            sb.append(" and b.cid_no='" + params.getCidNo() + "'");
        }
        if (params.getOperatorId() != null) {   // 查询个人任务列表
            sb.append(" and ot.operator_id=" + params.getOperatorId());
        } else {    // 查询所有可分配任务，包含正常放款订单、不包含已完成任务
            sb.append(" and (ot.status is null or ot.status!=1)");
        }

        log.info("getPool conditions=" + sb.toString());
        Integer offset = (params.getPage() - 1) * params.getPageSize();
        Integer rows = params.getPageSize();
        List<OverdueGetListBean> l = crazyJoinMapper.getPoolList(sb.toString(), offset, rows);
        Map m = Maps.newHashMap();
        m.put("total", crazyJoinMapper.getPoolListCount(sb.toString()));
        m.put("list", l);
        return ResponseUtil.success(m);
    }

    /**
     * 催收档案，待回收任务列表(需排除已完成任务）
     */
    @PostMapping("/task/getList")
    public String getTaskList(@RequestBody @Valid OverdueGetListParams params) {
        StringBuilder sb = new StringBuilder();
        if (params.getApplyId() != null) {
            sb.append(" and ai.id=" + params.getApplyId());
        }
        if (params.getStartDate() != null) {
            sb.append(" and rp.repay_end_date>='" + DateUtil.startOf(params.getStartDate()) + "'");
        }
        if (params.getEndDate() != null) {
            sb.append(" and rp.repay_end_date<='" + DateUtil.endOf(params.getEndDate()) + "'");
        }
        if (params.getProductId() != null) {
            sb.append(" and pi.id=" + params.getProductId());
        }
        if (!Strings.isNullOrEmpty(params.getName())) {
            sb.append(" and b.name like '%" + params.getName().trim()+ "%'");
        }
        if (!Strings.isNullOrEmpty(params.getMobile())) {
            sb.append(" and uri.mobile='" + params.getMobile() + "'");
        }
        if (!Strings.isNullOrEmpty(params.getCidNo())) {
            sb.append(" and b.cid_no='" + params.getCidNo() + "'");
        }
        if (params.getOperatorId() != null) {
            sb.append(" and ot.operator_id=" + params.getOperatorId());
        }

        log.info("getTaskList conditions=" + sb.toString());
        Integer offset = (params.getPage() - 1) * params.getPageSize();
        Integer rows = params.getPageSize();
        List<OverdueGetListBean> l = crazyJoinMapper.getTaskList(sb.toString(), offset, rows);

        Map m = Maps.newHashMap();
        log.info("Task List Element str:"+ GsonUtil.toJson(l.get(0).toString()));
        m.put("total", crazyJoinMapper.getTaskListCount(sb.toString()));
        m.put("list", l);
        return ResponseUtil.success(m);
    }

    /**
     * 催收分配（按催收员或者自动分配）
     */
    @PostMapping("/task/allocate")
    public String allocateTask(@RequestBody @Valid OverdueTaskAllocateParams params) {
        log.info("allocate overdue task, param=" + GsonUtil.toJson(params));
        Date now = new Date();
        boolean needUpdate;

        if (params.getApplyIdList() != null && params.getApplyIdList().size() > 0) {
            for (Integer applyId : params.getApplyIdList()) {
                this.assignSingleTask(applyId, params.getOperatorId(), params.getDistributor_id());
            }
        } else {
            Integer assetLevel = params.getAsset_level();
            if (assetLevel != null) {

                List<Integer> applyList = this.crazyJoinMapper.getOperationTaskByAssetLevel(assetLevel);

                List<Integer> operators = this.operationTaskConfigBeanMapper.getOperatorsByLevel(assetLevel);
                if (operators != null && !operators.isEmpty()) {

                    LinkedList<Integer> queue = new LinkedList<>();
                    queue.addAll(applyList);
                    int next = 0;
                    while (!queue.isEmpty() && next < operators.size()) {
                        Integer operator = operators.get(next++);
                        Integer applyId = queue.pop();
                        assignSingleTask(operator, applyId, -1001);
                        next = next % operators.size();
                    }
                }
            }
        }

        return ResponseUtil.success();
    }


    private void assignSingleTask(Integer applyId, Integer operatorId, Integer distributorId) {

        Date  now  = new  Date();
        QueryWrapper<TbOperationTaskBean> wrapper = new QueryWrapper<>();
        wrapper.eq("apply_id", applyId);
        wrapper.eq("task_type", OperationTaskTypeEnum.TASK_OVERDUE.getCode());
        // wrapper.eq("has_owner", 0);
        TbOperationTaskBean taskBean = operationTaskMapper.selectOne(wrapper);
        boolean needUpdate = true;
        if (taskBean == null) {
            taskBean = new TbOperationTaskBean();
            taskBean.setCreate_time(now);
            needUpdate = false;
        }
        taskBean.setApply_id(applyId);
        taskBean.setOperator_id(operatorId);
        taskBean.setDistributor_id(distributorId);
        taskBean.setHas_owner(1);
        taskBean.setStatus(OperationTaskStatusEnum.TASK_STATUS_NEW.getCode());
        taskBean.setTask_type(OperationTaskTypeEnum.TASK_OVERDUE.getCode());
        taskBean.setUpdate_time(now);

        recordOperationTask(taskBean);
        if (needUpdate) {
            if (operationTaskMapper.updateById(taskBean) <= 0) {
                log.error("Failed to update task: " + GsonUtil.toJson(taskBean));
            }

        } else {
            if (operationTaskMapper.insert(taskBean) <= 0) {
                log.error("Failed to add new task: " + GsonUtil.toJson(taskBean));
            }
        }
    }

    /**
     * 催收任务回收
     */
    @PostMapping("/task/recycle")
    public String recycleTask(@RequestBody @Valid OverdueTaskAllocateParams params) {
        log.info("recycle overdue task, param=" + GsonUtil.toJson(params));
        QueryWrapper<TbOperationTaskBean> wrapper = new QueryWrapper<>();
        wrapper.eq("task_type", OperationTaskTypeEnum.TASK_OVERDUE.getCode());
        wrapper.eq("has_owner", 1);
        if (params.getOperatorId() != null) {
            wrapper.eq("operator_id", params.getOperatorId());
        }
        if (params.getApplyIdList() != null && params.getApplyIdList().size() > 0) {
            wrapper.in("apply_id", params.getApplyIdList());
        }
        TbOperationTaskBean newTaskBean = new TbOperationTaskBean();
        newTaskBean.setOperator_id(null);
        newTaskBean.setDistributor_id(params.getDistributor_id());
        newTaskBean.setStatus(OperationTaskStatusEnum.TASK_STATUS_CANCEL.getCode());
        newTaskBean.setHas_owner(0);
        newTaskBean.setUpdate_time(new Date());

        log.info("[SQL] recycle overdue task:" + wrapper.getSqlSegment());
        if (operationTaskMapper.update(newTaskBean, wrapper) <= 0) {
            log.error("batch update failed!");
            return ResponseUtil.failed();
        }

        return ResponseUtil.success();
    }


    /**
     * 已催回任务列表
     */
    @PostMapping("/task/recallList")
    public String getRecallList(@RequestBody @Valid OverdueRecallListParams params) {
        StringBuilder sb = new StringBuilder();
        if (params.getAllocStartDate() != null) {
            sb.append(" and rod.data_dt>='" + DateUtil.startOf(params.getAllocStartDate()) + "'");
        }
        if (params.getAllocEndDate() != null) {
            sb.append(" and rod.data_dt<='" + DateUtil.endOf(params.getAllocEndDate()) + "'");
        }
        if (params.getRepayStartDate() != null) {
            sb.append(" and rp.repay_time>='" + DateUtil.startOf(params.getRepayStartDate()) + "'");
        }
        if (params.getRepayStartDate() != null) {
            sb.append(" and rp.repay_time<='" + DateUtil.endOf(params.getRepayEndDate()) + "'");
        }
        if (params.getLoanStartDate() != null) {
            sb.append(" and ai.loan_time>='" + DateUtil.startOf(params.getRepayStartDate()) + "'");
        }
        if (params.getLoanStartDate() != null) {
            sb.append(" and ai.loan_time <='" + DateUtil.endOf(params.getRepayEndDate()) + "'");
        }

        if (params.getOperatorId() != null) {
            sb.append(" and rod.operator_id=" + params.getOperatorId());
        }

        log.info("getRecallList conditions=" + sb.toString());
        Integer offset = (params.getPage() - 1) * params.getPageSize();
        Integer rows = params.getPageSize();
        List<OverdueGetListBean> l = crazyJoinMapper.getRecallList(sb.toString(), offset, rows);
        Map m = Maps.newHashMap();
        m.put("total", crazyJoinMapper.getRecallListCount(sb.toString()));
        m.put("list", l);
        return ResponseUtil.success(m);
    }


    private void recordOperationTask(TbOperationTaskBean operationTaskBean) {

        QueryWrapper<TbOperationTaskHistoryBean> wrapper = new QueryWrapper<>();
        wrapper.eq("apply_id", operationTaskBean.getApply_id());
        wrapper.eq("task_type", operationTaskBean.getTask_type());
        List<TbOperationTaskHistoryBean> taskBeans = operationTaskHistoryMapper.selectList(wrapper);
        TbOperationTaskHistoryBean fromBean = null;    // 订单任务拥有者
        TbOperationTaskHistoryBean taskBean = null;    // 订单任务新任拥有着
        for (TbOperationTaskHistoryBean bean : taskBeans) {
            if (bean.getHas_owner() == 1) {
                if (fromBean != null) {
                    log.error("Invalid task allocation! bean1=" + GsonUtil.toJson(fromBean) +
                            ", bean2=" + GsonUtil.toJson(bean));
                    return;
                }
                fromBean = bean;
            }
            if (bean.getOperator_id().equals(operationTaskBean.getOperator_id())) { // 再次分配给以往的催收员
                taskBean = bean;
            }
        }

        if (fromBean != null) {
            fromBean.setHas_owner(0);
            fromBean.setStatus(OperationTaskStatusEnum.TASK_STATUS_CANCEL.getCode());
            fromBean.setUpdate_time(new Date());
            if (operationTaskHistoryMapper.updateById(fromBean) <= 0) {
                log.error("Failed to update task: " + GsonUtil.toJson(fromBean));
            }
        }

        if (taskBean == null) {
            taskBean = new TbOperationTaskHistoryBean();
            taskBean.copy(operationTaskBean);
            if (operationTaskHistoryMapper.insert(taskBean) <= 0) {
                log.error("Failed to add new task: " + GsonUtil.toJson(taskBean));
            }
        } else {
            taskBean.copy(operationTaskBean);
            if (operationTaskHistoryMapper.updateById(taskBean) <= 0) {
                log.error("Failed to update task: " + GsonUtil.toJson(taskBean));
            }
        }
    }
}
