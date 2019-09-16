package com.sup.core.mapper;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.sup.common.bean.TbApplyMaterialInfoBean;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * Project:uniloan
 * Class:  ApplyMaterialInfoMapper
 * <p>
 * Author: guanfeng
 * Create: 2019-09-05
 */

public interface ApplyMaterialInfoMapper extends BaseMapper<TbApplyMaterialInfoBean> {
    @Select("<script> select * from apply_material_info <if test='ew != null'>${ew.customSqlSegment}</if> </script>")
    List<TbApplyMaterialInfoBean> selectApplyMaterialInfo(@Param(Constants.WRAPPER)Wrapper<TbApplyMaterialInfoBean> ew);
}
