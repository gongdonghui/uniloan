package com.sup.core.report;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.sup.common.bean.OperationReportBean;
import com.sup.common.param.OperationReportParam;
import com.sup.common.util.Result;
import com.sup.core.mapper.OperationReportMapper;
import lombok.extern.log4j.Log4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
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


    @Override
    public Result<List<OperationReportBean>> operation(OperationReportParam param) {
        if (param == null) {
            return Result.fail("input  param is null");
        }
        List<OperationReportBean> ret = null;
        if (param.getChannel_id() < 0) {  //all  channel

            ret = operatioReportMapper.selectList(new QueryWrapper<OperationReportBean>()
                    .ge("data_dt", param.getStart_date())
                    .le("data_dt", param.getEnd_date()));
        } else {
            ret = operatioReportMapper.selectList(new QueryWrapper<OperationReportBean>()
                    .eq("channel_id", param.getChannel_id())
                    .ge("data_dt", param.getStart_date())
                    .le("data_dt", param.getEnd_date()));
        }
        if (ret == null) return Result.fail("not obtain  report  data");
        return Result.succ(ret);
    }

    @Override
    public Result check(OperationReportParam param) {
        return null;
    }

    @Override
    public Result collection(OperationReportParam param) {
        return null;
    }
}
