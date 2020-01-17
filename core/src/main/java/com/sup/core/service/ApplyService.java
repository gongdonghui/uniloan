package com.sup.core.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.gson.Gson;
import com.sup.common.bean.TbApplyInfoBean;
import com.sup.common.bean.TbApplyInfoHistoryBean;
import com.sup.common.bean.TbOperationTaskBean;
import com.sup.common.loan.ApplyStatusEnum;
import com.sup.common.loan.LoanFeeTypeEnum;
import com.sup.common.loan.OperationTaskStatusEnum;
import com.sup.common.loan.OperationTaskTypeEnum;
import com.sup.common.mq.ApplyStateMessage;
import com.sup.common.mq.MqTag;
import com.sup.common.mq.MqTopic;
import com.sup.common.mq.UserStateMessage;
import com.sup.common.param.LoanCalculatorParam;
import com.sup.common.util.DateUtil;
import com.sup.common.util.GsonUtil;
import com.sup.common.util.Result;
import com.sup.core.mapper.ApplyInfoHistoryMapper;
import com.sup.core.mapper.ApplyInfoMapper;
import com.sup.core.mapper.OperationTaskMapper;
import com.sup.core.util.MqMessenger;
import io.netty.handler.codec.mqtt.MqttConnAckMessage;
import lombok.extern.log4j.Log4j;
import org.apache.rocketmq.common.message.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

/**
 * Project:uniloan
 * Class:  LoanService
 * <p>
 * Author: guanfeng
 * Create: 2019-09-09
 */

@Log4j
@Service
public class ApplyService {

    @Autowired
    private ApplyInfoMapper applyInfoMapper;
    @Autowired
    private ApplyInfoHistoryMapper applyInfoHistoryMapper;
    @Autowired
    private OperationTaskMapper operationTaskMapper;

    @Autowired
    private LoanService loanService;
    @Autowired
    private MqMessenger mqMessenger;

    @Autowired
    private RuleConfigService ruleConfigService;


    public boolean addApplyInfo(TbApplyInfoBean bean) {
        log.info("addApplyInfo: userId=" + bean.getUser_id()
                + ", appId=" + bean.getApp_id()
                + ", channelId=" + bean.getChannel_id()
                + ", productId=" + bean.getProduct_id());

        if (applyInfoMapper.insert(bean) <= 0) {
            log.error("insert into ApplyInfo failed!");
            return false;
        }
        // apply id will be set after insert succeeded.
        log.info("insert into ApplyInfo succ, applyId=" + bean.getId());
        TbApplyInfoHistoryBean applyInfoHistoryBean = new TbApplyInfoHistoryBean(bean);
        applyInfoHistoryBean.setCreate_time(bean.getCreate_time());
        if (applyInfoHistoryMapper.insert(applyInfoHistoryBean) <= 0) {
            log.error("insert into ApplyInfoHistory failed!");
            return false;
        }
        return true;
    }

    public List<TbApplyInfoBean> getApplyInprogress(Integer userId) {
        QueryWrapper<TbApplyInfoBean> wrapper = new QueryWrapper<>();
        wrapper.eq("user_id", userId);
        wrapper.in("status", ApplyStatusEnum.APPLY_INIT.getCode()
                , ApplyStatusEnum.APPLY_AUTO_PASS.getCode()
                , ApplyStatusEnum.APPLY_FIRST_PASS.getCode()
                , ApplyStatusEnum.APPLY_SECOND_PASS.getCode()
                , ApplyStatusEnum.APPLY_FINAL_PASS.getCode()
                , ApplyStatusEnum.APPLY_AUTO_LOANING.getCode()
                , ApplyStatusEnum.APPLY_AUTO_LOAN_FAILED.getCode()
                , ApplyStatusEnum.APPLY_LOAN_SUCC.getCode()
                , ApplyStatusEnum.APPLY_REPAY_PART.getCode()
                , ApplyStatusEnum.APPLY_OVERDUE.getCode()
                );
        return applyInfoMapper.selectList(wrapper);
    }

