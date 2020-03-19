package com.sup.cms.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.sup.cms.bean.po.DetailsRepayBean;
import com.sup.cms.bean.vo.*;
import com.sup.cms.mapper.CrazyJoinMapper;
import com.sup.cms.mapper.TbManualRepayMapper;
import com.sup.common.bean.TbApplyInfoBean;
import com.sup.common.bean.TbManualRepayBean;
import com.sup.common.param.ManualRepayParam;
import com.sup.common.service.CoreService;
import com.sup.common.util.DateUtil;
import com.sup.common.util.GsonUtil;
import com.sup.common.util.ResponseUtil;
import com.sup.common.util.Result;
import lombok.extern.log4j.Log4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Date;
import java.util.List;

/**
 *
 * @Author: kouichi
 * @Date: 2019/10/13 17:41
 */
@Log4j
@RestController
@RequestMapping("/repay")
public class RepayController {

    @Autowired
    private CoreService coreService;

    @Autowired
    private TbManualRepayMapper manualRepayMapper;

    @Autowired
    private CrazyJoinMapper crazyJoinMapper;

    /**
     * 确认用户线下还款(未通过系统提交还款凭证)
     * @param params
     * @return
     */
    @PostMapping("/offline/confirm")
    public String confirmOfflineRepay(@RequestBody @Valid RepayOfflineConfirmParams params) {
        log.info("confirmOfflineRepay param=" + GsonUtil.toJson(params));

        if (params.getRepayAmount() <= 0) {
            return ResponseUtil.failed("Invalid repay amount(<=0)!");
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


    /**
     * 确认用户线下还款、线上提交的还款信息
     * @param params
     * @return
     */
    @PostMapping("/online/confirm")
    public String repay(@Valid @RequestBody ApplyRepayParams params) {
        Result<TbApplyInfoBean> result = coreService.getApplyInfo(params.getApplyId());
        if (!result.isSucc()) {
            return ResponseUtil.failed(result.getMessage());
        }
        TbApplyInfoBean applyInfoBean = result.getData();
        if (applyInfoBean == null) {
            return ResponseUtil.failed("Invalid applyId!");
        }
        boolean confirm = params.getConfirm().equals(1);
        List<Integer> ids = params.getRepayInfoIds();
        List<String> tradeNos = params.getTradeNos();

        if (confirm && (ids == null || tradeNos == null || ids.size() != tradeNos.size() || params.getRepayAmount() <= 0)) {
            log.error("Invalid params:" + GsonUtil.toJson(params));
            return ResponseUtil.failed("Invalid params.");
        }

        if (confirm) {  // 验证流水号是否重复
            QueryWrapper<TbManualRepayBean> wrapper = new QueryWrapper<>();
            wrapper.eq("status", 1);      // 还款成功
            wrapper.in("trade_no", tradeNos);   // 待检测流水号
            List<TbManualRepayBean> beans = manualRepayMapper.selectList(wrapper);
            if (beans != null && beans.size() > 0) {
                log.error("Duplicated trade no, params = " + GsonUtil.toJson(params));
                return ResponseUtil.failed("Duplicated trade no.");
            }
        }
        // 更新还款资料状态
        Date now = new Date();
        for (int i = 0; i < ids.size(); ++i) {
            Integer id = ids.get(i);
            QueryWrapper<TbManualRepayBean> wrapper = new QueryWrapper<>();
            wrapper.eq("apply_id", params.getApplyId());
            wrapper.eq("user_id", params.getUserId());
            wrapper.eq("id", id);
            wrapper.eq("status", 0);    // 还款资料待处理
            TbManualRepayBean bean = manualRepayMapper.selectOne(wrapper);
            if (bean == null) {
                continue;
            }
            if (confirm) {  // 还款确认
                bean.setStatus(1);
                bean.setTrade_no(tradeNos.get(i));
            } else {    // 还款失败
                bean.setStatus(2);
            }
            bean.setUpdate_time(now);
            if (manualRepayMapper.updateById(bean) <= 0) {
                log.error("Failed to update bean=" + GsonUtil.toJson(bean));
            }
        }

        if (!confirm) { // 还款失败无需更新还款计划等
            return ResponseUtil.success();
        }

        // 更新还款计划、还款状态等
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
     * 获取手动还款凭证
     * @param params
     * @return
     */
    @PostMapping("/material/get")
    public String getRepayMaterial(@Valid @RequestBody RepayMaterialParams params) {
        QueryWrapper<TbManualRepayBean> wrapper = new QueryWrapper<>();
        wrapper.eq("apply_id", params.getApplyId());
        wrapper.eq("user_id", params.getUserId());
        wrapper.eq("status", 0);    // 还款资料待处理
        List<TbManualRepayBean> repayBeans = manualRepayMapper.selectList(wrapper);
        if (repayBeans == null || repayBeans.size() == 0) {
            log.error("No material found for param:" + GsonUtil.toJson(params));
            return ResponseUtil.failed("No material found!");
        }
        return ResponseUtil.success(repayBeans);
    }

    @GetMapping("/repayPlan/get")
    public String getRepayPlan(@RequestParam("applyId") String applyId) {
        DetailsRepayBean bean = crazyJoinMapper.detailsRepay(applyId);
        log.info(">>> applyId = " + applyId + ", repay bean:" + GsonUtil.toJson(bean));
        if (bean != null) {
            bean.setList(crazyJoinMapper.detailsRepayList(applyId));
        }
        return ResponseUtil.success(bean);
    }
}
