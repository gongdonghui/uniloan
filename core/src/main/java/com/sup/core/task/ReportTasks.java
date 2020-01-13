package com.sup.core.task;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.sup.common.bean.TbReportOverdueDetailBean;
import com.sup.common.loan.ApplyStatusEnum;
import com.sup.common.loan.OperationTaskTypeEnum;
import com.sup.common.util.DateUtil;
import com.sup.common.util.GsonUtil;
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
            wrapper.eq("operator_id", bean.getOperator_id());
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

}
