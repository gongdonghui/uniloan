package com.sup.cms.facade;

import com.sup.cms.bean.po.ReportCollectorBean;
import com.sup.cms.bean.vo.CollectorReportParam;
import com.sup.common.bean.CheckReportBean;
import com.sup.common.bean.CollectionReportBean;
import com.sup.common.bean.OperationReportBean;
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

    @ResponseBody
    @RequestMapping(value = "collection", produces = "application/json;charset=UTF-8")
    Result<List<CollectionReportBean>> collection(@RequestBody OperationReportParam param);

    @ResponseBody
    @RequestMapping(value = "collector", produces = "application/json;charset=UTF-8")
    Result<List<ReportCollectorBean>> collector(@RequestBody CollectorReportParam param);

}
