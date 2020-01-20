package com.sup.cms.facade.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.google.common.collect.Maps;
import com.sup.cms.bean.po.LoanStatBean;
import com.sup.cms.bean.po.ReportCollectorBean;
import com.sup.cms.bean.vo.CollectorReportParam;
import com.sup.cms.facade.ReportFacade;
import com.sup.cms.mapper.CheckReportMapper;
import com.sup.cms.mapper.CollectionReportMapper;
import com.sup.cms.mapper.CrazyJoinMapper;
import com.sup.cms.mapper.OperationReportMapper;
import com.sup.common.bean.CheckReportBean;
import com.sup.common.bean.CollectionReportBean;
import com.sup.common.bean.OperationReportBean;
import com.sup.common.param.OperationReportParam;
import com.sup.common.util.DateUtil;
import com.sup.common.util.GsonUtil;
import com.sup.common.util.ResponseUtil;
import com.sup.common.util.Result;
import lombok.extern.log4j.Log4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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
        for(ReportCollectorBean bean : result) {
            Integer totalNum = bean.getTaskNum();
            Float rate = 0F;
            if (totalNum > 0) {
                rate = (float)(bean.getCollectNum() + bean.getPartialCollectNum())/totalNum;
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
        m.put("total",crazyJoinMapper.getOperationReportCount(sb.toString()));
        m.put("list",l);
        return ResponseUtil.success(m);
    }
}
