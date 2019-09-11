package com.sup.core.mapper;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.sup.core.bean.RepayPlanInfoBean;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * Project:uniloan
 * Class:  RepayPlanInfoMapper
 * <p>
 * Author: guanfeng
 * Create: 2019-09-09
 */

public interface RepayPlanInfoMapper extends BaseMapper<RepayPlanInfoBean> {

    @Select("<script> select * from ${tableName} <if test='ew != null'> ${ew.customSqlSegment} </if> </script>")
    List<RepayPlanInfoBean> selectRepayPlan(@Param("tableName") String tn, @Param(Constants.WRAPPER) Wrapper<RepayPlanInfoBean> ew);

}
