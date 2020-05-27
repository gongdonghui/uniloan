package com.sup.core.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.sup.common.bean.*;
import com.sup.common.bean.paycenter.CreateVCInfo;
import com.sup.common.bean.paycenter.DestroyVCInfo;
import com.sup.common.bean.paycenter.PayInfo;
import com.sup.common.bean.paycenter.RepayInfo;
import com.sup.common.bean.paycenter.vo.CreateVCVO;
import com.sup.common.bean.paycenter.vo.PayVO;
import com.sup.common.bean.paycenter.vo.RepayVO;
import com.sup.common.loan.*;
import com.sup.common.param.*;
import com.sup.common.service.PayCenterService;
import com.sup.common.util.DateUtil;
import com.sup.common.util.FunpayOrderUtil;
import com.sup.common.util.GsonUtil;
import com.sup.common.util.Result;
import com.sup.core.bean.VCInfo;
import com.sup.core.mapper.*;
import com.sup.core.util.MqMessenger;
import com.sup.core.util.ToolUtils;
import lombok.extern.log4j.Log4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.*;

import static java.lang.Integer.max;

/**
 * Project:uniloan
 * Class:  LoanService
 * <p>
 * Author: guanfeng
 * Create: 2019-09-09
 */

@Log4j
@Service
public class LoanService {

    @Value("#{new Integer('${loan.auto-loan-retry-times}')}")
    private Integer AUTO_LOAN_RETRY_TIMES;

    @Autowired
    private RepayPlanMapper repayPlanMapper;
    @Autowired
    private UserBankInfoMapper userBankInfoMapper;
    @Autowired
    private ApplyMaterialInfoMapper applyMaterialInfoMapper;
    @Autowired
    private ApplyInfoMapper applyInfoMapper;
    @Autowired
    private ProductInfoMapper productInfoMapper;
    @Autowired
    private RepayHistoryMapper  repayHistoryMapper;

    @Autowired
    private VirtualCardBankInfoMapper virtualCardBankInfoMapper;
    @Autowired
    private VirtualCardInfoHistoryMapper virtualCardInfoHistoryMapper;
    @Autowired
    private VirtualCardInfoMapper   virtualCardInfoMapper;
    @Autowired
    private UserRegisterInfoMapper userRegisterInfoMapper;

    @Autowired
    private ApplyService applyService;
    @Autowired
    private PayCenterService funpayService;
    @Autowired
    private MqMessenger mqMessenger;
    @Autowired
    private RuleConfigService ruleConfigService;
    @Autowired
    private RepayStatMapper repayStatMapper;

    public Result retryLoan(ApplyRetryLoanParam param) {
        log.info("retryLoan param: " + GsonUtil.toJson(param));
        QueryWrapper<TbApplyInfoBean> wrapper = new QueryWrapper<>();
        wrapper.eq("id", param.getApplyId());
        TbApplyInfoBean applyInfoBean = applyInfoMapper.selectOne(wrapper);
        if (applyInfoBean == null) {
            log.error("No apply info found!");
            return Result.fail("No apply info!");
        }
        return autoLoan(applyInfoBean, param.getOperatorId());
    }

    public synchronized Result<TbApplyInfoBean> autoLoan(TbApplyInfoBean applyInfoBean, Integer operatorId) {
        String userId = String.valueOf(applyInfoBean.getUser_id());
        String applyId = String.valueOf(applyInfoBean.getId());

        ApplyStatusEnum status = ApplyStatusEnum.getStatusByCode(applyInfoBean.getStatus());
        if (status == null || (status != ApplyStatusEnum.APPLY_FINAL_PASS && status != ApplyStatusEnum.APPLY_AUTO_LOAN_FAILED)) {
            log.error("Invalid status for autoLoan, bean=" + GsonUtil.toJson(applyInfoBean) + ", operator_id=" + operatorId);
            return Result.fail("Invalid status for auto loan!");
        }
        if (applyInfoBean.getInhand_quota() <= 0) {
            log.error("Invalid in-hand quota = " + applyInfoBean.getInhand_quota());
            return Result.fail("Invalid in-hand quota!");
        }

        // 2. get user bank info
        QueryWrapper<TbApplyMaterialInfoBean> materialWrapper = new QueryWrapper<>();
        TbApplyMaterialInfoBean applyMaterialInfoBean = applyMaterialInfoMapper.selectOne(materialWrapper
                .eq("apply_id", applyId)
                .eq("info_type", ApplyMaterialTypeEnum.APPLY_MATERIAL_BANK.getCode()));

        if (applyMaterialInfoBean == null) {
            log.error("No apply material(bank info) found for applyId=" + applyId);
            return Result.fail("No apply material found!");
        }
        String infoId = applyMaterialInfoBean.getInfo_id();
        QueryWrapper<TbUserBankAccountInfoBean> bankWrapper = new QueryWrapper<>();

        // make sure there is only one bank card for current apply
        TbUserBankAccountInfoBean bankInfoBean = userBankInfoMapper.selectOne(
                bankWrapper.eq("info_id", infoId).eq("user_id", userId).orderByDesc("create_time"));
        if (bankInfoBean == null) {
            log.error("No bank info for user(" + userId + "), info_id=" + infoId);
            return Result.fail("No bank info found!");
        }

        log.info("autoLoan, bank info:" + GsonUtil.toJson(bankInfoBean));
        log.info("autoLoan, apply info(before loan):" + GsonUtil.toJson(applyInfoBean));

        // 3. loan using funpay(need thread safe)
        boolean loanSucc = false;
        {
            String token = ToolUtils.getToken();    // orderNo
            PayInfo payInfo = new PayInfo();
            payInfo.setUserId(userId);
            payInfo.setOrderNo(token);
            payInfo.setAmount(applyInfoBean.getInhand_quota());
            payInfo.setBankNo(String.valueOf(bankInfoBean.getBank()));
            payInfo.setAccountNo(bankInfoBean.getAccount_id());
            payInfo.setAccountType(bankInfoBean.getAccount_type());
            payInfo.setAccountName(bankInfoBean.getName());
            payInfo.setRemark(applyId);

            applyInfoBean.setOrder_number(token);

            try {
                Result<PayVO> result = funpayService.pay(payInfo);
                if (result != null && result.isSucc()) {
                    loanSucc = true;
                    applyInfoBean.setTrade_number(result.getData().getTradeNo());
                }
            }catch (Exception e) {
                e.printStackTrace();
                log.error(e.getMessage());
            }
        }

        // if loan succeeded, update apply info status
        if (loanSucc) {
            applyInfoBean.setStatus(ApplyStatusEnum.APPLY_AUTO_LOANING.getCode());
        } else {
            applyInfoBean.setStatus(ApplyStatusEnum.APPLY_AUTO_LOAN_FAILED.getCode());
        }
        applyInfoBean.setUpdate_time(new Date());
        applyInfoBean.setOperator_id(operatorId);

        log.info("autoLoan, apply info(after loan):" + GsonUtil.toJson(applyInfoBean));
        return applyService.updateApplyInfo(applyInfoBean);
    }

//    private TbRepayHistoryBean getOrNewRepayHistoryBean(Integer userId, Integer applyId, Integer repayAmount) {
//        TbRepayPlanBean repayPlanBean = repayPlanMapper.selectOne(
//                new QueryWrapper<TbRepayPlanBean>().eq("apply_id", applyId));
//        if (repayPlanBean == null) {
//            log.error("Invalid applyId = " + applyId);
//            return null;
//        }
//        if (repayPlanBean.getRepay_status() == RepayPlanStatusEnum.PLAN_PAID_ALL.getCode()) {
//            log.error("Nothing to repay for applyId=" + applyId);
//            return null;
//        }
//        return getOrNewRepayHistoryBean(userId, applyId, repayAmount, repayPlanBean.getId());
//    }

