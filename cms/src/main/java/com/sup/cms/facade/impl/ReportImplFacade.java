package com.sup.cms.facade.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.google.common.collect.Maps;
import com.sup.cms.bean.po.LoanStatBean;
import com.sup.cms.bean.po.RefuseStatBean;
import com.sup.cms.bean.po.ReportCollectorBean;
import com.sup.cms.bean.po.TbCollectionRecordBean;
import com.sup.cms.bean.vo.CollectorReportParam;
import com.sup.cms.facade.ReportFacade;
import com.sup.cms.mapper.*;
import com.sup.common.bean.*;
import com.sup.common.loan.ApplyStatusEnum;
import com.sup.common.loan.OperationTaskTypeEnum;
import com.sup.common.param.CheckOverviewParam;
import com.sup.common.param.OperationReportParam;
import com.sup.common.util.*;
import lombok.extern.log4j.Log4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;

/**
 * gongshuai
 * <p>
 * 2019/10/5
 */
@RestController
@Log4j
public class ReportImplFacade implements ReportFacade {

    @Autowired
    private OperationReportMapper operatioReportMapper;

    @Autowired
    private CheckReportMapper checkReportMapper;

    @Autowired
    private CollectionReportMapper collectionReportMapper;

    @Autowired
    private CrazyJoinMapper crazyJoinMapper;

    @Autowired
    private TbApplyInfoMapper applyInfoMapper;

    @Autowired
    private ReportOperatorDailyMapper reportOperatorDailyMapper;

    @Autowired
    private AuthUserBeanMapper authUserBeanMapper;


    @Override
    public Result<List<OperationReportBean>> operation(OperationReportParam param) {
        if (param == null) {
            return Result.fail("input  param is null");
        }
        QueryWrapper<OperationReportBean> wrapper = new QueryWrapper<>();
        wrapper.ge("data_dt", DateUtil.startOf(param.getStart_date()));
        wrapper.le("data_dt", DateUtil.endOf(param.getEnd_date()));
        if (param.getChannel_id() < 0) {  //all  channel
            wrapper.orderByDesc("data_dt", "channel_id");
        } else {
            wrapper.eq("channel_id", param.getChannel_id());
            wrapper.orderByDesc("data_dt");
        }
        log.info("[SQL] operation report=" + wrapper.getSqlSegment());
        List<OperationReportBean> ret = operatioReportMapper.selectList(wrapper);
        if (ret == null) return Result.fail("not obtain  report  data");
        return Result.succ(ret);
    }

    @Override
    public Result<List<CheckReportBean>> check(OperationReportParam param) {
        if (param == null) {
            return Result.fail("input param is null");
        }
        QueryWrapper<CheckReportBean> wrapper = new QueryWrapper<>();
        wrapper.ge("data_dt", DateUtil.startOf(param.getStart_date()));
        wrapper.le("data_dt", DateUtil.endOf(param.getEnd_date()));
        wrapper.orderByDesc("data_dt");
        List<CheckReportBean> ret = this.checkReportMapper.selectList(wrapper);
        return Result.succ(ret);
    }