    public Result updateApplyInfo(TbApplyInfoBean bean) {
        if (bean == null) {
            return Result.fail("Bean is null!");
        }

        ApplyStatusEnum newState = ApplyStatusEnum.getStatusByCode(bean.getStatus());
        if (newState == null) {
            log.error("updateApplyInfo: invalid status=" + bean.getStatus()
                    + ", operator = " + bean.getOperator_id()
                    + ", applyId = " + bean.getId()
            );
            return Result.fail("invalid status!");
        }
        log.debug("updateApplyInfo: bean = " + GsonUtil.toJson(bean) +
                ", new status = " + newState.getCodeDesc()
        );
        // TODO: should check the status order here to avoid invalid operation

        Date now = new Date();
        bean.setUpdate_time(now);
        TbOperationTaskBean taskBean = null;
        switch (newState) {  // 对特定状态，做相应的更新
            case APPLY_INIT:
                bean.setCreate_time(now);
                break;
            case APPLY_AUTO_PASS:   // 自动审核通过，添加初审任务
                addOperationTask(bean.getId(), OperationTaskTypeEnum.TASK_FIRST_AUDIT, "");
                break;
            case APPLY_FIRST_PASS:  // 初审通过，添加终审任务
                addOperationTask(bean.getId(), OperationTaskTypeEnum.TASK_FINAL_AUDIT, "");
                break;
            case APPLY_SECOND_PASS: // 暂不使用
                break;
            case APPLY_FINAL_PASS:  // 终审通过，更新到手金额
                LoanCalculatorParam param = loanService.calcLoanAmount(bean);
                if (param == null) {
                    log.error("Calculate loan amount failed! bean = " + GsonUtil.toJson(bean));
                    break;
                }
                //bean.setInhand_quota(getInhandQuota(bean));
                bean.setInhand_quota(param.getInhandAmount());
                bean.setPass_time(now);
                break;
            case APPLY_LOAN_SUCC:   // 放款成功，生成还款计划
                if (bean.getLoan_time() == null) { // 手动放款时，放款时间可能为空
                    bean.setLoan_time(now);
                }
                if (!loanService.addRepayPlan(bean)) {
                    log.error("Failed to add repay plan for applyId = " + bean.getId());
                }
                break;
            case APPLY_OVERDUE:     // 逾期，添加逾期任务
                addOperationTask(bean.getId(), OperationTaskTypeEnum.TASK_OVERDUE, "");
                break;
            case APPLY_REPAY_ALL:   // 已还清
                closeOperationTask(bean.getId(), OperationTaskTypeEnum.TASK_OVERDUE, "pay off");
                log.info("APPLY_REPAY_ALL, update credit level for user=" + bean.getUser_id());
                break;
            case APPLY_WRITE_OFF:   // 还款计划也更新为核销
                // 回收逾期任务
                closeOperationTask(bean.getId(), OperationTaskTypeEnum.TASK_OVERDUE, "write off");
                loanService.writeOffRepayPlan(bean.getId());
                break;
            default:
                break;
        }

        mqMessenger.applyStatusChange(bean);

        if (applyInfoMapper.updateById(bean) <= 0) {
            return Result.fail("update ApplyInfo failed! bean = " + GsonUtil.toJson(bean));
        }

        if (newState == ApplyStatusEnum.APPLY_REPAY_ALL) {
            ruleConfigService.updateCreditLevelForUser(bean.getUser_id());
        }

        TbApplyInfoHistoryBean applyInfoHistoryBean = new TbApplyInfoHistoryBean(bean);
        applyInfoHistoryBean.setCreate_time(now);
        if (applyInfoHistoryMapper.insert(applyInfoHistoryBean) <= 0) {
            return Result.fail("insert into ApplyInfoHistory failed! bean = " + GsonUtil.toJson(applyInfoHistoryBean));
        }
        return Result.succ();
    }
    public synchronized void closeOperationTask(Integer applyId, OperationTaskTypeEnum taskType, String comment) {
        // 将任务状态置为完成
        QueryWrapper<TbOperationTaskBean> wrapper = new QueryWrapper<>();
        wrapper.eq("apply_id", applyId);
        wrapper.eq("task_type", taskType.getCode());
        wrapper.ne("status", OperationTaskStatusEnum.TASK_STATUS_DONE.getCode());
        wrapper.eq("has_owner", 1);
        wrapper.last("limit 1");
        TbOperationTaskBean taskBean = operationTaskMapper.selectOne(wrapper);
        if (taskBean == null) {  // not exists or finished
            return;
        }
        taskBean.setStatus(OperationTaskStatusEnum.TASK_STATUS_DONE.getCode());
        taskBean.setComment(comment);
        taskBean.setUpdate_time(new Date());
        operationTaskMapper.updateById(taskBean);
    }