    private TbRepayHistoryBean getOrNewRepayHistoryBean(Integer userId, Integer applyId, Integer repayAmount, Integer repayPlanId) {
        Date now = new Date();
        // 检查还款记录表，还款处理中的记录
        QueryWrapper<TbRepayHistoryBean> wrapper = new QueryWrapper<>();
        wrapper.eq("apply_id", applyId);
        wrapper.eq("repay_plan_id", repayPlanId);
        wrapper.eq("repay_status", RepayHistoryStatusEnum.REPAY_STATUS_PROCESSING.getCode());
        // wrapper.ge("expire_time", now);
        wrapper.orderByDesc("id").last("limit 1");   // create_time

        TbRepayHistoryBean repayHistoryBean = repayHistoryMapper.selectOne(wrapper);
        if (repayHistoryBean != null) {
            return repayHistoryBean;
        }

        // 创建还款记录
        repayHistoryBean = new TbRepayHistoryBean();
        repayHistoryBean.setUser_id(userId);
        repayHistoryBean.setApply_id(applyId);
        repayHistoryBean.setRepay_plan_id(repayPlanId);
        repayHistoryBean.setRepay_amount(Long.valueOf(repayAmount));
        repayHistoryBean.setRepay_status(RepayHistoryStatusEnum.REPAY_STATUS_PROCESSING.getCode());
        repayHistoryBean.setCreate_time(now);
        repayHistoryBean.setUpdate_time(now);
        if (repayHistoryMapper.insert(repayHistoryBean) <= 0) {
            log.error("Fail to add repay detail for applyId= " + applyId);
            return null;
        }
        return repayHistoryBean;
    }


    public Result getRepayInfo(@RequestBody RepayInfo repayInfo) {
        QueryWrapper<TbRepayPlanBean> wrapper = new QueryWrapper<>();
        TbRepayPlanBean repayPlanBean = repayPlanMapper.selectOne(
                wrapper.eq("apply_id", Integer.valueOf(repayInfo.getApplyId())));
        if (repayPlanBean == null) {
            log.error("Invalid applyId = " + repayInfo.getApplyId());
            return Result.fail("Invalid applyId!");
        }
        if (repayPlanBean.getRepay_status() == RepayPlanStatusEnum.PLAN_PAID_ALL.getCode()) {
            return Result.fail("Nothing to repay.");
        }

        TbRepayHistoryBean repayHistoryBean = getOrNewRepayHistoryBean(Integer.valueOf(repayInfo.getUserId()),
                Integer.valueOf(repayInfo.getApplyId()), repayInfo.getAmount(), repayPlanBean.getId());
        if (repayHistoryBean == null) {
            return Result.fail("");
        }
        Date expireDate = repayHistoryBean.getExpire_time();
        boolean isExpire = (expireDate != null) && (DateUtil.compareDate(new Date(), expireDate) > 0);
        log.info("repayHistoryBean=" + GsonUtil.toJson(repayHistoryBean) + ", isExpire=" + isExpire);
        if (!isExpire && repayHistoryBean.getRepay_code() != null) {
            RepayVO r = new RepayVO();
            r.setCode(repayHistoryBean.getRepay_code());
            r.setShopLink(repayHistoryBean.getRepay_location());
            r.setExpireDate(DateUtil.formatDateTime(repayHistoryBean.getExpire_time()));
            r.setTradeNo(repayHistoryBean.getTrade_number());
            return Result.succ(r);
        }

        repayInfo.setOrderNo(String.valueOf(repayHistoryBean.getId()));
        try {
            Result<RepayVO> result = funpayService.repay(repayInfo);
            if (!result.isSucc()) {
                log.error("Failed to get repay info for applyId = " + repayInfo.getApplyId() + ", msg = " + result.getMessage());
                return Result.fail("Failed to get repay info!");
            }
            RepayVO r = result.getData();
            log.info(">>>> repay return: " + GsonUtil.toJson(r));
            repayHistoryBean.setRepay_code(r.getCode());
            repayHistoryBean.setRepay_location(r.getShopLink());
            repayHistoryBean.setTrade_number(r.getTradeNo());
            repayHistoryBean.setExpire_time(DateUtil.parseDateTime(r.getExpireDate()));
            repayHistoryBean.setRepay_status(RepayHistoryStatusEnum.REPAY_STATUS_PROCESSING.getCode());

            if (repayHistoryMapper.updateById(repayHistoryBean) <= 0) {
                log.error("Failed to update repayHistory bean = " + GsonUtil.toJson(repayHistoryBean));
                // return Result.fail("");
            }
            repayPlanBean.setRepay_code(r.getCode());
            repayPlanBean.setRepay_location(r.getShopLink());
            repayPlanBean.setTrade_number(r.getTradeNo());
            repayPlanBean.setExpire_time(DateUtil.parseDateTime(r.getExpireDate()));
            repayPlanBean.setRepay_status(RepayPlanStatusEnum.PLAN_PAID_PROCESSING.getCode());
            repayPlanBean.setUpdate_time(new Date());
            if (repayPlanMapper.updateById(repayPlanBean) <= 0) {
                log.error("Failed to update repayPlan bean = " + GsonUtil.toJson(repayPlanBean));
            }
            return Result.succ(r);

        } catch (Exception e) {
            e.printStackTrace();
            log.error("Paycenter error: " + e.getMessage());
        }
        return Result.fail("System error!");
    }