    @Override
    public Result<OverallReportBean> overall(OperationReportParam param) {

        if (param == null || param.getStart_date() == null) {
            log.info("get  overall report input param  isnull");
            return Result.fail("input para is null");

        }
        QueryWrapper<OperationReportBean> wrapper = new QueryWrapper<>();
        wrapper.ge("data_dt", DateUtil.startOf(param.getStart_date()));
        wrapper.le("data_dt", DateUtil.endOf(param.getStart_date()));
        List<OperationReportBean> operationReportBeans = this.operatioReportMapper.selectList(wrapper);
        OverallReportBean overallReportBean = new OverallReportBean();
        int apply_num = 0;
        int auto_pass = 0;
        int first_pass = 0;
        int final_pass = 0;
        int loan_num = 0;
        int loan_failed = 0;
        int loan_pending = 0;
        int register = 0;

        for (OperationReportBean operationReportBean : operationReportBeans) {
            apply_num += operationReportBean.getApply_num();
            auto_pass += operationReportBean.getAuto_pass();
            first_pass += operationReportBean.getFirst_pass();
            final_pass += operationReportBean.getFinal_pass();
            loan_num += operationReportBean.getLoan_num();
            loan_failed = operationReportBean.getLoan_failed() == null ? 0 : operationReportBean.getLoan_failed();
            loan_pending = operationReportBean.getLoan_pending() == null ? 0 : operationReportBean.getLoan_pending();
            register += operationReportBean.getRegister();
        }
        overallReportBean.setApply_num(apply_num);
        overallReportBean.setAuto_pass(auto_pass);
        overallReportBean.setFirst_pass(first_pass);
        overallReportBean.setFinal_pass(final_pass);
        overallReportBean.setLoan_num(loan_num);
        overallReportBean.setLoan_failed(loan_failed);
        overallReportBean.setLoan_pending(loan_pending);
        overallReportBean.setRegister(register);

        int first_check = 0;
        int first_pending = 0;
        int final_check = 0;
        int final_pending = 0;
        int final_total = 0;
        int first_total = 0;

        QueryWrapper<CheckReportBean> wrapper2 = new QueryWrapper<>();
        wrapper2.ge("data_dt", DateUtil.startOf(param.getStart_date()));
        wrapper2.le("data_dt", DateUtil.endOf(param.getStart_date()));
        wrapper2.orderByDesc("data_dt");
        List<CheckReportBean> checkReportBeans = this.checkReportMapper.selectList(wrapper2);
        for (CheckReportBean checkReportBean : checkReportBeans) {
            if (checkReportBean.getTask_type() == OperationTaskTypeEnum.TASK_FIRST_AUDIT.getCode()) {
                first_total += checkReportBean.getTotal();
                first_check += checkReportBean.getChecked();


            } else if (checkReportBean.getTask_type() == OperationTaskTypeEnum.TASK_FINAL_AUDIT.getCode()) {
                final_total += checkReportBean.getTotal();
                final_check += checkReportBean.getChecked();
            }

        }
        first_pending = first_total - first_check;
        final_pending = final_total - final_check;

        overallReportBean.setFirst_total(first_total);
        overallReportBean.setFirst_check(first_check);
        overallReportBean.setFirst_pending(first_pending);
        overallReportBean.setFinal_total(final_total);
        overallReportBean.setFinal_check(final_check);
        overallReportBean.setFinal_pending(final_pending);

        return Result.succ(overallReportBean);
    }

