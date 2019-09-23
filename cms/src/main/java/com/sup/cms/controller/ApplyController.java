package com.sup.cms.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.google.common.base.Strings;
import com.sup.cms.bean.po.ApplyOperationTaskBean;
import com.sup.cms.bean.po.ApprovalGetListBean;
import com.sup.cms.bean.vo.*;
import com.sup.cms.mapper.ApplyOperationTaskMapper;
import com.sup.cms.util.DateUtil;
import com.sup.cms.util.ResponseUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

/**
 * @Author: kouichi
 * @Date: 2019/9/22 11:00
 */
@RequestMapping("/apply")
public class ApplyController {

    @Autowired
    private ApplyOperationTaskMapper applyOperationTaskMapper;

    @PostMapping("/approval/getList")
    public String getList1(@Valid @RequestBody ApplyApprovalGetListParams params) {
        QueryWrapper<ApprovalGetListBean> qw = new QueryWrapper<>();
        StringBuilder sb = new StringBuilder();
        //下面这部分内容为前端查询时的参数
        sb.append(null != params.getApplyId() ? " and a.apply_id=\"" + params.getApplyId() + "\"" : "");
        sb.append(null != params.getStartTime() ? " and b.create_time>=\"" + DateUtil.formatDateTime(params.getStartTime()) + "\"" : "");
        sb.append(null != params.getEndTime() ? " and b.create_time<=\"" + DateUtil.formatDateTime(params.getEndTime()) + "\"" : "");
        sb.append(Strings.isNullOrEmpty(params.getName()) ? " and c.name=\"" + params.getName() + "\"" : "");
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

    @PostMapping("/approval/allocation")
    public String allocation(@Valid @RequestBody ApplyApprovalAllocationParams params) {
        //todo 校验 终审和信审不能是同一个操作人
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

    @GetMapping("/approval/details")
    public String details(@RequestParam("applyId") String applyId) {
        //todo 根据applyId获取全部信息 拼装后返回展示在页面即可
        return ResponseUtil.success();
    }

    @PostMapping("/approval/action")
    public String action(@Valid @RequestBody ApplyApprovalActionParams params) {
        //todo 调用关老师接口改apply状态
        return ResponseUtil.success();
    }

    @GetMapping("/approval/hangUp")
    public String hangUp(@RequestParam("applyId") String applyId) {
        //todo 不知道挂起是要干啥 看看能不能这期先不做
        return ResponseUtil.success();
    }

    @PostMapping("/management/getList")
    public String getList2(@Valid @RequestBody ApplyManagementGetListParams params) {
        //todo 入参已和前端对齐一致
        // 返回参数 待开发
        return ResponseUtil.success();
    }

    @PostMapping("/allocation/history")
    public String history(@Valid @RequestBody ApplyAllocationHistoryParams params) {
        //todo 入参已和前端对齐一致
        // 返回参数 待开发
        return ResponseUtil.success();
    }

    @PostMapping("/allocation/re")
    public String reAllocation(@Valid @RequestBody ApplyApprovalReAllocationParams params) {
        //todo 重新指派的时候 是否只可以 重新指派自己曾经指派过的  还是胡乱的指派
        return ResponseUtil.success();
    }

}