    public synchronized boolean addRepayPlan(TbApplyInfoBean applyInfoBean) {

        ApplyStatusEnum status = ApplyStatusEnum.getStatusByCode(applyInfoBean.getStatus());
        if (status != ApplyStatusEnum.APPLY_LOAN_SUCC) {
            // repay plan must be added after loan
            log.error("addRepayPlan: invalid status = " + GsonUtil.toJson(status));
            return false;
        }
        // generate repay plan if not exist(need thread safe)
        TbRepayPlanBean repayPlanBean = repayPlanMapper.getByApplyId(applyInfoBean.getId());
        if (repayPlanBean != null) {
            log.error("RepayPlan already exists for applyId = " + applyInfoBean.getId());
            return false;
        }

        List<TbRepayPlanBean> plans = genRepayPlan(applyInfoBean);
        if (plans == null || plans.size() == 0) {
            return false;
        }
        boolean ret = true;
        for (TbRepayPlanBean bean : plans) {
            if (repayPlanMapper.insert(bean) <= 0) {
                ret = false;
                log.error("Failed to add repay plan: " + GsonUtil.toJson(bean));
            }
        }
        return ret;
    }



    public Result<TbApplyInfoBean> updateRepayPlan(TbRepayPlanBean bean) {
        if (bean == null) {
            return Result.fail("TbRepayPlanBean is null!");
        }
        log.info("updateRepayPlan: bean = " + GsonUtil.toJson(bean));

        Date now = new Date();
        Integer applyId = bean.getApply_id();

        bean.setUpdate_time(now);
        if (repayPlanMapper.updateById(bean) <= 0) {
            log.error("updateRepayPlan failed! bean = " + GsonUtil.toJson(bean));
            return Result.fail("");
        }

        List<TbRepayPlanBean> planBeans = repayPlanMapper.selectList(
                new QueryWrapper<TbRepayPlanBean>().eq("apply_id", applyId)
        );
        assert(planBeans != null && planBeans.size() > 0);

        // update repay statis
        TbRepayStatBean statBean = repayStatMapper.selectOne(
                new QueryWrapper<TbRepayStatBean>().select("apply_id", "create_time").eq("apply_id", applyId)
        );
        if (statBean == null) {
            statBean = statRepayPlan(applyId, planBeans);
            statBean.setCreate_time(now);
            statBean.setUpdate_time(now);
            if (repayStatMapper.insert(statBean) <= 0) {
                log.error("Failed to insert! bean = " + GsonUtil.toJson(statBean));
            }
        } else {
            statBean = statRepayPlan(statBean, planBeans);
            statBean.setUpdate_time(now);
            if (repayStatMapper.update(statBean,
                    new QueryWrapper<TbRepayStatBean>().eq("apply_id", applyId)) <= 0) {
                log.error("Failed to update! bean = " + GsonUtil.toJson(statBean));
            }
        }

        // update ApplyInfo
        TbApplyInfoBean applyInfoBean = applyInfoMapper.selectById(applyId);
        if (applyInfoBean == null) {
            log.error("Invalid applyId! bean = " + GsonUtil.toJson(bean));
            return Result.succ();
        }

        if (applyInfoBean.getStatus() == ApplyStatusEnum.APPLY_REPAY_PART.getCode()) {
            if (!bean.getRepay_status().equals(RepayPlanStatusEnum.PLAN_PAID_ALL.getCode())) {
                // 该计划未还清，无需更新ApplyInfo
                return Result.succ();
            }
        }

        if (statBean.getAct_total() + statBean.getReduction_fee() >= statBean.getNeed_total()) {
            applyInfoBean.setStatus(ApplyStatusEnum.APPLY_REPAY_ALL.getCode());
            mqMessenger.applyStatusChange(applyInfoBean);
        } else {
            if (applyInfoBean.getStatus() != ApplyStatusEnum.APPLY_OVERDUE.getCode()) {
                // 非逾期状态下，更新为未还清；逾期未还清仍为逾期状态
                applyInfoBean.setStatus(ApplyStatusEnum.APPLY_REPAY_PART.getCode());
            }
        }
        applyInfoBean.setUpdate_time(new Date());
        return applyService.updateApplyInfo(applyInfoBean);
    }

    public Result getRepayPlan(String applyId) {
        if (applyId == null) {
            return Result.fail("Invalid applyId!");
        }
        QueryWrapper<TbRepayPlanBean> wrapper = new QueryWrapper<>();
        List<TbRepayPlanBean> plans = repayPlanMapper.getRepayPlan(wrapper.eq("apply_id", applyId));

        return Result.succ(plans);
    }

    public boolean writeOffRepayPlan(Integer applyId) {
        // write off all the repay plan
        if (applyId == null) {
            return false;
        }
        QueryWrapper<TbRepayPlanBean> wrapper = new QueryWrapper<>();
        List<TbRepayPlanBean> plans = repayPlanMapper.getRepayPlan(wrapper.eq("apply_id", applyId));
        int status = RepayPlanStatusEnum.PLAN_PAID_WRITE_OFF.getCode();
        Date now = new Date();
        for (TbRepayPlanBean bean : plans) {
            bean.setRepay_status(status);
            bean.setUpdate_time(now);
            repayPlanMapper.updateById(bean);
        }
        return true;
    }