    /**
     * 数据概况  多日
     *
     * @param param
     * @return
     */
    @Override
    public Result<OverallMultiReportBean> overallmul(OperationReportParam param) {

        if (param != null && param.getStart_date() != null
                && param.getEnd_date() != null) {

            OverallMultiReportBean ret = new OverallMultiReportBean();
            QueryWrapper<OperationReportBean> wrapper = new QueryWrapper<>();
            String start_str = DateUtil.startOf(param.getStart_date());
            String end_str = DateUtil.startOf(param.getEnd_date());
            wrapper.ge("data_dt", start_str);
            wrapper.le("data_dt", end_str);


            int apply_num = 0;
            int auto_pass = 0;
            int manual_pass = 0;
            int loan_num = 0;
            int loan_amt = 0;
            int repay_amt = 0;
            int repay_num = 0;
            int repay_actual_num = 0;
            int repay_actual_amt = 0;
            int fr_num = 0;
            int fr_amt = 0;
            List<OperationReportBean> operationReportBeans = this.operatioReportMapper.selectList(wrapper);
            for (OperationReportBean operationReportBean : operationReportBeans) {


                apply_num += operationReportBean.getApply_num();
                auto_pass += operationReportBean.getAuto_pass();
                manual_pass += operationReportBean.getManual_pass();
                loan_num += operationReportBean.getLoan_num();
                loan_amt += operationReportBean.getLoan_amt();
                repay_amt += operationReportBean.getRepay_amt();
                repay_num += operationReportBean.getRepay();
                repay_actual_amt += operationReportBean.getRepay_actual_amt();
                repay_actual_num += operationReportBean.getRepay_actual();
                fr_num += operationReportBean.getFirst_overdue();
                fr_amt += operationReportBean.getFirst_overdue_amt();
            }

            ret.setApply(apply_num);
            ret.setAuto_pass(auto_pass);
            ret.setManual_pass(manual_pass);
            ret.setLoan_amt(loan_amt);
            ret.setLoan_num(loan_num);
            double ctr = apply_num > 0 ? ((loan_num + 0.001f) / (apply_num + 0.001f)) : 0;
            ret.setLoan_ctr(ctr);

            ret.setRepay_num(repay_num);
            ret.setRepay_actual_num(repay_actual_num);
            ret.setRepay_amt(repay_amt);
            ret.setRepay_actual_amt(repay_actual_amt);
            double repay_rate = repay_num > 0 ? ((repay_actual_num + 0.001f) / (repay_num + 0.001f)) : 0;
            ret.setRepay_rate(repay_rate);

            ret.setFr_num(fr_num);
            ret.setFr_amt(fr_amt);
            double fr_rate = loan_num > 0 ? ((fr_num + 0.001f) / (loan_num + 0.001f)) : 0;
            ret.setFr_rate(fr_rate);

            //TODO   add  collection info


            QueryWrapper<TbApplyInfoBean> applyInfoWrapper = new QueryWrapper<>();
            applyInfoWrapper.or(w -> w.ge("loan_time", start_str).le("loan_time", end_str));
            applyInfoWrapper.in("status"
                    , ApplyStatusEnum.APPLY_LOAN_SUCC.getCode()
                    , ApplyStatusEnum.APPLY_OVERDUE.getCode()
            );
            int in_loan = 0;
            int in_loan_amt = 0;
            int delinquency_amt = 0;
            int delinquency_num = 0;

            List<TbApplyInfoBean> infos = this.applyInfoMapper.selectList(applyInfoWrapper);
            for (TbApplyInfoBean applyInfoBean : infos) {
                if (applyInfoBean.getStatus() == ApplyStatusEnum.APPLY_LOAN_SUCC.getCode()) {
                    in_loan_amt += applyInfoBean.getGrant_quota();
                    ++in_loan;
                }
                if (applyInfoBean.getStatus() == ApplyStatusEnum.APPLY_OVERDUE.getCode()) {
                    delinquency_amt += applyInfoBean.getGrant_quota();
                    ++delinquency_num;
                }
            }
            ret.setIn_loan_num(in_loan);
            ret.setIn_loan_amt(in_loan_amt);
            ret.setDelinquency_amt(delinquency_amt);
            ret.setDelinquency_num(delinquency_num);
            double dq_rate = loan_num > 0 ? ((delinquency_num + 0.001f) / (loan_num + 0.001f)) : 0;
            ret.setDelinquency_rate(dq_rate);

            return Result.succ(ret);
        } else {
            return Result.fail("input param is null");
        }
    }

    @Override
    public Result<List<CollectionReportBean>> collection(OperationReportParam param) {
        if (param == null) {
            return Result.fail("input param is null");

        }
        QueryWrapper<CollectionReportBean> wrapper = new QueryWrapper<>();
        wrapper.ge("data_dt", DateUtil.startOf(param.getStart_date()));
        wrapper.le("data_dt", DateUtil.endOf(param.getEnd_date()));
        wrapper.orderByDesc("data_dt");
        List<CollectionReportBean> ret = this.collectionReportMapper.selectList(wrapper);

        return Result.succ(ret);
    }

    @Override
    public String collector(CollectorReportParam param) {
        log.info("Report collector param:" + GsonUtil.toJson(param));
        StringBuilder sb = new StringBuilder();
        if (param.getStartDate() != null) {
            sb.append(" and data_dt>='" + DateUtil.formatDate(param.getStartDate()) + "'");
        }
        if (param.getEndDate() != null) {
            sb.append(" and data_dt<='" + DateUtil.formatDate(param.getEndDate()) + "'");
        }
        Integer offset = (param.getPage() - 1) * param.getPageSize();
        Integer rows = param.getPageSize();
        List<ReportCollectorBean> result;
        Integer resultCount = 0;
        if (param.getOperatorId() != null) {
            sb.append(" and operator_id=" + param.getOperatorId());
            result = crazyJoinMapper.getCollectorReport(sb.toString(), offset, rows);
            resultCount = crazyJoinMapper.getCollectorReportCount(sb.toString());
        } else {
            result = crazyJoinMapper.getCollectorReportAll(sb.toString(), offset, rows);
            resultCount = crazyJoinMapper.getCollectorReportAllCount(sb.toString());
        }
        for (ReportCollectorBean bean : result) {
            Integer totalNum = bean.getTaskNum();
            Float rate = 0F;
            if (totalNum > 0) {
                rate = (float) (bean.getCollectNum() + bean.getPartialCollectNum()) / totalNum;
            }
            bean.setCollectRate(rate);
        }
        Map m = Maps.newHashMap();
        m.put("total", resultCount);
        m.put("list", result);
        return ResponseUtil.success(m);
    }

