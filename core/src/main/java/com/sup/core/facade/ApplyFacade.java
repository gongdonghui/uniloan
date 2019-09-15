package com.sup.core.facade;

import com.sup.common.bean.TbApplyInfoBean;
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

@RequestMapping(value = "/apply")
public interface ApplyFacade {

    // add apply
    @ResponseBody
    @RequestMapping(value = "add", produces = "application/json;charset=UTF-8")
    Object addApplyInfo(@RequestBody TbApplyInfoBean bean);

    // audit apply
    @ResponseBody
    @RequestMapping(value = "update", produces = "application/json;charset=UTF-8")
    Object updateApplyInfo(@RequestBody TbApplyInfoBean bean);

}
