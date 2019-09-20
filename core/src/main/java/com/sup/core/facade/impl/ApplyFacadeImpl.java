package com.sup.core.facade.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.sup.common.param.ApplyInfoParam;
import com.sup.common.bean.TbApplyInfoBean;
import com.sup.common.bean.TbApplyMaterialInfoBean;
import com.sup.common.bean.TbProductInfoBean;
import com.sup.common.loan.ApplyStatusEnum;
import com.sup.common.util.DateUtil;
import com.sup.common.util.GsonUtil;
import com.sup.common.util.Result;
import com.sup.core.bean.RiskDecisionResultBean;
import com.sup.core.facade.ApplyFacade;
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
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.List;

/**
 * Project:uniloan
 * Class:  ApplyFacadeImpl
 * <p>
 * Author: guanfeng
 * Create: 2019-09-11
 */

@Log4j
@RestController
public class ApplyFacadeImpl implements ApplyFacade {

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


    @Override
    public Result addApplyInfo(ApplyInfoParam applyInfoParam) {
        // 检测是否重复提交
        QueryWrapper<TbApplyInfoBean> wrapper = new QueryWrapper<>();
        wrapper.eq("user_id", applyInfoParam.getUser_id());
        wrapper.eq("product_id", applyInfoParam.getProduct_id());
        wrapper.eq("channel_id", applyInfoParam.getChannel_id());
        wrapper.eq("app_id", applyInfoParam.getApp_id());
        wrapper.orderByDesc("create_time");
        TbApplyInfoBean oldApply = applyInfoMapper.selectOne(wrapper);
        if (oldApply != null) {
            if (oldApply.getStatus() == ApplyStatusEnum.APPLY_INIT.getCode()) {
                return Result.fail("Duplicated Apply!");
            }
        }

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

    @Override
    public Result updateApplyInfo(TbApplyInfoBean bean) {
        return applyService.updateApplyInfo(bean);
    }
}
