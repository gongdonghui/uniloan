package com.sup.market.web;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.google.common.collect.ImmutableMap;
import com.sup.common.bean.TbApplyInfoBean;
import com.sup.common.bean.TbRepayPlanBean;
import com.sup.common.bean.TbRepayStatBean;
import com.sup.common.bean.TbUserRegistInfoBean;
import com.sup.common.loan.ApplyStatusEnum;
import com.sup.common.loan.RepayPlanStatusEnum;
import com.sup.common.mq.UserStateMessage;
import com.sup.market.mapper.TbApplyInfoMapper;
import com.sup.market.mapper.TbRepayPlanMapper;
import com.sup.market.mapper.TbRepayStatMapper;
import com.sup.market.mapper.TbUserRegistInfoMapper;
import com.sup.market.service.MqProducerService;
import com.sup.market.util.ToolUtils;
import org.apache.log4j.Logger;
import org.apache.rocketmq.common.message.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import java.util.Date;
import java.util.List;

import static com.sup.common.mq.MqTag.ONEDAY_OVERDUE_NOTIFY;
import static com.sup.common.mq.MqTag.ONEDAY_TO_REPAY_NOTIFY;
import static com.sup.common.mq.MqTopic.SYSTEM_NOTIFY;

/**
 * Created by xidongzhou1 on 2019/9/23.
 */
@Configuration
@EnableScheduling
public class CronTask {
  public Logger logger = Logger.getLogger(CronTask.class);

  @Autowired
  private MqProducerService producer;

  @Autowired
  private TbApplyInfoMapper tb_apply_info_mapper;

  @Autowired
  private TbRepayPlanMapper tb_repay_plan_mapper;

  @Autowired
  private TbRepayStatMapper tb_repay_stat_mapper;

  @Autowired
  private TbUserRegistInfoMapper tb_regist_info_mapper;


  @Scheduled(cron = "0 0 12 * * *")
  public void NotifyRepayUser() {
    QueryWrapper query = new QueryWrapper<TbApplyInfoBean>().eq("status", ApplyStatusEnum.APPLY_LOAN_SUCC.getCode()).orderByAsc("create_time");
    List<TbApplyInfoBean> applies = tb_apply_info_mapper.selectList(query);
    if (applies == null) {
      logger.info("has_no_loan_succ_order");
      return;
    }

    for (TbApplyInfoBean apply : applies) {
      logger.info("analyze_apply: " + apply.getId().toString());
      try {

        TbRepayStatBean stat_bean = tb_repay_stat_mapper.selectOne(new QueryWrapper<TbRepayStatBean>().eq("apply_id", apply.getId()).last("limit 1"));
        if (stat_bean == null) {
          logger.info("found_no_stat_bean_for_apply: " + apply.getId().toString());
          continue;
        }

        int curr_repay_seq_no = stat_bean.getCurrent_seq();
        TbRepayPlanBean plan = tb_repay_plan_mapper.selectOne(new QueryWrapper<TbRepayPlanBean>().eq("apply_id", apply.getId()).eq("seq_no", curr_repay_seq_no));
        logger.info(String.format("current_seq_no: %d, plan: %s", curr_repay_seq_no, JSON.toJSONString(plan)));

        if (plan.getRepay_status().equals(RepayPlanStatusEnum.PLAN_PAID_ALL.getCode())) {
          continue;
        }

        Date plan_dt = plan.getRepay_end_date();
        long plan_day = plan_dt.getTime() / (24 * 3600 * 1000l);
        long today = System.currentTimeMillis() / (24 * 3600 * 1000l);
        logger.info(String.format("plan_dt: %s, today: %s", ToolUtils.NormTime(plan_dt), ToolUtils.NormTime(new Date())));
        if (plan_day == (today + 1)) {
          // need to pay tomorrow !!
          UserStateMessage user_msg = new UserStateMessage();
          user_msg.setUser_id(apply.getUser_id());
          user_msg.setRel_id(plan.getId());
          user_msg.setState(ONEDAY_TO_REPAY_NOTIFY);
          user_msg.setMobile(tb_regist_info_mapper.selectById(apply.getId()).getMobile());
          user_msg.setCreate_time(ToolUtils.NormTime(new Date()));
          user_msg.setExt(JSON.toJSONString(ImmutableMap.of("order_id", String.valueOf(apply.getId()), "amount", String.valueOf(plan.getNeed_total()))));
          producer.sendMessage(new Message(SYSTEM_NOTIFY, ONEDAY_TO_REPAY_NOTIFY, "", JSON.toJSONString(user_msg).getBytes()));
          continue;
        }

        if (plan_day == (today - 1)) {
          UserStateMessage user_msg = new UserStateMessage();
          user_msg.setUser_id(apply.getUser_id());
          user_msg.setRel_id(plan.getId());
          user_msg.setState(ONEDAY_OVERDUE_NOTIFY);
          user_msg.setMobile(tb_regist_info_mapper.selectById(apply.getId()).getMobile());
          user_msg.setCreate_time(ToolUtils.NormTime(new Date()));
          user_msg.setExt(JSON.toJSONString(ImmutableMap.of("order_id", String.valueOf(apply.getId()), "amount", String.valueOf(plan.getNeed_total()))));
          producer.sendMessage(new Message(SYSTEM_NOTIFY, ONEDAY_OVERDUE_NOTIFY, "", JSON.toJSONString(user_msg).getBytes()));
          continue;
        }
      } catch (Exception e) {
        logger.error("run_cron_task_error: " + ToolUtils.GetTrace(e));
      }
    }
  }
}