    public Result<TbApplyInfoBean> payCallBack(@RequestBody FunpayCallBackParam param) {
        // update ApplyInfo status
        TbApplyInfoBean bean = applyInfoMapper.selectOne(
                new QueryWrapper<TbApplyInfoBean>().eq("order_number", param.getOrderNo()));
        if (bean == null) {
            log.error("Invalid param = " + GsonUtil.toJson(param));
            return Result.fail("Invalid applyId!");
        }
        Integer status = param.getStatus();
        FunpayOrderUtil.Status orderStatus = FunpayOrderUtil.getStatus(status);
        if (orderStatus == FunpayOrderUtil.Status.PROCESSING) {
            // 处理中回调个毛线～～
            return Result.succ("");
        }
        if (orderStatus == FunpayOrderUtil.Status.SUCCESS) {
            // 放款成功，需更新放款时间
            if (param.getFinishTime() != null) {
                bean.setLoan_time(param.getFinishTime());
            } else {
                bean.setLoan_time(new Date());
            }
            // 检查放款金额与到手金额
            if (!param.getAmount().equals(bean.getInhand_quota())) {
                log.error("########### invalid loan amount ############");
                log.error("param = " + GsonUtil.toJson(param));
                bean.setInhand_quota(param.getAmount());
            }
            if (!param.getTradeNo().equals(bean.getTrade_number())) {
                log.info("new trade number(" + param.getTradeNo() + ") for bean " + GsonUtil.toJson(bean));
                bean.setTrade_number(param.getTradeNo());
            }
            bean.setStatus(ApplyStatusEnum.APPLY_LOAN_SUCC.getCode());
        } else {
            log.error("payCallBack: loan failed for applyId = " + bean.getId() +
                    ", reason: " + FunpayOrderUtil.getMessage(status)
            );
            bean.setStatus(ApplyStatusEnum.APPLY_AUTO_LOAN_FAILED.getCode());
        }
        return applyService.updateApplyInfo(bean);
    }

    public Result<TbApplyInfoBean> repayCallBack(@RequestBody FunpayCallBackParam param) {
        log.info("repayCallBack param=" + GsonUtil.toJson(param));
        if (param.getAmount() == null || param.getAmount() <= 0) {
            log.error("Invalid amount. param = " + GsonUtil.toJson(param));
            return Result.fail("");
        }
        Integer status = param.getStatus();
        FunpayOrderUtil.Status orderStatus = FunpayOrderUtil.getStatus(status);
        if (orderStatus == FunpayOrderUtil.Status.PROCESSING) {
            // 处理中，返回即可
            return Result.succ();
        }
        QueryWrapper<TbRepayHistoryBean> wrapper = new QueryWrapper<>();
        TbRepayHistoryBean repayHistoryBean = repayHistoryMapper.selectById(param.getOrderNo());
        if (repayHistoryBean == null) {
            log.error("Invalid repayHistory id. param = " + GsonUtil.toJson(param));
            return Result.fail("Invalid id!");
        }
        if (orderStatus == FunpayOrderUtil.Status.ERROR) {
            // 还款失败
            repayHistoryBean.setRepay_status(RepayHistoryStatusEnum.REPAY_STATUS_FAILED.getCode());
            repayHistoryMapper.updateById(repayHistoryBean);
            mqMessenger.sendRepayMessage(repayHistoryBean);
            return Result.fail("Repay failed!");
        }

        return repayAndUpdate(param.getOrderNo(), Long.valueOf(param.getAmount()), param.getFinishTime(), false);
    }

    public Result<TbApplyInfoBean> manualLoan(ManualLoanParam param) {
        if (param.getAmount() == null || param.getAmount() <= 0) {
            log.error("Invalid amount = " + GsonUtil.toJson(param));
            return Result.fail("Invalid param!");
        }
        TbApplyInfoBean applyInfoBean = applyInfoMapper.selectById(param.getApplyId());
        if (applyInfoBean == null) {
            log.error("Invalid param = " + GsonUtil.toJson(param));
            return Result.fail("Invalid param!");
        }
        if (!applyInfoBean.getInhand_quota().equals(param.getAmount())) {
            log.error("########### invalid loan amount ############");
            log.error("param = " + GsonUtil.toJson(param));
            log.error("bean  = " + GsonUtil.toJson(applyInfoBean));
        }
        Date loanTime = DateUtil.parse(param.getLoanTime(), DateUtil.DEFAULT_DATETIME_FORMAT);
        applyInfoBean.setStatus(ApplyStatusEnum.APPLY_LOAN_SUCC.getCode());
        applyInfoBean.setOperator_id(Integer.valueOf(param.getOperatorId()));
        applyInfoBean.setLoan_time(loanTime);
        applyInfoBean.setTrade_number(param.getTradeNumber());

        return applyService.updateApplyInfo(applyInfoBean);
    }

