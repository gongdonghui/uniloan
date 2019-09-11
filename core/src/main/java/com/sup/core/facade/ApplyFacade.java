package com.sup.core.facade;

import com.sup.core.bean.ApplyInfoBean;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Project:uniloan
 * Class:  ApplyFacade
 * <p>
 * Author: guanfeng
 * Create: 2019-09-10
 */

public interface ApplyFacade {

    // audit apply
    @ResponseBody
    @RequestMapping(value = "update", produces = "application/json;charset=UTF-8")
    Object updateApplyInfo(String operator, @RequestBody ApplyInfoBean bean);

}