    public synchronized void addOperationTask(Integer applyId, OperationTaskTypeEnum taskType, String comment) {
        QueryWrapper<TbOperationTaskBean> wrapper = new QueryWrapper<>();
        wrapper.eq("apply_id", applyId);
        wrapper.eq("task_type", taskType.getCode());
        // wrapper.eq("has_owner", 0);
        wrapper.last("limit 1");
        TbOperationTaskBean taskBean = operationTaskMapper.selectOne(wrapper);
        if (taskBean != null) {  // already exists
            return;
        }
        Date now = new Date();
        taskBean = new TbOperationTaskBean();
        taskBean.setApply_id(applyId);
        taskBean.setHas_owner(0);
        taskBean.setTask_type(taskType.getCode());
        taskBean.setStatus(OperationTaskStatusEnum.TASK_STATUS_NEW.getCode());
        taskBean.setComment(comment);
        taskBean.setCreate_time(now);
        taskBean.setUpdate_time(now);
        operationTaskMapper.insert(taskBean);
    }

    public synchronized void cancelOperationTask(Integer applyId, OperationTaskTypeEnum taskType, String comment) {
        QueryWrapper<TbOperationTaskBean> wrapper = new QueryWrapper<>();
        wrapper.eq("apply_id", applyId);
        wrapper.eq("task_type", taskType.getCode());
        wrapper.eq("status", OperationTaskStatusEnum.TASK_STATUS_NEW.getCode());
        wrapper.eq("has_owner", 1);
        TbOperationTaskBean taskBean = operationTaskMapper.selectOne(wrapper);
        if (taskBean != null) {
            taskBean.setHas_owner(0);
            taskBean.setStatus(OperationTaskStatusEnum.TASK_STATUS_CANCEL.getCode());
            taskBean.setComment(comment);
            taskBean.setUpdate_time(new Date());
            operationTaskMapper.updateById(taskBean);
        }
    }

//    protected void sendMessage(TbApplyInfoBean bean) throws Exception {
//        String state_desc = ApplyStatusEnum.getStatusByCode(bean.getStatus()).getCodeDesc();
//        UserStateMessage message = new UserStateMessage();
//        message.setUser_id(bean.getUser_id());
//        message.setRel_id(bean.getApp_id());
//        message.setState(state_desc);
//        message.setCreate_time(DateUtil.format(new Date(), DateUtil.DEFAULT_DATETIME_FORMAT));
//        message.setExt(JSON.toJSONString(ImmutableMap.of("order_id", bean.getApp_id().toString())));
//        mqProducerService.sendMessage(new Message(MqTopic.USER_STATE, state_desc, "", GsonUtil.toJson(message).getBytes()));
//    }

//    protected int getInhandQuota(TbApplyInfoBean bean) {
//        LoanFeeTypeEnum feeType = LoanFeeTypeEnum.getStatusByCode(bean.getFee_type());
//        if (feeType == null) {
//            log.error("Invalid feeType = " + bean.getFee_type() + ", applyId = " + bean.getId());
//            return 0;
//        }
//        int loanAmount = bean.getGrant_quota();
//        int feeTotal = (int)(loanAmount * bean.getFee());    // service fee
//        int interestTotal = (int)(loanAmount * bean.getRate() * bean.getPeriod());
//
//        int quotaInhand = 0;
//        switch (feeType) {
//            case LOAN_PRE_FEE:
//                quotaInhand = loanAmount - feeTotal;
//                break;
//            case LOAN_PRE_FEE_PRE_INTEREST:
//                quotaInhand = loanAmount - feeTotal - interestTotal;
//                break;
//            case LOAN_POST_FEE_POST_INTEREST:
//                quotaInhand = loanAmount;
//                break;
//            default:
//                break;
//        }
//        return quotaInhand;
//    }
}