    public Result<TbApplyInfoBean> manualRepay(ManualRepayParam param) {
        if (param.getAmount() == null || param.getAmount() <= 0) {
            log.error("Invalid amount = " + GsonUtil.toJson(param));
            return Result.fail("");
        }
        // 简单验证还款信息
        QueryWrapper<TbApplyInfoBean> wrapper = new QueryWrapper<>();
        wrapper.eq("id", param.getApplyId());
        wrapper.eq("user_id", param.getUserId());
        wrapper.in("status",
                ApplyStatusEnum.APPLY_LOAN_SUCC.getCode(),
                ApplyStatusEnum.APPLY_REPAY_PART.getCode(),
                ApplyStatusEnum.APPLY_OVERDUE.getCode(),
                ApplyStatusEnum.APPLY_WRITE_OFF.getCode());
        TbApplyInfoBean applyInfoBean = applyInfoMapper.selectOne(wrapper);
        if (applyInfoBean == null) {    // 可能已还清
            log.error("Invalid repay info! [SQL]=" + wrapper.getSqlSegment());
            return Result.fail("Invalid repay info!");
        }

        // 创建还款记录
        Date now = new Date();
        Date repayTime = DateUtil.parse(param.getRepayTime(), DateUtil.DEFAULT_DATETIME_FORMAT);
        TbRepayHistoryBean repayHistoryBean = new TbRepayHistoryBean();
        repayHistoryBean.setUser_id(Integer.valueOf(param.getUserId()));
        repayHistoryBean.setApply_id(Integer.valueOf(param.getApplyId()));
        repayHistoryBean.setOperator_id(Integer.valueOf(param.getOperatorId()));
        repayHistoryBean.setRepay_amount(Long.valueOf(param.getAmount()));
        repayHistoryBean.setRepay_img(param.getRepayImg());
        repayHistoryBean.setComment(param.getComment());
        repayHistoryBean.setRepay_time(repayTime);
        repayHistoryBean.setRepay_status(RepayHistoryStatusEnum.REPAY_STATUS_PROCESSING.getCode());
        repayHistoryBean.setCreate_time(now);
        repayHistoryBean.setUpdate_time(now);
        if (repayHistoryMapper.insert(repayHistoryBean) <= 0) {
            log.error("Failed to add new repayHistory! bean = " + GsonUtil.toJson(repayHistoryBean));
            return Result.fail("Failed to add new repayHistory!");
        }

        return repayAndUpdate(repayHistoryBean, Long.valueOf(param.getAmount()), repayTime, true);
    }

    protected Result<TbApplyInfoBean> repayAndUpdate(String repayHistoryId, Long repayAmount, Date repayTime, boolean isManual) {
        TbRepayHistoryBean repayHistoryBean = repayHistoryMapper.selectById(repayHistoryId);
        if (repayHistoryBean == null) {
            log.error("Invalid repayHistory id = " + repayHistoryId);
            return Result.fail("Invalid id!");
        }
        return repayAndUpdate(repayHistoryBean, repayAmount, repayTime, isManual);
    }

    public Result<TbApplyInfoBean> repayAndUpdate(TbRepayHistoryBean repayHistoryBean, Long repayAmount, Date repayTime, boolean isManual) {

        if (repayHistoryBean == null) {
            return Result.fail("");
        }
        if (repayAmount <= 0) {
            return Result.succ();
        }
        if (repayTime == null) {
            repayTime = new Date();
        }
        // 更新还款记录
        repayHistoryBean.setRepay_status(RepayHistoryStatusEnum.REPAY_STATUS_SUCCEED.getCode());
        repayHistoryBean.setRepay_amount(repayAmount);
        repayHistoryBean.setRepay_time(repayTime);
        repayHistoryBean.setUpdate_time(new Date());

        // 更新还款计划
        // TODO: 根据plan_id查找当期还款计划
        TbRepayPlanBean repayPlanBean = repayPlanMapper.getByApplyId(repayHistoryBean.getApply_id());
        // TbRepayPlanBean repayPlanBean = repayPlanMapper.selectById(repayHistoryBean.getRepay_plan_id());
        if (repayPlanBean == null) {
            repayHistoryMapper.updateById(repayHistoryBean);
            log.error("Invalid apply id! bean = " + GsonUtil.toJson(repayHistoryBean));
            return Result.fail("Invalid apply id!");
        }
        repayHistoryBean.setRepay_plan_id(repayPlanBean.getId());
        if (repayHistoryMapper.updateById(repayHistoryBean) <= 0) {
            log.error("Failed to update bean: " + GsonUtil.toJson(repayHistoryBean));
        }

        repayPlanBean.updateActFields(repayAmount);
        repayPlanBean.setRepay_time(repayTime);
        RepayPlanStatusEnum repayStatus;

        if (repayPlanBean.getAct_total() + repayPlanBean.getReduction_fee() < repayPlanBean.getNeed_total()) {
            repayStatus = RepayPlanStatusEnum.PLAN_PAID_PART;
        } else {
            repayStatus = RepayPlanStatusEnum.PLAN_PAID_ALL;
        }
        repayPlanBean.setRepay_status(repayStatus.getCode());
        // sendRepayMessage(repayPlanBean.getUser_id(), repayHistoryBean.getId(), repayStatus, repayAmount, repayTime);
        mqMessenger.sendRepayMessage(repayHistoryBean);
        return updateRepayPlan(repayPlanBean);
    }

    public LoanCalculatorParam calcLoanAmount(TbApplyInfoBean applyInfoBean) {
        return calcLoanAmount(applyInfoBean.getProduct_id(), applyInfoBean.getGrant_quota(), applyInfoBean.getPeriod());
    }

    public LoanCalculatorParam calcLoanAmount(Integer productId, Integer applyAmount, Integer applyPeriod) {
        QueryWrapper<TbProductInfoBean> wrapper = new QueryWrapper<>();
        wrapper.eq("id", productId);
        TbProductInfoBean productInfoBean = productInfoMapper.selectOne(wrapper);
        if (productInfoBean == null) {
            log.error("Invalid product id: " + productId);
            return null;
        }
        LoanFeeTypeEnum feeType = LoanFeeTypeEnum.getStatusByCode(productInfoBean.getFee_type());
        if (feeType == null) {
            log.error("Invalid feeType! product bean = " + GsonUtil.toJson(productInfoBean));
            return null;
        }

        int loanAmount = applyAmount;
        int feeTotal = (int)(loanAmount * productInfoBean.getFee());
        int interestTotal = (int) (loanAmount * productInfoBean.getRate() * applyPeriod);

        int quotaInhand = 0;
        int preRepay = 0;   // 预扣款
        switch (feeType) {
            case LOAN_PRE_FEE:
                quotaInhand = loanAmount - feeTotal;
                preRepay = feeTotal;
                break;
            case LOAN_PRE_FEE_PRE_INTEREST:
                quotaInhand = loanAmount - feeTotal - interestTotal;
                preRepay = feeTotal + interestTotal;
                break;
            case LOAN_POST_FEE_POST_INTEREST:
                quotaInhand = loanAmount;
                break;
            default:
                break;
        }
        LoanCalculatorParam param = new LoanCalculatorParam();
        param.setProductId(productId);
        param.setApplyAmount(applyAmount);
        param.setApplyPeriod(applyPeriod);
        param.setInhandAmount(quotaInhand);
        param.setTotalAmount(loanAmount + feeTotal + interestTotal - preRepay);
        param.setManagementFee(feeTotal);
        param.setOverdueRate(productInfoBean.getOverdue_rate());
        Date repayEndDate = DateUtil.getDate(new Date(), applyPeriod);
        param.setRepayEndDate(DateUtil.formatDate(repayEndDate));
        // log.info("Product bean: " + GsonUtil.toJson(productInfoBean));
        log.info("Return param: " + GsonUtil.toJson(param));
        return param;
    }

