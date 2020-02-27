package com.sup.core.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.sup.common.bean.TbApplyInfoBean;
import com.sup.core.mapper.ApplyInfoMapper;
import com.sup.core.mapper.AuthUserGroupBeanMapper;
import com.sup.core.mapper.OperationTaskConfigBeanMapper;
import lombok.extern.log4j.Log4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * gongshuai
 * <p>
 * 2020/2/23
 */
@Service
@Log4j
public class OperationTaskAssignService {


    @Autowired
    private OperationTaskConfigBeanMapper operationTaskConfigBeanMapper;

    @Autowired
    private ApplyInfoMapper   applyInfoMapper;


    public void assignTask(Integer creiditLevel) {

        List<Integer> operatorList = this.operationTaskConfigBeanMapper.getOperatorsByLevel(creiditLevel);
        QueryWrapper<TbApplyInfoBean>     queryWrapper  =  new QueryWrapper<>();
        queryWrapper.eq("credit_level", creiditLevel);
        List<TbApplyInfoBean>  applyInfoBeanList   = this.applyInfoMapper.selectList(queryWrapper);




    }
}
