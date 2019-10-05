package com.sup.core.report;

import com.sup.common.param.ReportParam;
import com.sup.common.util.Result;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * gongshuai
 * <p>
 * 2019/10/5
 */
@RequestMapping(value = "/report")
public interface ReportFacade {
    @ResponseBody
    @RequestMapping(value = "channel", produces = "application/json;charset=UTF-8")
    Result channel(@RequestBody ReportParam param);

    @ResponseBody
    @RequestMapping(value = "op", produces = "application/json;charset=UTF-8")
    Result operation(@RequestBody ReportParam param);

    @ResponseBody
    @RequestMapping(value = "check", produces = "application/json;charset=UTF-8")
    Result check(@RequestBody ReportParam param);

    @ResponseBody
    @RequestMapping(value = "collection", produces = "application/json;charset=UTF-8")
    Result collection(@RequestBody ReportParam param);
}