    public Result reduction(ReductionParam param) {

        // 获取对应的还款计划，判断是否未还清
        QueryWrapper<TbRepayPlanBean> wrapper = new QueryWrapper<>();
        // wrapper.ne("repay_status", RepayPlanStatusEnum.PLAN_PAID_ALL.getCode());
        // wrapper.ne("repay_status", RepayPlanStatusEnum.PLAN_PAID_WRITE_OFF.getCode());
        wrapper.eq("id", param.getPlanId());
        wrapper.eq("apply_id", param.getApplyId());

        TbRepayPlanBean repayPlanBean = repayPlanMapper.selectOne(wrapper);
        if (repayPlanBean == null) {
            log.error("Invalid param: " + GsonUtil.toJson(param));
            return Result.fail("Invalid param.");
        }
        if (repayPlanBean.getRepay_status() == RepayPlanStatusEnum.PLAN_PAID_ALL.getCode()) {
            log.info("Loan already paid off. bean = " + GsonUtil.toJson(repayPlanBean));
            return Result.fail("Loan already paid off.");
        }
        Long needTotal = repayPlanBean.getNeed_total();
        Long actTotal = repayPlanBean.getAct_total();
        Long actReductionFee = repayPlanBean.getReduction_fee();
        if (param.getAmount() <= 0 || needTotal - actTotal < param.getAmount()) {
            log.error("Invalid reduction amount, param = " + GsonUtil.toJson(param));
            return Result.fail("Invalid amount!");
        }
        // 增加减免金额，更新还款计划，记录费用减免
        Date now = new Date();
        TbRepayHistoryBean historyBean = new TbRepayHistoryBean();
        historyBean.setUser_id(repayPlanBean.getUser_id());
        historyBean.setApply_id(repayPlanBean.getApply_id());
        historyBean.setRepay_plan_id(param.getPlanId());
        historyBean.setRepay_status(RepayHistoryStatusEnum.REPAY_STATUS_REDUCTION.getCode());
        historyBean.setRepay_amount(Long.valueOf(param.getAmount()));
        historyBean.setOperator_id(param.getOperatorId());
        historyBean.setComment(param.getComment());
        historyBean.setCreate_time(now);
        historyBean.setUpdate_time(now);
        repayHistoryMapper.insert(historyBean);

        actTotal += param.getAmount();
        actReductionFee += param.getAmount();
        if (actTotal >= repayPlanBean.getNeed_total()) {
            repayPlanBean.setRepay_status(RepayPlanStatusEnum.PLAN_PAID_ALL.getCode());
        }
        repayPlanBean.setReduction_fee(actReductionFee);
        repayPlanBean.setUpdate_time(now);

        return updateRepayPlan(repayPlanBean);
    }


    protected List<TbRepayPlanBean> genRepayPlan(TbApplyInfoBean bean) {

        LoanFeeTypeEnum feeType = LoanFeeTypeEnum.getStatusByCode(bean.getFee_type());
        if (feeType == null) {
            log.error("genRepayPlan: invalid feeType = " + bean.getFee_type()
                    + ", applyId = " + bean.getId()
            );
            return null;
        }
        int loanAmount = bean.getGrant_quota();
        int feeTotal = (int)(loanAmount * bean.getFee());    // service fee
        int interestTotal = (int)(loanAmount * bean.getRate() * bean.getPeriod());

        int principalToRepay = loanAmount;
        int feeToRepay = feeTotal;
        int interestToRepay = interestTotal;
        int total = loanAmount + feeTotal + interestTotal;

        switch (feeType) {
            case LOAN_PRE_FEE:
                feeToRepay = 0;
                interestToRepay = interestTotal;
                break;
            case LOAN_PRE_FEE_PRE_INTEREST:
                feeToRepay = 0;
                interestToRepay = 0;
                break;
            case LOAN_POST_FEE_POST_INTEREST:
                feeToRepay = feeTotal;
                interestToRepay = interestTotal;
                break;
            default:
                break;
        }
        List<TbRepayPlanBean> plans = new ArrayList<>();

        int totalToRepay = principalToRepay + feeToRepay + interestToRepay;
        Date repayStartTime = bean.getLoan_time();
        Date repayEndTime = DateUtil.getDate(repayStartTime, bean.getPeriod());

        Date now = new Date();
        // TODO: 根据期数生成多个还款计划

        TbRepayPlanBean repayPlanBean = new TbRepayPlanBean();
        repayPlanBean.setUser_id(bean.getUser_id());
        repayPlanBean.setApply_id(bean.getId());
        repayPlanBean.setProduct_id(bean.getProduct_id());
        repayPlanBean.setSeq_no(1);
        repayPlanBean.setRepay_start_date(repayStartTime);
        repayPlanBean.setRepay_end_date(repayEndTime);
        repayPlanBean.setRepay_status(RepayPlanStatusEnum.PLAN_NOT_PAID.getCode());
        repayPlanBean.setIs_overdue(RepayPlanOverdueEnum.PLAN_NOT_OVER_DUE.getCode());
        repayPlanBean.setNeed_principal(Long.valueOf(principalToRepay));
        repayPlanBean.setNeed_interest(Long.valueOf(interestTotal));
        repayPlanBean.setNeed_management_fee(Long.valueOf(feeTotal));
        repayPlanBean.setNeed_total(Long.valueOf(totalToRepay));
        // 实际已还款项，含已扣除费用（服务费、利息等）
        repayPlanBean.setAct_interest(Long.valueOf(interestTotal - interestToRepay));
        repayPlanBean.setAct_management_fee(Long.valueOf(feeTotal - feeToRepay));
        repayPlanBean.setAct_total(Long.valueOf(0));
        repayPlanBean.setCreate_time(now);
        repayPlanBean.setUpdate_time(now);

        plans.add(repayPlanBean);

        return plans;
    }


