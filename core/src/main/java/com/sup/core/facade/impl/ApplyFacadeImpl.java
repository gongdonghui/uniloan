package com.sup.core.facade.impl;

import com.sup.common.param.ApplyInfoParam;
import com.sup.common.bean.TbApplyInfoBean;
import com.sup.common.bean.TbApplyMaterialInfoBean;
import com.sup.common.bean.TbProductInfoBean;
import com.sup.common.loan.ApplyStatusEnum;
import com.sup.common.util.DateUtil;
import com.sup.common.util.Result;
import com.sup.core.facade.ApplyFacade;
import com.sup.core.mapper.ApplyMaterialInfoMapper;
import com.sup.core.mapper.ProductInfoMapper;
import com.sup.core.service.ApplyService;
import lombok.extern.log4j.Log4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;

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
    private ApplyMaterialInfoMapper applyMaterialInfoMapper;

    @Autowired
    private ProductInfoMapper productInfoMapper;

    @Value("apply.expire-days")
    private int APPLY_EXPIRE_DAYS;

    @Override
    public Result addApplyInfo(ApplyInfoParam applyInfoParam) {
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
