package com.sup.core.task;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.sup.common.bean.TbLoanInfoBean;
import com.sup.common.bean.TbReportOverdueDetailBean;
import com.sup.common.loan.ApplyStatusEnum;
import com.sup.common.loan.OperationTaskTypeEnum;
import com.sup.common.util.DateUtil;
import com.sup.common.util.GsonUtil;
import com.sup.core.mapper.LoanInfoMapper;
import com.sup.core.mapper.ReportOverdueDetailMapper;
import com.sup.core.service.ApplyService;
import lombok.extern.log4j.Log4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * Project:uniloan
 * Class:  ScheduleTasks
 * <p>
 * Author: guanfeng
 * Create: 2019-09-20
 */

@Log4j
@Component
public class ReportTasks {
    @Value("#{new Integer('${query.page-num}')}")
    private Integer QUERY_PAGE_NUM;

    @Autowired
    private ReportOverdueDetailMapper reportOverdueDetailMapper;

    @Autowired
    private ApplyService applyService;
    @Autowired
    private LoanInfoMapper loanInfoMapper;



    /**
     * 更新催收明细表(tb_report_overdue_detail)
     */
    @Scheduled(cron = "0 5 */1 * * ?")
    public void updateOverdueDetail() {
        Date now = new Date();
        Date yesterday = DateUtil.getDate(now, -1);
        StringBuilder sb = new StringBuilder();
        sb.append(" and (ot.status!=1 or (ot.status=1 and ot.update_time>='" + DateUtil.startOf(yesterday) +"'))");

        log.info("updateOverdueDetail conditions:" + sb.toString());
        List<TbReportOverdueDetailBean> detailBeans = reportOverdueDetailMapper.getReportOverdueDetail(sb.toString());

        for (TbReportOverdueDetailBean bean : detailBeans) {
            QueryWrapper<TbReportOverdueDetailBean> wrapper = new QueryWrapper<>();
            wrapper.eq("data_dt", bean.getData_dt());
            // wrapper.eq("operator_id", bean.getOperator_id());
            wrapper.eq("apply_id", bean.getApply_id());
            TbReportOverdueDetailBean _bean = reportOverdueDetailMapper.selectOne(wrapper);
            if (_bean == null) {
                bean.setCreate_time(now);
                bean.setUpdate_time(now);
                if (reportOverdueDetailMapper.insert(bean) <= 0) {
                    log.error("Failed to insert bean:" + GsonUtil.toJson(bean));
                }
                continue;
            }
            if (!bean.eaquals(_bean)) {
                bean.setNormal_repay(_bean.getNormal_repay());  // 保持正常还款金额为任务分配前的值
                bean.setId(_bean.getId());
                bean.setUpdate_time(now);
                if (reportOverdueDetailMapper.updateById(bean) <= 0) {
                    log.error("Failed to update bean:" + GsonUtil.toJson(bean));
                }
            }
            if (bean.getStatus() == ApplyStatusEnum.APPLY_REPAY_ALL.getCode()) {
                // 已还清，关闭逾期任务
                applyService.closeOperationTask(bean.getApply_id(), OperationTaskTypeEnum.TASK_OVERDUE, "pay off");

            }
        }
    }

    /**
     * 更新还款信息表（tb_loan_info）
     * 该任务需在更新还款统计任务之后运行
     */
    @Scheduled(cron = "0 0 2 * * ?")
    public void updateLoanInfo() {
        List<TbLoanInfoBean> beans = loanInfoMapper.selectList(null);
        Map<Integer, TbLoanInfoBean> beanMap = new HashMap<>();
        for (TbLoanInfoBean bean : beans) {
            beanMap.put(bean.getApply_id(), bean);
        }
        Date now = new Date();
        Integer total = loanInfoMapper.getLoanInfoCount();
        Integer pageCount = (total + QUERY_PAGE_NUM - 1) / QUERY_PAGE_NUM;
        for (int i = 0; i < pageCount; i++) {
            List<TbLoanInfoBean> loanInfoBeans = loanInfoMapper.getLoanInfo(i * QUERY_PAGE_NUM, QUERY_PAGE_NUM);
            for (TbLoanInfoBean bean : loanInfoBeans) {
                TbLoanInfoBean oldBean = beanMap.getOrDefault(bean.getApply_id(), null);
                if (oldBean == null) {
                    bean.setCreate_time(now);
                    bean.setUpdate_time(now);
                    if (loanInfoMapper.insert(bean) <= 0) {
                        log.error("updateLoanInfo failed to insert bean:" + GsonUtil.toJson(bean));
                    }
                } else {
                    bean.setId(oldBean.getId());
                    bean.setCreate_time(oldBean.getCreate_time());
                    bean.setUpdate_time(now);
                    if (loanInfoMapper.updateById(bean) <= 0) {
                        log.error("updateLoanInfo failed to update bean:" + GsonUtil.toJson(bean));
                    }
                }
            }
        }
    }

}
