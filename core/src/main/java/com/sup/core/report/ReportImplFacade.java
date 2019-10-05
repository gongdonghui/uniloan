package com.sup.core.report;

import com.sup.common.param.ReportParam;
import com.sup.common.util.Result;
import lombok.extern.log4j.Log4j;
import org.springframework.web.bind.annotation.RestController;

/**
 * gongshuai
 * <p>
 * 2019/10/5
 */
@RestController
@Log4j
public class ReportImplFacade implements ReportFacade {
    @Override
    public Result channel(ReportParam param) {
        return null;
    }

    @Override
    public Result operation(ReportParam param) {
        return null;
    }

    @Override
    public Result check(ReportParam param) {
        return null;
    }

    @Override
    public Result collection(ReportParam param) {
        return null;
    }
}
