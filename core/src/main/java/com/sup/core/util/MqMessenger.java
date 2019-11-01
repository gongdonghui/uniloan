package com.sup.core.util;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.ImmutableMap;
import com.sup.common.bean.TbApplyInfoBean;
import com.sup.common.bean.TbRepayHistoryBean;
import com.sup.common.loan.ApplyStatusEnum;
import com.sup.common.loan.RepayHistoryStatusEnum;
import com.sup.common.mq.MqTag;
import com.sup.common.mq.MqTopic;
import com.sup.common.mq.UserStateMessage;
import com.sup.common.util.DateUtil;
import com.sup.common.util.GsonUtil;
import com.sup.core.service.MqProducerService;
import lombok.extern.log4j.Log4j;
import org.apache.rocketmq.common.message.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;

@Log4j
@Component
public class MqMessenger {

    @Autowired
    private MqProducerService mqProducerService;


    /**
     * 进件状态变更
     * @param bean
     * @throws Exception
     */
    public void applyStatusChange(TbApplyInfoBean bean) {
        try {
            String state_desc = ApplyStatusEnum.getStatusByCode(bean.getStatus()).getCodeDesc();
            String pass_time = bean.getPass_time() == null ? "" : DateUtil.formatDateTime(bean.getPass_time());
            String loan_time = bean.getLoan_time() == null ? "" : DateUtil.formatDateTime(bean.getLoan_time());

            UserStateMessage message = new UserStateMessage();
            message.setUser_id(bean.getUser_id());
            message.setRel_id(bean.getApp_id());
            message.setState(state_desc);
            message.setCreate_time(DateUtil.format(new Date(), DateUtil.DEFAULT_DATETIME_FORMAT));
            message.setExt(JSON.toJSONString(
                    ImmutableMap.of(
                            "order_id", bean.getId().toString(),
                            "pass_time", pass_time,
                            "loan_time", loan_time
                    ))
            );
            log.info("bean: " + GsonUtil.toJson(bean));
            log.info("MQ message: " + GsonUtil.toJson(message));
            mqProducerService.sendMessage(new Message(MqTopic.USER_STATE, state_desc, "", GsonUtil.toJson(message).getBytes()));
        }catch (Exception e) {
            e.printStackTrace();
            log.error("applyStatusChange: Failed to send MQ message. e = " + e.getMessage());
        }
    }

    /**
     * 还款消息通知
     * @param repayHistoryBean
     */
    public void sendRepayMessage(TbRepayHistoryBean repayHistoryBean) {
        try {
            RepayHistoryStatusEnum status = RepayHistoryStatusEnum.getStatusByCode(repayHistoryBean.getRepay_status());
            String state_desc = status.getCodeDesc();
            UserStateMessage message = new UserStateMessage();
            message.setUser_id(repayHistoryBean.getUser_id());
            message.setRel_id(repayHistoryBean.getApply_id());
            message.setState(state_desc);
            message.setCreate_time(DateUtil.format(new Date(), DateUtil.DEFAULT_DATETIME_FORMAT));
            message.setExt(JSON.toJSONString(
                    ImmutableMap.of(
                            "order_id", repayHistoryBean.getApply_id().toString(),
                            "repay_amount", repayHistoryBean.getRepay_amount(),
                            "repay_time", DateUtil.formatDateTime(repayHistoryBean.getRepay_time())
                    )));
            log.info("bean: " + GsonUtil.toJson(repayHistoryBean));
            log.info("MQ message: " + GsonUtil.toJson(message));
            if (status == RepayHistoryStatusEnum.REPAY_STATUS_SUCCEED) {
                mqProducerService.sendMessage(new Message(MqTopic.USER_STATE, MqTag.REPAY_SUCC_NOTIFY, "", GsonUtil.toJson(message).getBytes()));
            } else if (status == RepayHistoryStatusEnum.REPAY_STATUS_FAILED) {
                mqProducerService.sendMessage(new Message(MqTopic.USER_STATE, MqTag.REPAY_FAIL_NOTIFY, "", GsonUtil.toJson(message).getBytes()));
            }
        }catch (Exception e) {
            e.printStackTrace();
            log.error("Failed to send MQ message. e = " + e.getMessage());
        }
    }
}
