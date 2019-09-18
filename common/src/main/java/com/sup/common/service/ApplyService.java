package com.sup.common.service;

import com.sup.common.bean.TbApplyInfoBean;
import com.sup.common.param.ApplyInfoParam;
import com.sup.common.util.Result;
import org.springframework.cloud.openfeign.FeignClient;
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

@FeignClient(value = "service-core")
@RequestMapping(value = "/apply")
public interface ApplyService {

    // add apply
    @ResponseBody
    @RequestMapping(value = "add", produces = "application/json;charset=UTF-8")
    Result addApplyInfo(@RequestBody ApplyInfoParam applyInfoParam);

    // audit apply
    @ResponseBody
    @RequestMapping(value = "update", produces = "application/json;charset=UTF-8")
    Result updateApplyInfo(@RequestBody TbApplyInfoBean bean);

}
