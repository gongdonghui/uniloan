package com.sup.core.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.sup.common.bean.TbApplyInfoBean;
import com.sup.common.bean.TbApplyMaterialInfoBean;
import com.sup.common.bean.TbProductInfoBean;
import com.sup.common.loan.ApplyStatusEnum;
import com.sup.common.param.ApplyInfoParam;
import com.sup.common.util.DateUtil;
import com.sup.common.util.GsonUtil;
import com.sup.common.util.Result;
import com.sup.core.bean.RiskDecisionResultBean;
import com.sup.core.mapper.ApplyInfoMapper;
import com.sup.core.mapper.ApplyMaterialInfoMapper;
import com.sup.core.mapper.ProductInfoMapper;
import com.sup.core.param.AutoDecisionParam;
import com.sup.core.service.ApplyService;
import com.sup.core.service.impl.DecisionEngineImpl;
import com.sup.core.status.DecisionEngineStatusEnum;
import lombok.extern.log4j.Log4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.List;

/**
 * Project:uniloan
 * Class:  ApplyFacade
 * <p>
 * Author: guanfeng
 * Create: 2019-09-10
 */

@Log4j
@RestController
@RequestMapping(value = "/apply")
public class ApplyController {

    @Autowired
    private ApplyService applyService;

    @Autowired
    private DecisionEngineImpl decisionEngine;

    @Autowired
    private ApplyMaterialInfoMapper applyMaterialInfoMapper;

    @Autowired
    private ProductInfoMapper productInfoMapper;

    @Autowired
    private ApplyInfoMapper applyInfoMapper;


    @Value("#{new Integer('${apply.expire-days}')}")
    private Integer APPLY_EXPIRE_DAYS;


    @Scheduled(cron = "0 */10 * * * ?")
    public void checkApplyInfo() {
        // 1. 获取所有新提交的进件
        List<TbApplyInfoBean> applyInfoBeans = applyInfoMapper.selectList(
                new QueryWrapper<TbApplyInfoBean>().eq("status", ApplyStatusEnum.APPLY_INIT.getCode())
        );

        if (applyInfoBeans == null || applyInfoBeans.size() == 0) {
            return;
        }
        AutoDecisionParam param = new AutoDecisionParam();
        for (TbApplyInfoBean bean : applyInfoBeans) {
            param.setApplyId(String.valueOf(bean.getId()));
            param.setProductId(String.valueOf(bean.getProduct_id()));
            param.setUserId(String.valueOf(bean.getUser_id()));
            // 2. 自动审查
            RiskDecisionResultBean result = decisionEngine.applyRules(param);
            if (result == null) {
                // Exception??
                log.error("DecisionEngine return null for param = " + GsonUtil.toJson(param));
                bean.setStatus(ApplyStatusEnum.APPLY_AUTO_DENY.getCode());
            } else if (result.getRet() == DecisionEngineStatusEnum.APPLY_DE_AUTO_PASS.getCode()) {
                bean.setStatus(ApplyStatusEnum.APPLY_AUTO_PASS.getCode());
            } else {
                bean.setStatus(ApplyStatusEnum.APPLY_AUTO_DENY.getCode());
                bean.setDeny_code(result.getRefuse_code());
            }
            // 3. 更新进件状态
            applyService.updateApplyInfo(bean);
        }
    }

    // add apply
    @ResponseBody
    @RequestMapping(value = "add", produces = "application/json;charset=UTF-8")
    Result addApplyInfo(@RequestBody ApplyInfoParam applyInfoParam) {
        TbProductInfoBean product = productInfoMapper.selectById(applyInfoParam.getProduct_id());
        if (product == null) {
            log.error("invalid product id = " + applyInfoParam.getProduct_id());
            return Result.fail("Invalid product id!");
        }
        Date now = new Date();
        Date expireTime = DateUtil.getDate(now, APPLY_EXPIRE_DAYS);

        // add apply info
        TbApplyInfoBean applyInfoBean = new TbApplyInfoBean();
        applyInfoBean.setUser_id(applyInfoParam.getUser_id());
        applyInfoBean.setProduct_id(applyInfoParam.getProduct_id());
        applyInfoBean.setChannel_id(applyInfoParam.getChannel_id());
        applyInfoBean.setApp_id(applyInfoParam.getApp_id());
        applyInfoBean.setQuota(applyInfoParam.getApply_quota());
        applyInfoBean.setApply_quota(applyInfoParam.getApply_quota());
        applyInfoBean.setPeriod(applyInfoParam.getPeriod());
        applyInfoBean.setRate(product.getRate());
        applyInfoBean.setFee(product.getFee());
        applyInfoBean.setFee_type(product.getFee_type());
        applyInfoBean.setStatus(ApplyStatusEnum.APPLY_INIT.getCode());
        applyInfoBean.setCreate_time(now);
        applyInfoBean.setUpdate_time(now);
        applyInfoBean.setExpire_time(expireTime);

        if (!applyService.addApplyInfo(applyInfoBean)) {
            log.error("addApplyInfo failed!");
            return Result.fail("addApplyInfo failed!");
        }

        log.info("new apply info, applyId = " + applyInfoBean.getId());
        // add apply material info
        boolean addSucc = true;
        for (Integer materialType: applyInfoParam.getInfoIdMap().keySet()) {
            String infoId = applyInfoParam.getInfoIdMap().get(materialType);
            TbApplyMaterialInfoBean bean = new TbApplyMaterialInfoBean();
            bean.setApply_id(applyInfoBean.getId());
            bean.setInfo_id(infoId);
            bean.setInfo_type(materialType);
            bean.setCreate_time(now);
            if (applyMaterialInfoMapper.insert(bean) <= 0) {
                addSucc = false;
                log.error("Failed to add applyMaterialInfo, userId = " + applyInfoParam.getUser_id()
                        + ", materialType = " + materialType);
            }
        }

        if (addSucc) {
            return Result.succ();
        }
        return Result.fail("Error in adding apply material!");
    }

    // audit apply
    @ResponseBody
    @RequestMapping(value = "update", produces = "application/json;charset=UTF-8")
    Result updateApplyInfo(@RequestBody TbApplyInfoBean bean) {
        return applyService.updateApplyInfo(bean);
    }

}
