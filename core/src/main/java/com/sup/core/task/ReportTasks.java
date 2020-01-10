package com.sup.core.task;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sup.common.bean.*;
import com.sup.common.bean.paycenter.PayStatusInfo;
import com.sup.common.bean.paycenter.RepayStatusInfo;
import com.sup.common.bean.paycenter.vo.PayStatusVO;
import com.sup.common.bean.paycenter.vo.RepayStatusVO;
import com.sup.common.loan.*;
import com.sup.common.service.PayCenterService;
import com.sup.common.util.DateUtil;
import com.sup.common.util.FunpayOrderUtil;
import com.sup.common.util.GsonUtil;
import com.sup.common.util.Result;
import com.sup.core.bean.CollectionRepayBean;
import com.sup.core.bean.CollectionStatBean;
import com.sup.core.bean.OperationTaskJoinBean;
import com.sup.core.bean.RiskDecisionResultBean;
import com.sup.core.mapper.*;
import com.sup.core.param.AutoDecisionParam;
import com.sup.core.service.ApplyService;
import com.sup.core.service.LoanService;
import com.sup.core.service.RuleConfigService;
import com.sup.core.service.impl.DecisionEngineImpl;
import com.sup.core.status.DecisionEngineStatusEnum;
import com.sup.core.util.MqMessenger;
import lombok.extern.log4j.Log4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
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


    /**
     * 更新催收明细表(tb_report_overdue_detail)
     */
    @Scheduled(cron = "0 0 */1 * * ?")
    public void updateOverdueDetail() {

    }

}
