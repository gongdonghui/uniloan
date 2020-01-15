package com.sup.cms.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.google.common.base.Strings;
import com.google.common.collect.Maps;
import com.sup.cms.bean.po.LoanRepayInfoGetListBean;
import com.sup.cms.bean.po.LoanUnRepayInfoGetListBean;
import com.sup.cms.bean.vo.LoanRepayInfoGetListParams;
import com.sup.cms.bean.vo.LoanUnRepayInfoGetListParams;
import com.sup.cms.bean.vo.RepayOfflineConfirmParams;
import com.sup.cms.mapper.CrazyJoinMapper;
import com.sup.cms.mapper.TbApplyInfoMapper;
import com.sup.common.bean.TbApplyInfoBean;
import com.sup.common.loan.ApplyStatusEnum;
import com.sup.common.param.ManualRepayParam;
import com.sup.common.service.CoreService;
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
import java.util.List;
import java.util.Map;

/**
 * 贷款管理下面的子页面
 *
 * @Author: kouichi
 * @Date: 2019/10/13 17:41
 */
@Log4j
@RestController
@RequestMapping("/repay")
public class RepayController {
    @Autowired
    private CrazyJoinMapper crazyJoinMapper;

    @Autowired
    private CoreService coreService;

    @Autowired
    private TbApplyInfoMapper applyInfoMapper;

    /**
     * 确认线下还款
     * @param params
     * @return
     */
    @PostMapping("/offline/confirm")
    public String confirmOfflineRepay(@RequestBody @Valid RepayOfflineConfirmParams params) {
        log.info("confirmOfflineRepay param=" + GsonUtil.toJson(params));

        if (params.getRepayAmount() <= 0) {
            return ResponseUtil.failed("Invalid repay amount(<=0)!");
        }

        // 简单验证还款信息
        QueryWrapper<TbApplyInfoBean> wrapper = new QueryWrapper<>();
        wrapper.eq("id", params.getApplyId());
        wrapper.eq("user_id", params.getUserId());
        wrapper.in("status",
                ApplyStatusEnum.APPLY_LOAN_SUCC.getCode(),
                ApplyStatusEnum.APPLY_REPAY_PART.getCode(),
                ApplyStatusEnum.APPLY_OVERDUE.getCode(),
                ApplyStatusEnum.APPLY_WRITE_OFF.getCode());
        TbApplyInfoBean applyInfoBean = applyInfoMapper.selectOne(wrapper);
        if (applyInfoBean == null) {
            log.error("Invalid repay info! [SQL]=" + wrapper.getSqlSegment());
            return ResponseUtil.failed("Invalid repay info!");
        }

        ManualRepayParam repayParam = new ManualRepayParam();
        repayParam.setAmount(params.getRepayAmount());
        repayParam.setApplyId(String.valueOf(params.getApplyId()));
        repayParam.setOperatorId(String.valueOf(params.getOperatorId()));
        repayParam.setUserId(String.valueOf(params.getUserId()));
        repayParam.setRepayTime(DateUtil.formatDateTime(params.getRepayDate()));
        repayParam.setRepayImg(params.getRepayImg());
        repayParam.setComment(params.getComment());
        if (!coreService.manualRepay(repayParam).isSucc()) {
            return ResponseUtil.failed("Core service for manual repay failed!");
        }

        return ResponseUtil.success();
    }

}
