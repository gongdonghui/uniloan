package com.sup.core.mapper;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.sup.common.bean.TbRepayPlanBean;
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

public interface RepayPlanInfoMapper extends BaseMapper<TbRepayPlanBean> {

    @Select("<script> select * from tb_repay_plan <if test='ew != null'> ${ew.customSqlSegment} </if> </script>")
    List<TbRepayPlanBean> getRepayPlan(@Param(Constants.WRAPPER) Wrapper<TbRepayPlanBean> ew);


}
