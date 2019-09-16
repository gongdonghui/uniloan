package com.sup.core.facade;

import com.sup.common.bean.ApplyInfoParam;
import com.sup.common.bean.TbApplyInfoBean;
import com.sup.common.loan.ApplyMaterialTypeEnum;
import com.sup.common.util.Result;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Map;

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
    Result addApplyInfo(@RequestBody ApplyInfoParam applyInfoParam);

    // audit apply
    @ResponseBody
    @RequestMapping(value = "update", produces = "application/json;charset=UTF-8")
    Result updateApplyInfo(@RequestBody TbApplyInfoBean bean);

}
