package com.sup.cms.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.google.common.base.Strings;
import com.google.common.collect.Maps;
import com.sup.cms.bean.po.*;
import com.sup.cms.bean.vo.*;
import com.sup.cms.mapper.*;
import com.sup.common.loan.OperationTaskTypeEnum;
import com.sup.common.util.ResponseUtil;
import com.sup.common.util.GsonUtil;
import lombok.extern.log4j.Log4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Date;
import java.util.List;
import java.util.Map;

// 逾期派单
// ->查询关老师operation表，把逾期的记录显示出来
// ->管理员进行派单操作
// ->根据applyId找到operation表中对应的记录 update 指派人 被指派人
// ->打开我的催收页面
// ->显示所有指派给自己的单子
// ->找到要催收的单子，点详情，拉到最下面可一条一条的添加 催收记录 这就完成了整个催收流程
// ->催收档案里显示所有已经指派了人的催收情况 在这里可以重新指派之类的

/**
 * 催收管理页面
 *
 * @Author: kouichi
 * @Date: 2019/9/24 15:50
 */
@RequestMapping("/collection")
@RestController
@Log4j
public class CollectionController {

    @Autowired
    private CollectionRecordBeanMapper collectionRecordBeanMapper;
    @Autowired
    private CollectionAllocateRecordBeanMapper collectionAllocateRecordBeanMapper;
    @Autowired
    private ApplyOperationTaskMapper applyOperationTaskMapper;
    @Autowired
    private AuthUserBeanMapper userBeanMapper;
    @Autowired
    private CrazyJoinMapper crazyJoinMapper;

    /**
     * 查看指派记录按钮
     *
     * @param applyId
     * @return
     */
    @GetMapping("/allocateRecords")
    public String allocateRecords(@RequestParam("applyId") String applyId) {
        QueryWrapper<CollectionAllocateRecordBean> qw = new QueryWrapper<>();
        qw.eq("apply_id", applyId);
        List<CollectionAllocateRecordBean> l = collectionAllocateRecordBeanMapper.selectList(qw);
        return ResponseUtil.success(l);
    }

    /**
     * 添加催收记录按钮
     *
     * @param params
     * @return
     */
    @PostMapping("/addRecord")
    public String addRecord(@Valid @RequestBody CollectionAddAllocateRecordParams params) {
        //添加催收记录时把最新的催收状态 存入task的comment中
        log.info("addRecord param:" + GsonUtil.toJson(params));
        QueryWrapper<ApplyOperationTaskBean> wrapper = new QueryWrapper<>();
        wrapper.eq("apply_id", params.getApplyId());
        wrapper.eq("task_type", OperationTaskTypeEnum.TASK_OVERDUE.getCode());
        wrapper.eq("operator_id", params.getOperatorId());

        ApplyOperationTaskBean task = applyOperationTaskMapper.selectOne(wrapper);
        if (task == null) {
            log.error("No task for param:" + GsonUtil.toJson(params));
            return ResponseUtil.failed();
        }
        task.setComment(params.getStatus());
        task.setUpdateTime(new Date());
        if (applyOperationTaskMapper.updateById(task) <= 0) {
            return ResponseUtil.failed();
        }
        TbCollectionRecordBean bean = GsonUtil.beanCopy(params, TbCollectionRecordBean.class);
        bean.setCreateTime(new Date());
        if (collectionRecordBeanMapper.insert(bean) <= 0) {
            return ResponseUtil.failed();
        }
        return ResponseUtil.success();
    }

    /**
     * 催收记录列表
     *
     * @param applyId
     * @return
     */
    @GetMapping("/records")
    public String records(@RequestParam("applyId") String applyId) {
        List<CollectionRecordBean> l = collectionRecordBeanMapper.getRecords(applyId);
        return ResponseUtil.success(l);
    }

    @PostMapping("/records/export")
    public String exportRecord(@Valid @RequestBody CollectionRecordsExportParam params) {
        StringBuilder sb = new StringBuilder();
        sb.append(params.getStart() != null ? " and  date(alert_date)>=" + params.getStart() : "");
        sb.append(params.getEnd() != null ? " and  date(alert_date)<=" + params.getStart() : "");
        sb.append(params.getApplyId() != null ? " and apply_id=" + params.getApplyId() : "");

        log.info("export param=" + GsonUtil.toJson(params) + ", conditions=" + sb.toString());
        List<CollectionRecords> records = collectionRecordBeanMapper.exportRecords(sb.toString());
        return ResponseUtil.success(records);
    }