    public TbRepayStatBean statRepayPlan(Integer applyId, List<TbRepayPlanBean> planBeans) {
        TbRepayStatBean statBean = new TbRepayStatBean();
        statBean.setApply_id(applyId);
        return statRepayPlan(statBean, planBeans);
    }

    public TbRepayStatBean statRepayPlan(TbRepayStatBean statBean, List<TbRepayPlanBean> planBeans) {
        if (planBeans == null || planBeans.size() == 0) {
            return statBean;
        }
        Collections.sort(planBeans, new Comparator<TbRepayPlanBean>() {
            @Override
            public int compare(TbRepayPlanBean o1, TbRepayPlanBean o2) {
                return o1.getId() - o2.getId();
            }
        });
        Date now = new Date();

        int normalRepayTimes = 0;
        int overdueRepayTimes = 0;
        int overdueTimes = 0;
        int currentSeq = planBeans.size();
        int overdueDays = 0;        // 逾期天数
        int maxOverdueDays = 0;     // 最大逾期天数

        for (TbRepayPlanBean planBean : planBeans) {
            Date repayStartDate = planBean.getRepay_start_date();
            Date repayEndDate = planBean.getRepay_end_date();
            if (DateUtil.compareDate(repayStartDate, now) <= 0 && DateUtil.compareDate(now, repayEndDate) <= 0) {
                currentSeq = Math.min(currentSeq, planBean.getSeq_no());
            }
            boolean isOverdue = planBean.getIs_overdue() == RepayPlanOverdueEnum.PLAN_OVER_DUE.getCode();
            boolean repayed = planBean.getAct_total() > 0;
            if (isOverdue) {
                Date overdueEndDate = now;
                if (planBean.getRepay_status() == RepayPlanStatusEnum.PLAN_PAID_ALL.getCode()) {
                    overdueEndDate = planBean.getRepay_time();
                } else if (planBean.getRepay_status() == RepayPlanStatusEnum.PLAN_PAID_WRITE_OFF.getCode()) {
                    overdueEndDate = planBean.getUpdate_time();
                } else {
                    // 最后一期逾期天数
                    overdueDays = Math.max(0, DateUtil.getDaysBetween(repayEndDate, now));
                }
                maxOverdueDays = Math.max(maxOverdueDays, DateUtil.getDaysBetween(repayEndDate, overdueEndDate));
            }
            if (planBean.getRepay_status() == RepayPlanStatusEnum.PLAN_PAID_PART.getCode() ||
                    planBean.getRepay_status() == RepayPlanStatusEnum.PLAN_PAID_ALL.getCode()) {
                overdueRepayTimes += isOverdue && repayed ? 1 : 0;
                normalRepayTimes += !isOverdue && repayed ? 1 : 0;
            }
            overdueTimes += isOverdue ? 1 : 0;
            statBean.inc(planBean);
        }

        statBean.setCurrent_seq(currentSeq);
        statBean.setNormal_repay_times(normalRepayTimes);
        statBean.setOverdue_repay_times(overdueRepayTimes);
        statBean.setOverdue_times(overdueTimes);
        statBean.setOverdue_days_max(maxOverdueDays);
        statBean.setOverdue_days(overdueDays);

        return statBean;
    }

