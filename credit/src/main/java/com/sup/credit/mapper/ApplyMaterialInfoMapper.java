package com.sup.credit.mapper;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.sup.credit.bean.ApplyMaterialInfoBean;
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

public interface ApplyMaterialInfoMapper extends BaseMapper<ApplyMaterialInfoBean> {
    @Select("<script> select * from apply_material_info <if test='ew != null'>${ew.customSqlSegment}</if> </script>")
    List<ApplyMaterialInfoBean> selectApplyMaterialInfo(@Param(Constants.WRAPPER)Wrapper<ApplyMaterialInfoBean> ew);
}
