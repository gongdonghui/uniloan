package com.sup.cms.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.google.common.base.Strings;
import com.google.common.collect.Maps;
import com.sup.cms.bean.po.OverdueGetListBean;
import com.sup.cms.bean.vo.OverdueGetListParams;
import com.sup.cms.bean.vo.OverdueTaskAllocateParams;
import com.sup.cms.bean.vo.OverdueTaskRecycleParams;
import com.sup.cms.mapper.CrazyJoinMapper;
import com.sup.cms.mapper.OperationTaskMapper;
import com.sup.common.bean.TbOperationTaskBean;
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


    /**
     * 催收列表（查询所有）
     * 催收列表查询（查询个人催收任务）
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
        if (params.getOperatorId() != null) {
            sb.append(" and ot.operator_id=" + params.getOperatorId());
        }

        log.info("getPool conditions=" + sb.toString());
        Integer offset = (params.getPage() - 1) * params.getPageSize();
        Integer rows = params.getPageSize();
        List<OverdueGetListBean> l = crazyJoinMapper.getPoolList(sb.toString(), offset, rows);
        Map m = Maps.newHashMap();
        m.put("total",crazyJoinMapper.getPoolListCount(sb.toString()));
        m.put("list",l);
        return ResponseUtil.success(m);
    }

    /**
     * 催收任务列表
     */
    @PostMapping("/task/getList")
    public String getTaskList(@RequestBody @Valid OverdueGetListParams params) {
        // TODO
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
            sb.append(" and b.name='" + params.getName() + "'");
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
        m.put("total",crazyJoinMapper.getTaskListCount(sb.toString()));
        m.put("list",l);
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
                QueryWrapper<TbOperationTaskBean> wrapper = new QueryWrapper<>();
                wrapper.eq("apply_id", applyId);
                wrapper.eq("task_type", OperationTaskTypeEnum.TASK_OVERDUE.getCode());
                wrapper.eq("has_owner", 0);
                TbOperationTaskBean taskBean = operationTaskMapper.selectOne(wrapper);
                needUpdate = true;
                if (taskBean == null) {
                    taskBean = new TbOperationTaskBean();
                    needUpdate = false;
                }
                taskBean.setApply_id(applyId);
                taskBean.setOperator_id(params.getOperatorId());
                taskBean.setDistributor_id(params.getDistributor_id());
                taskBean.setHas_owner(1);
                taskBean.setStatus(OperationTaskStatusEnum.TASK_STATUS_NEW.getCode());
                taskBean.setTask_type(OperationTaskTypeEnum.TASK_OVERDUE.getCode());
                taskBean.setUpdate_time(now);
                if (needUpdate) {
                    if (operationTaskMapper.updateById(taskBean) <= 0) {
                        log.error("Failed to update task: " + GsonUtil.toJson(taskBean));
                        return ResponseUtil.failed();
                    }

                } else {
                    taskBean.setCreate_time(now);
                    if (operationTaskMapper.insert(taskBean) <= 0) {
                        log.error("Failed to add new task: " + GsonUtil.toJson(taskBean));
                        return ResponseUtil.failed();
                    }
                }
            }
        } else {
            // auto allocate
            // TODO

        }

        return ResponseUtil.success();
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
     * 催收报表
     */
    @PostMapping("/report/get")
    public String getReport(@RequestBody @Valid OverdueGetListParams params) {
        // TODO
        return ResponseUtil.success();
    }

}