    /**
     * 获取还款使用的虚拟卡
     *
     * @param repayInfo 还款参数
     * @return 虚拟卡号、收款单位、收款银行名称、支行名称、银行地图URL、虚拟卡费用
     */
    public Result<CreateVCVO> getVirtualCard(RepayInfo repayInfo) {
        log.info("getVirtualCard repayInfo=" + GsonUtil.toJson(repayInfo));
        // 1. 检查虚拟卡: 是否可用 && 订单是否一致。如订单不一致需要先销毁，再申请新的虚拟卡
        Date now = new Date();
        VCInfo vcInfo = virtualCardInfoMapper.getVCInfo(repayInfo.getUserId());
        if (vcInfo != null) {
            boolean isValid = vcInfo.getStatus() == VirtualCardStatusEnum.VC_STATUS_VALID.getCode();
            boolean isExpired = vcInfo.getExpireTime() != null && DateUtil.compareDate(now, vcInfo.getExpireTime()) > 0;
            if (isValid && !isExpired) {
                if (repayInfo.getApplyId().equals(vcInfo.getApplyId())) {
                    // 虚拟卡尚未失效
                    CreateVCVO obj = new CreateVCVO();
                    obj.setAccountName(vcInfo.getAccountName());
                    obj.setAccountNo(vcInfo.getAccountNo());
                    obj.setBankLink(vcInfo.getBankLink());
                    obj.setBankName(vcInfo.getBankName());
                    obj.setBranchBankName(vcInfo.getBranchName());
                    obj.setServiceFee(vcInfo.getServiceFee());
                    return Result.succ(obj);
                }
                // 订单号改变(如申请了虚拟卡，但是通过其他方式已还清)
                destoryVirtualCard(vcInfo.getOrderNo(), vcInfo.getAccountNo());
            }
        }

        // 2. 重新获取虚拟卡
        TbUserRegistInfoBean userInfo = userRegisterInfoMapper.selectById(repayInfo.getUserId());
        if (userInfo == null) {
            log.error("Invalid userId!");
            return Result.fail("Invalid userId");
        }

        String orderNo = repayInfo.getApplyId();     // 使用apply_id作为虚拟卡的order_no

        int index = max(0, repayInfo.getPhone().length() - 9);
        CreateVCInfo createVCInfo = new CreateVCInfo();
        createVCInfo.setUserId(repayInfo.getUserId());
        createVCInfo.setMobile(repayInfo.getPhone().substring(index));
        createVCInfo.setAmount(repayInfo.getAmount());
        createVCInfo.setUserName(userInfo.getName());
        createVCInfo.setOrderNo(orderNo);

        try {
            Result<CreateVCVO> vcResult = funpayService.createVC(createVCInfo);
            log.info("create virtual card result: " + GsonUtil.toJson(vcResult));
            if (vcResult.isSucc()) {
                CreateVCVO vc = vcResult.getData();
                TbVirtualCardInfo vcCardInfo = new TbVirtualCardInfo();
                vcCardInfo.setUser_id(Integer.valueOf(repayInfo.getUserId()));
                vcCardInfo.setBank_name(vc.getBankName());
                vcCardInfo.setApply_id(Integer.valueOf(repayInfo.getApplyId()));
                vcCardInfo.setOrder_no(Integer.valueOf(orderNo));
                vcCardInfo.setVc_no(vc.getAccountNo());
                vcCardInfo.setStatus(VirtualCardStatusEnum.VC_STATUS_VALID.getCode());
                vcCardInfo.setService_fee(vc.getServiceFee());
                vcCardInfo.setExpire_time(vc.getExpireDate());
                vcCardInfo.setCreate_time(now);

                if (vcInfo != null) {
                    vcCardInfo.setId(vcInfo.getId());
                    virtualCardInfoMapper.updateById(vcCardInfo);
                } else {
                    virtualCardInfoMapper.insert(vcCardInfo);
                }
                TbVirtualCardInfoHistory virtualCardInfoHistory = new TbVirtualCardInfoHistory();
                virtualCardInfoHistory.copy(vcCardInfo);
                virtualCardInfoHistoryMapper.insert(virtualCardInfoHistory);

                // 验证银行信息是否有变动
                TbVirtualCardBankInfo bankInfo = virtualCardBankInfoMapper.selectOne(
                        new QueryWrapper<TbVirtualCardBankInfo>().eq("bank_name", vc.getBankName())
                );
                if (bankInfo == null) {
                    bankInfo = new TbVirtualCardBankInfo();
                    bankInfo.setBank_account_name(vc.getAccountName());
                    bankInfo.setBank_name(vc.getBankName());
                    bankInfo.setBranch_name(vc.getBranchBankName());
                    bankInfo.setLink(vc.getBankLink());
                    bankInfo.setService_fee(vc.getServiceFee());
                    bankInfo.setCreate_time(now);
                    virtualCardBankInfoMapper.insert(bankInfo);
                }

                return vcResult;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return Result.fail("");
    }

    public Result vcCallBack(@RequestBody FunpayCallBackParam param) {

        return funpayRepay(param);
    }


    /**
     * 通过funpay还款，其中orderNo为applyId
     * @param param
     * @return
     */
    private synchronized Result funpayRepay(@RequestBody FunpayCallBackParam param) {
        log.info("funpayRepay param=" + GsonUtil.toJson(param));
        if (param.getAmount() == null || param.getAmount() <= 0) {
            log.error("Invalid amount. param = " + GsonUtil.toJson(param));
            return Result.fail("");
        }
        Integer status = param.getStatus();
        FunpayOrderUtil.Status orderStatus = FunpayOrderUtil.getStatus(status);
        if (orderStatus == FunpayOrderUtil.Status.PROCESSING) {
            // 处理中，返回即可
            return Result.succ();
        }

        Integer applyId = Integer.valueOf(param.getOrderNo());
        TbRepayPlanBean repayPlanBean = repayPlanMapper.selectOne(
                new QueryWrapper<TbRepayPlanBean>().eq("apply_id", applyId));
        if (repayPlanBean == null) {
            log.error("Invalid applyId = " + applyId);
            return null;
        }
        if (repayPlanBean.getRepay_status() == RepayPlanStatusEnum.PLAN_PAID_ALL.getCode()) {
            log.error("Nothing to repay for applyId=" + applyId);
            return null;
        }

        TbRepayHistoryBean repayHistoryBean = getOrNewRepayHistoryBean(
                repayPlanBean.getUser_id(), applyId, param.getAmount(), repayPlanBean.getId()
        );
        if (repayHistoryBean == null) {
            return Result.fail("");
        }
        if (orderStatus == FunpayOrderUtil.Status.ERROR) {
            // 还款失败
            repayHistoryBean.setRepay_status(RepayHistoryStatusEnum.REPAY_STATUS_FAILED.getCode());
            repayHistoryMapper.updateById(repayHistoryBean);
            mqMessenger.sendRepayMessage(repayHistoryBean);
            return Result.fail("Repay failed!");
        }
        Result<TbApplyInfoBean> ret = repayAndUpdate(repayHistoryBean, Long.valueOf(param.getAmount()), param.getFinishTime(), false);
        if (ret.isSucc() && ret.getData() != null && ret.getData().getStatus() == ApplyStatusEnum.APPLY_REPAY_ALL.getCode()) {
            // 已还清，更新虚拟卡状态为不可用
            TbVirtualCardInfo cardInfo = virtualCardInfoMapper.selectOne(
                    new QueryWrapper<TbVirtualCardInfo>().eq("user_id", repayPlanBean.getUser_id())
            );
            if (cardInfo != null && cardInfo.getStatus() == VirtualCardStatusEnum.VC_STATUS_VALID.getCode()) {
                if (destoryVirtualCard(String.valueOf(cardInfo.getOrder_no()), cardInfo.getVc_no()).isSucc()) {
                    cardInfo.setStatus(VirtualCardStatusEnum.VC_STATUS_INVALID.getCode());
                    cardInfo.setExpire_time(new Date());
                    virtualCardInfoMapper.updateById(cardInfo);
                }
            }
        }
        return ret;
    }

    private Result destoryVirtualCard(String orderNo, String accountNo) {
        // 需要先销毁已有虚拟卡
        DestroyVCInfo _info = new DestroyVCInfo();
        _info.setOrderNo(orderNo);
        _info.setAccountNo(accountNo);
        try {
            Result ret = funpayService.destroyVC(_info);
            log.info("destory virtual card, bean = " + GsonUtil.toJson(_info) + ", ret = " + GsonUtil.toJson(ret));
            if (!ret.isSucc()) {
                return Result.fail("Failed to destory virtual card!");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return Result.fail("Failed to destory virtual card!");
        }
        return Result.succ();
    }
}
