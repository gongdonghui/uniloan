package com.sup.cms.facade;

import com.sup.cms.bean.vo.ConfigIdParam;
import com.sup.common.bean.OperationTaskConfigBean;
import com.sup.cms.bean.vo.GetOptConfigParams;
import com.sup.common.util.Result;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

/**
 * gongshuai
 * <p>
 * 2020/2/22
 */
@RequestMapping(value = "/opt_config")
public interface OperationTaskConfigFacade {
    /**
     *  新建配置
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "create", produces = "application/json;charset=UTF-8")
    Result create(@RequestBody OperationTaskConfigBean operationTaskConfigBean);

    /**
     * 删除配置
      * @param configId
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "delete", produces = "application/json;charset=UTF-8")
    Result delete(@RequestBody ConfigIdParam param);

    /**
     * 修改配置
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "edit", produces = "application/json;charset=UTF-8")
    Result edit(@RequestBody OperationTaskConfigBean operationTaskConfigBean);

    /**
     * 查询配置
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "list", produces = "application/json;charset=UTF-8")
    Result<List<OperationTaskConfigBean>> list(@RequestBody GetOptConfigParams getOptConfigParams);
}
