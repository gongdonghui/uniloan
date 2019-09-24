package com.sup.backend.mapper;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.sup.common.bean.TbApplyInfoBean;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * Created by xidongzhou1 on 2019/8/30.
 */
public interface TbApplyInfoMapper extends BaseMapper<TbApplyInfoBean> {
  @Select("<script> select * from ${tableName} <if test='ew != null'> ${ew.customSqlSegment} </if> </script>")
  List<TbApplyInfoBean> selectCus(@Param("tableName") String tn, @Param(Constants.WRAPPER) Wrapper<TbApplyInfoBean> ew);
}
