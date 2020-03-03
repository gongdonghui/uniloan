package com.sup.cms.facade.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.sup.common.bean.OperationTaskConfigBean;
import com.sup.cms.bean.vo.GetOptConfigParams;
import com.sup.cms.facade.OperationTaskConfigFacade;
import com.sup.cms.mapper.OperationTaskConfigBeanMapper;
import com.sup.common.util.Result;
import lombok.extern.log4j.Log4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * gongshuai
 * <p>
 * 2020/2/22
 */
@Log4j
@RestController
public class OperationTaskConigImplFacade implements OperationTaskConfigFacade {
    @Autowired
    private OperationTaskConfigBeanMapper operationTaskConfigBeanMapper;

    /**
     * 新建配置
     *
     * @param operationTaskConfigBean
     * @return
     */
    @Override
    public Result create(OperationTaskConfigBean operationTaskConfigBean) {
        if (operationTaskConfigBean != null) {
            this.operationTaskConfigBeanMapper.insert(operationTaskConfigBean);
            return Result.succ();


        } else {
            return Result.fail("input error");
        }
    }

    /**
     * 删除配置
     *
     * @param configId
     * @return
     */
    @Override
    public Result delete(Integer configId) {
        if (configId != null) {
            this.operationTaskConfigBeanMapper.deleteById(configId);
            return Result.succ();

        } else {
            return Result.fail("input error");
        }

    }

    /**
     * 修改配置
     *
     * @param operationTaskConfigBean
     * @return
     */
    @Override
    public Result edit(OperationTaskConfigBean operationTaskConfigBean) {
        if (operationTaskConfigBean != null) {
            this.operationTaskConfigBeanMapper.updateById(operationTaskConfigBean);
            return Result.succ();

        } else {
            return Result.fail("input error");
        }
    }

    /**
     * 查询配置
     *
     * @param getOptConfigParams
     * @return
     */
    @Override
    public Result<List<OperationTaskConfigBean>> list(GetOptConfigParams getOptConfigParams) {

        if (getOptConfigParams != null) {
            QueryWrapper<OperationTaskConfigBean> queryWrapper = new QueryWrapper<>();
            if (getOptConfigParams.getAssetLevel() != null) {
                queryWrapper.eq("asset_level", getOptConfigParams.getAssetLevel());
            }
            if (getOptConfigParams.getGroup() != null) {
                queryWrapper.eq("group", getOptConfigParams.getGroup());
            }

            List<OperationTaskConfigBean> list = this.operationTaskConfigBeanMapper.selectList(queryWrapper);

            return Result.succ(list);


        } else {
            return Result.fail("input error");
        }


    }
}