    /**
     * 逾期派单-列表
     *
     * @param params
     * @return
     */
    @PostMapping("/allocate/getList")
    public String allocateGetList(@Valid @RequestBody CollectionAllocateGetListParams params) {
        StringBuilder sb = new StringBuilder();
        sb.append(null != params.getProductId() ? " and c.id=" + params.getProductId() : "");
        sb.append(null != params.getApplyId() ? " and b.id=" + params.getApplyId() : "");
        sb.append(null != params.getOverdueDays() ? " and h.overdue_days_max=" + params.getOverdueDays() : "");
        sb.append(!Strings.isNullOrEmpty(params.getName()) ? " and e.name=\"" + params.getName() + "\"" : "");
        sb.append(!Strings.isNullOrEmpty(params.getMobile()) ? " and g.mobile=\"" + params.getMobile() + "\"" : "");
        sb.append(!Strings.isNullOrEmpty(params.getCidNo()) ? " and e.cid_no=\"" + params.getCidNo() + "\"" : "");
        Integer offset = (params.getPage() - 1) * params.getPageSize();
        Integer rows = params.getPageSize();
        List<CollectionAllocateGetListBean> l = crazyJoinMapper.collectionAllocateGetList(sb.toString(), offset, rows);
        Map m = Maps.newHashMap();
        m.put("total",crazyJoinMapper.collectionAllocateGetListForPaging(sb.toString()));
        m.put("list",l);
        return ResponseUtil.success(m);
    }

    /**
     * 逾期派单-派单按钮
     *
     * @param params
     * @return
     */
    @PostMapping("/allocate/action")
    public String allocateAction(@Valid @RequestBody CollectionAllocateActionParams params) {
        //检查是不是逾期的任务
        ApplyOperationTaskBean bean = applyOperationTaskMapper.selectById(params.getId());
        if (bean == null || !bean.getTaskType().equals(3)) {
            return ResponseUtil.failed("任务不存在或任务状态不允许该操作");
        }
        if (bean.getHasOwner().equals(1)) {
            return ResponseUtil.failed("该任务已被认领，操作失败");
        }
        bean.setOperatorId(params.getOperatorId());
        bean.setDistributorId(params.getDistributorId());
        bean.setHasOwner(1);
        bean.setExpireTime(new Date());
        if (applyOperationTaskMapper.updateById(bean) <= 0) {
            return ResponseUtil.failed();
        }
        CollectionAllocateRecordBean allocateRecordBean = new CollectionAllocateRecordBean();
        allocateRecordBean.setActionTime(bean.getExpireTime());
        allocateRecordBean.setApplyId(params.getApplyId());
        allocateRecordBean.setDistributorId(params.getDistributorId());
        allocateRecordBean.setCollectorId(params.getOperatorId());
        allocateRecordBean.setCreateTime(bean.getExpireTime());
        allocateRecordBean.setCollectorName(userBeanMapper.selectById(params.getOperatorId()).getName());
        allocateRecordBean.setDistributorName(userBeanMapper.selectById(params.getDistributorId()).getName());
        if (collectionAllocateRecordBeanMapper.insert(allocateRecordBean) <= 0) {
            return ResponseUtil.failed();
        }
        return ResponseUtil.success();
    }

