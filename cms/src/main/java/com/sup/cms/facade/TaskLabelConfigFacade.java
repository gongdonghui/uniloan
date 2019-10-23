package com.sup.cms.facade;

import com.sup.common.param.GetLabelParam;
import com.sup.common.param.TaskLabelParam;
import com.sup.common.util.Result;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * gongshuai
 * <p>
 * 2019/10/14
 */

@RequestMapping(value = "/taskLabel")
public interface TaskLabelConfigFacade {
    @ResponseBody
    @RequestMapping(value = "addLabel", produces = "application/json;charset=UTF-8")
    Result addLabel(@RequestBody TaskLabelParam param);
    @ResponseBody
    @RequestMapping(value = "getLabel", produces = "application/json;charset=UTF-8")
    Result getLabel(@RequestBody GetLabelParam param);
}
