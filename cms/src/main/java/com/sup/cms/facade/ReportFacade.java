package com.sup.cms.facade;

import com.sup.cms.bean.vo.CollectorReportParam;
import com.sup.common.bean.*;
import com.sup.common.param.CheckOverviewParam;
import com.sup.common.param.OperationReportParam;
import com.sup.common.util.Result;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

/**
 * gongshuai
 * <p>
 * 2019/10/5
 */
@RequestMapping(value = "/report")
public interface ReportFacade {


    @ResponseBody
    @RequestMapping(value = "op", produces = "application/json;charset=UTF-8")
    Result<List<OperationReportBean>> operation(@RequestBody OperationReportParam param);

    @ResponseBody
    @RequestMapping(value = "check", produces = "application/json;charset=UTF-8")
    Result<List<CheckReportBean>> check(@RequestBody OperationReportParam param);

    /**
     * 数据概况   单日
     *
     * @param param
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "overall", produces = "application/json;charset=UTF-8")
    Result<OverallReportBean> overall(@RequestBody OperationReportParam param);

    /**
     * 数据概况  多日
     *
     * @param param
     * @return
     */
    @RequestMapping(value = "overallmul", produces = "application/json;charset=UTF-8")
    Result<OverallMultiReportBean> overallmul(@RequestBody OperationReportParam param);

    @ResponseBody
    @RequestMapping(value = "collection", produces = "application/json;charset=UTF-8")
    Result<List<CollectionReportBean>> collection(@RequestBody OperationReportParam param);

    /**
     * 催收日报
     *
     * @param param
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "collector", produces = "application/json;charset=UTF-8")
    String collector(@RequestBody CollectorReportParam param);

    /**
     * 运营日报
     *
     * @param param
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "operation", produces = "application/json;charset=UTF-8")
    String operationReport(@RequestBody OperationReportParam param);

    /**
     * 信审进度报表
     *
     * @param param
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "checkoverview", produces = "application/json;charset=UTF-8")
    Result<OperationStatBean> checkoverview(@RequestBody CheckOverviewParam param);

    /**
     * 信审员报表
     *
     * @param param
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "operator", produces = "application/json;charset=UTF-8")
    String operator(@RequestBody CheckOverviewParam param);

    /**
     * 信审员实时报表
     *
     * @param param
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "operator_cur", produces = "application/json;charset=UTF-8")
    Result<List<TbReportCheckOperatorDaily>> operator_cur(@RequestBody CheckOverviewParam param);
}
