package com.sup.cms.facade.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.sup.cms.facade.ReportFacade;
import com.sup.cms.mapper.CheckReportMapper;
import com.sup.cms.mapper.CollectionReportMapper;
import com.sup.cms.mapper.OperationReportMapper;
import com.sup.common.bean.CheckReportBean;
import com.sup.common.bean.CollectionReportBean;
import com.sup.common.bean.OperationReportBean;
import com.sup.common.param.OperationReportParam;
import com.sup.common.util.Result;
import lombok.extern.log4j.Log4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

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


    @Override
    public Result<List<OperationReportBean>> operation(OperationReportParam param) {
        if (param == null) {
            return Result.fail("input  param is null");
        }
        List<OperationReportBean> ret = null;
        if (param.getChannel_id() < 0) {  //all  channel

            ret = operatioReportMapper.selectList(new QueryWrapper<OperationReportBean>()
                    .ge("data_dt", param.getStart_date())
                    .le("data_dt", param.getEnd_date())
                    .orderByDesc("data_dt", "channel_id"));
        } else {
            ret = operatioReportMapper.selectList(new QueryWrapper<OperationReportBean>()
                    .eq("channel_id", param.getChannel_id())
                    .ge("data_dt", param.getStart_date())
                    .le("data_dt", param.getEnd_date())
                    .orderByDesc("data_dt"));
        }
        if (ret == null) return Result.fail("not obtain  report  data");
        return Result.succ(ret);
    }

    @Override
    public Result<List<CheckReportBean>> check(OperationReportParam param) {
        if (param == null) {
            return Result.fail("input param is null");

        }
        List<CheckReportBean> ret = this.checkReportMapper.selectList(new QueryWrapper<CheckReportBean>()
                        .ge("data_dt", param.getStart_date())
                        .le("data_dt", param.getEnd_date())
                        .orderByDesc("data_dt"));
        return Result.succ(ret);
    }

    @Override
    public Result<List<CollectionReportBean>> collection(OperationReportParam param) {
        if (param == null) {
            return Result.fail("input param is null");

        }
        List<CollectionReportBean> ret = this.collectionReportMapper.selectList(new QueryWrapper<CollectionReportBean>()
                .ge("data_dt", param.getStart_date())
                .le("data_dt", param.getEnd_date())
                .orderByDesc("data_dt"));

        return Result.succ(ret);
    }
}
