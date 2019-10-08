package com.sup.cms.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.google.common.collect.Lists;
import com.sup.cms.bean.po.*;
import com.sup.cms.bean.vo.*;
import com.sup.cms.mapper.ApplyOperationTaskMapper;
import com.sup.cms.mapper.AuthUserBeanMapper;
import com.sup.cms.mapper.CollectionAllocateRecordBeanMapper;
import com.sup.cms.mapper.CollectionRecordBeanMapper;
import com.sup.cms.util.GsonUtil;
import com.sup.cms.util.ResponseUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Date;
import java.util.List;

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
public class CollectionController {

    @Autowired
    private CollectionRecordBeanMapper collectionRecordBeanMapper;
    @Autowired
    private CollectionAllocateRecordBeanMapper collectionAllocateRecordBeanMapper;
    @Autowired
    private ApplyOperationTaskMapper applyOperationTaskMapper;
    @Autowired
    private AuthUserBeanMapper userBeanMapper;

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
        ApplyOperationTaskBean task = applyOperationTaskMapper.selectById(params.getApplyId());
        task.setComment(params.getStatus());
        if (applyOperationTaskMapper.updateById(task) <= 0) {
            return ResponseUtil.failed();
        }
        CollectionRecordBean bean = GsonUtil.beanCopy(params, CollectionRecordBean.class);
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
        QueryWrapper<CollectionRecordBean> qw = new QueryWrapper<>();
        qw.eq("apply_id", applyId);
        List<CollectionRecordBean> l = collectionRecordBeanMapper.selectList(qw);
        return ResponseUtil.success(l);
    }

    /**
     * 逾期派单-列表
     *
     * @param params
     * @return
     */
    @PostMapping("/allocate/getList")
    public String allocateGetList(@Valid @RequestBody CollectionAllocateGetListParams params) {
        //todo
        // select b.id as applyId,a.update_time as lastAllocateDate,e.name,'' as mobile,a.comment as status,c.name as productName,b.period+'日/期, 共1期' as period,'不知道业务类型在哪' as type,'不知道逾期级别在哪' as overdueLevel,'不知道逾期填数在哪'b.loan_time as payDate from tb_operation_task a left join tb_apply_info b on a.apply_id=b.id left join tb_product_info c on b.product_id=c.id left join tb_apply_material_info d on b.id=d.apply_id left join tb_user_citizen_identity_card_info e on d.info_id=e.info_id left join tb_repay_plan f on b.id=f.apply_id where a.task_type=3 and d.info_type=0 and f.seq_no=1
        List<CollectionAllocateGetListBean> l = Lists.newArrayList();
        return "";
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
        bean.setUpdateTime(new Date());
        if (applyOperationTaskMapper.updateById(bean) <= 0) {
            return ResponseUtil.failed();
        }
        CollectionAllocateRecordBean allocateRecordBean = new CollectionAllocateRecordBean();
        allocateRecordBean.setActionTime(bean.getUpdateTime());
        allocateRecordBean.setApplyId(params.getApplyId());
        allocateRecordBean.setDistributorId(params.getDistributorId());
        allocateRecordBean.setCollectorId(params.getOperatorId());
        allocateRecordBean.setCreateTime(bean.getUpdateTime());
        allocateRecordBean.setCollectorName(userBeanMapper.selectById(params.getOperatorId()).getName());
        allocateRecordBean.setDistributorName(userBeanMapper.selectById(params.getDistributorId()).getName());
        if (collectionAllocateRecordBeanMapper.insert(allocateRecordBean) <= 0) {
            return ResponseUtil.failed();
        }
        return ResponseUtil.success();
    }

    /**
     * 我的催收-列表
     *
     * @param params
     * @return
     */
    @PostMapping("/mine/getList")
    public String mine(@Valid @RequestBody CollectionMineGetListParams params) {
        //todo 提醒时间和催收人不知道有什么作用 如果这俩没作用的话 应该和逾期列表的字段一致
        // 和逾期列表一样 把where条件限制一下审批人就可以了
        return "";
    }

    /**
     * 催收档案-列表
     *
     * @param params
     * @return
     */
    @PostMapping("/archives/getList")
    public String archivesGetList(@Valid @RequestBody CollectionArchivesGetListParams params) {
        //todo 不知道字段在哪里 稍后做
        List<CollectionArchivesGetListBean> l = Lists.newArrayList();
        return "";
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