    /**
     * 运营日报
     *
     * @param param
     * @return
     */
    @Override
    public String operationReport(OperationReportParam param) {
        StringBuilder sb = new StringBuilder();
        if (param.getStart_date() != null) {
            sb.append(" and loan_time>='" + DateUtil.startOf(param.getStart_date()) + "'");
        }
        if (param.getEnd_date() != null) {
            sb.append(" and loan_time<='" + DateUtil.endOf(param.getEnd_date()) + "'");
        }
        if (param.getChannel_id() != null && param.getChannel_id() >= 0) {
            sb.append(" and channel_id=" + param.getChannel_id());
        }
        if (param.getProduct_id() != null && param.getProduct_id() >= 0) {
            sb.append(" and product_id=" + param.getProduct_id());
        }

        log.info("operationReport conditions=" + sb.toString());
        Integer offset = (param.getPage() - 1) * param.getPageSize();
        Integer rows = param.getPageSize();
        List<LoanStatBean> l = crazyJoinMapper.getOperationReport(sb.toString(), offset, rows);
        Map m = Maps.newHashMap();
        m.put("total", crazyJoinMapper.getOperationReportCount(sb.toString()));
        m.put("list", l);
        return ResponseUtil.success(m);
    }

    /**
     * 信审进度报表
     *
     * @param param
     * @return
     */
    @Override
    public Result<OperationStatBean> checkoverview(CheckOverviewParam param) {

        if (param != null && param.getStart_date() != null && param.getEnd_date() != null && param.getType() != null) {

            String start_str = DateUtil.startOf(param.getStart_date());
            String end_str = DateUtil.endOf(param.getEnd_date());
            String checkType = param.getType();
            Integer taskType = -1;


            if (checkType.equals("first")) {
                taskType = OperationTaskTypeEnum.TASK_FIRST_AUDIT.getCode();


            } else if (checkType.equals("final")) {
                taskType = OperationTaskTypeEnum.TASK_FINAL_AUDIT.getCode();

            } else {

                return Result.fail("input check type invalid");
            }
            List<OperationTaskJoinBean> operationTaskJoinBeanList = this.crazyJoinMapper.getOperationTaskJoinByTask(start_str, end_str, taskType);
            OperationStatBean operationStatBean = CheckStatUtil.processList(operationTaskJoinBeanList, taskType);

            return Result.succ(operationStatBean);

        } else {
            return Result.fail("input param is null");
        }

    }

    /**
     * 信审员报表
     *
     * @param param
     * @return
     */
    @Override
    public String operator(CheckOverviewParam param) {
        if (param != null && param.getStart_date() != null && param.getEnd_date() != null) {
            String start_str = DateUtil.startOf(param.getStart_date());
            String end_str = DateUtil.endOf(param.getEnd_date());

            String operator = param.getOperator_id();

            Integer offset = (param.getPage() - 1) * param.getPageSize();
            Integer rows = param.getPageSize();

            QueryWrapper<TbReportCheckOperatorDaily> wrapper = new QueryWrapper<>();
            wrapper.le("data_dt", end_str);
            wrapper.ge("data_dt", start_str);
            StringBuilder conditions = new StringBuilder();

            if (operator != null && !operator.isEmpty()) {
                try {
                    Integer val = Integer.parseInt(operator);
                    wrapper.eq("operator", val);
                    conditions.append(" and   operator = ");
                    conditions.append(val);
                } catch (Exception e) {
                    log.info("operator is invalid integer");
                }
            }

            Integer total = this.reportOperatorDailyMapper.selectCount(wrapper);

            List<TbReportCheckOperatorDaily> tbReportCheckOperatorDailyList = this.crazyJoinMapper.getOperatorReport(start_str, end_str, conditions.toString(), offset, rows);
            Map m = Maps.newHashMap();
            m.put("total", total);
            m.put("list", tbReportCheckOperatorDailyList);

            return ResponseUtil.success(m);

        } else {
            return ResponseUtil.failed("input param is null");
        }
    }