    /**
     * 我的催收-列表
     * <p>
     * 提醒时间和催收人不知道有什么作用 如果这俩没作用的话 应该和逾期列表的字段一致
     *
     * @param params
     * @return
     */
    @PostMapping("/mine/getList")
    public String mine(@Valid @RequestBody CollectionMineGetListParams params) {
        StringBuilder sb = new StringBuilder();
        //和逾期列表一样 把where条件限制一下审批人就可以了
        sb.append(" and a.operator_id=\"" + params.getOperatorId() + "\"");
        sb.append(null != params.getLastCollectionDateStart() ? " and a.update_time>=\"" + params.getLastCollectionDateStart() + "\"" : "");
        sb.append(null != params.getLastCollectionDateEnd() ? " and a.update_time<=\"" + params.getLastCollectionDateEnd() + "\"" : "");
        sb.append(null != params.getShouldRepayDateStart() ? " and f.repay_end_date>=\"" + params.getShouldRepayDateStart() + "\"" : "");
        sb.append(null != params.getShouldRepayDateEnd() ? " and f.repay_end_date<=\"" + params.getShouldRepayDateEnd() + "\"" : "");
        sb.append(null != params.getApplyId() ? " and b.id=" + params.getApplyId() : "");
        sb.append(!Strings.isNullOrEmpty(params.getName()) ? " and e.name=\"" + params.getName() + "\"" : "");
        sb.append(!Strings.isNullOrEmpty(params.getMobile()) ? " and g.mobile=\"" + params.getMobile() + "\"" : "");
        sb.append(!Strings.isNullOrEmpty(params.getCidNo()) ? " and e.cid_no=\"" + params.getCidNo() + "\"" : "");
        Integer offset = (params.getPage() - 1) * params.getPageSize();
        Integer rows = params.getPageSize();
        List<CollectionAllocateGetListBean> l = crazyJoinMapper.collectionAllocateGetList(sb.toString(), offset, rows);
        Map m = Maps.newHashMap();
        m.put("total",crazyJoinMapper.collectionAllocateGetListForPaging(sb.toString()));
        m.put("list",l);
        return ResponseUtil.success(m);
    }

    /**
     * 催收档案-列表
     *
     * @param params
     * @return
     */
    @PostMapping("/archives/getList")
    public String archivesGetList(@Valid @RequestBody CollectionArchivesGetListParams params) {
        StringBuilder sb = new StringBuilder();
        sb.append(null != params.getApplyId() ? " and b.id=" + params.getApplyId() : "");
        sb.append(null != params.getShouldRepayDateStart() ? " and f.repay_end_date>=\"" + params.getShouldRepayDateStart() + "\"" : "");
        sb.append(null != params.getShouldRepayDateEnd() ? " and f.repay_end_date<=\"" + params.getShouldRepayDateEnd() + "\"" : "");
        sb.append(null != params.getOperatorId() ? " and i.id=" + params.getOperatorId() : "");
        sb.append(null != params.getOperatorStatus() ? " and i.is_valid=" + params.getOperatorStatus() : "");
        sb.append(!Strings.isNullOrEmpty(params.getName()) ? " and e.name=\"" + params.getName() + "\"" : "");
        sb.append(!Strings.isNullOrEmpty(params.getMobile()) ? " and g.mobile=\"" + params.getMobile() + "\"" : "");
        sb.append(!Strings.isNullOrEmpty(params.getCidNo()) ? " and e.cid_no=\"" + params.getCidNo() + "\"" : "");
        sb.append(!Strings.isNullOrEmpty(params.getAppName()) ? " and j.APP_NAME=\"" + params.getAppName() + "\"" : "");
        Integer offset = (params.getPage() - 1) * params.getPageSize();
        Integer rows = params.getPageSize();
        List<CollectionArchivesGetListBean> l = crazyJoinMapper.collectionArchivesGetList(sb.toString(), offset, rows);
        Map m = Maps.newHashMap();
        m.put("total",crazyJoinMapper.collectionArchivesGetListForPaging(sb.toString()));
        m.put("list",l);
        return ResponseUtil.success(m);
    }

    /**
     * 催收档案-重新指派按钮
     *
     * @param params
     * @return
     */
    @PostMapping("/archives/reAllocate")
    public String reAllocate(@Valid @RequestBody CollectionArchivesReAllocateParams params) {
        ApplyOperationTaskBean bean = applyOperationTaskMapper.selectById(params.getId());
        bean.setOperatorId(params.getOperatorId());
        bean.setDistributorId(params.getDistributorId());
        bean.setHasOwner(1);
        if (applyOperationTaskMapper.updateById(bean) <= 0) {
            return ResponseUtil.failed();
        }
        CollectionAllocateRecordBean allocateRecordBean = new CollectionAllocateRecordBean();
        allocateRecordBean.setActionTime(new Date());
        allocateRecordBean.setApplyId(params.getApplyId());
        allocateRecordBean.setDistributorId(params.getDistributorId());
        allocateRecordBean.setCollectorId(params.getOperatorId());
        allocateRecordBean.setCollectorName(userBeanMapper.selectById(params.getOperatorId()).getName());
        allocateRecordBean.setDistributorName(userBeanMapper.selectById(params.getDistributorId()).getName());
        if (collectionAllocateRecordBeanMapper.insert(allocateRecordBean) <= 0) {
            return ResponseUtil.failed();
        }
        return ResponseUtil.success();
    }

}
