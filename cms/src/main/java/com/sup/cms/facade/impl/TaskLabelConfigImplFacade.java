package com.sup.cms.facade.impl;

import com.sup.cms.facade.TaskLabelConfigFacade;
import com.sup.common.param.GetLabelParam;
import com.sup.common.param.TaskLabelParam;
import com.sup.common.util.Result;
import lombok.extern.log4j.Log4j;
import org.springframework.web.bind.annotation.RestController;

/**
 * gongshuai
 * <p>
 * 2019/10/14
 */

@RestController
@Log4j
public class TaskLabelConfigImplFacade implements TaskLabelConfigFacade {
    @Override
    public Result addLabel(TaskLabelParam param) {
        return null;
    }

    @Override
    public Result getLabel(GetLabelParam param) {
        return null;
    }
}
