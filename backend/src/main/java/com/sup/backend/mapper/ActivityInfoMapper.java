package com.sup.backend.mapper;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.sup.backend.bean.ActivityInfoBean;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * Created by xidongzhou1 on 2019/8/30.
 */
public interface ActivityInfoMapper extends BaseMapper<ActivityInfoBean> {
  @Select("<script> select * from ${tableName} <if test='ew != null'> ${ew.customSqlSegment} </if> </script>")
  List<ActivityInfoBean> selectCus(@Param("tableName") String tn, @Param(Constants.WRAPPER) Wrapper<ActivityInfoBean> ew);
}