    private List<TbReportCheckOperatorDaily> buildOperatorReport(List<OperationTaskJoinBean> list, Integer taskType, Map<Integer, String> names, Date data_dt) {
        List<TbReportCheckOperatorDaily> ret = new ArrayList<>();
        Map<Integer, List<OperationTaskJoinBean>> map = new HashMap<>();
        for (OperationTaskJoinBean operationTaskJoinBean : list) {
            Integer operator = operationTaskJoinBean.getOperatorId();
            if (!map.containsKey(operator)) {
                map.put(operator, new ArrayList<>());
            }
            map.get(operator).add(operationTaskJoinBean);
        }
        for (Integer operator : map.keySet()) {
            if (map.get(operator).isEmpty()) {
                continue;
            }
            OperationStatBean operationStatBean = CheckStatUtil.processList(map.get(operator), taskType);
            TbReportCheckOperatorDaily tbReportCheckOperatorDaily = new TbReportCheckOperatorDaily();
            tbReportCheckOperatorDaily.setData_dt(data_dt);
            tbReportCheckOperatorDaily.setOperator(operator);
            tbReportCheckOperatorDaily.setChecked(operationStatBean.getChecked());
            tbReportCheckOperatorDaily.setPassed(operationStatBean.getPassed());
            tbReportCheckOperatorDaily.setAllocated(operationStatBean.getAllocated());
            tbReportCheckOperatorDaily.setLoan_num(operationStatBean.getLoan_num());
            tbReportCheckOperatorDaily.setLoan_amt(operationStatBean.getLoan_amt());
            tbReportCheckOperatorDaily.setPass_rate(operationStatBean.getPass_rate());
            tbReportCheckOperatorDaily.setLoan_rate(operationStatBean.getLoan_rate());
            tbReportCheckOperatorDaily.setUpdate_time(new Date());
            if (names != null && names.containsKey(operator)) {
                tbReportCheckOperatorDaily.setOperator_name(names.get(operator));
            } else {
                continue;
            }
            ret.add(tbReportCheckOperatorDaily);
        }
        return ret;


    }

    /**
     * 信审员实时报表
     *
     * @param param
     * @return
     */
    @Override
    public Result<List<TbReportCheckOperatorDaily>> operator_cur(CheckOverviewParam param) {

        Date cur = new Date();
        String start_str = DateUtil.startOf(cur);
        String end_str = DateUtil.endOf(cur);
        List<TbReportCheckOperatorDaily> ret = new ArrayList<>();

        List<AuthUserBean> users = this.authUserBeanMapper.selectList(new QueryWrapper<AuthUserBean>());
        Map<Integer, String> names = new HashMap<Integer, String>();
        for (AuthUserBean authUserBean : users) {
            names.put(authUserBean.getId(), authUserBean.getName());

        }
        List<OperationTaskJoinBean> list = this.crazyJoinMapper.getOperationTaskJoin(start_str, end_str, OperationTaskTypeEnum.TASK_FIRST_AUDIT.getCode());
        List<TbReportCheckOperatorDaily> first_ret = this.buildOperatorReport(list, OperationTaskTypeEnum.TASK_FIRST_AUDIT.getCode(), names, cur);
        if (!first_ret.isEmpty())
            ret.addAll(first_ret);  //初审统计

        List<OperationTaskJoinBean> final_list = this.crazyJoinMapper.getOperationTaskJoin(start_str, end_str, OperationTaskTypeEnum.TASK_FINAL_AUDIT.getCode());
        List<TbReportCheckOperatorDaily> final_ret = this.buildOperatorReport(final_list, OperationTaskTypeEnum.TASK_FINAL_AUDIT.getCode(), names, cur);
        if (!final_ret.isEmpty())
            ret.addAll(final_ret);   //终审统计

        return Result.succ(ret);

    }

    /**
     * 拒贷统计
     *
     * @param param
     * @return
     */
    @Override
    public Result<List<RefuseStatBean>> refuse_stat(CheckOverviewParam param) {


        if (param != null && param.getStart_date() != null && param.getEnd_date() != null) {
            String start_str = DateUtil.startOf(param.getStart_date());
            String end_str = DateUtil.endOf(param.getEnd_date());
            List<RefuseStatBean> ret = this.crazyJoinMapper.getRefuseStat(start_str, end_str);
            return Result.succ(ret);
        } else {

            return Result.fail("input param invalid");
        }
    }
}
