package com.sup.core.mapper;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.sup.common.bean.TbApplyInfoBean;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface ApplyInfoMapper extends BaseMapper<TbApplyInfoBean> {

    @Select("<script> select * from apply_info <if test='ew != null'>${ew.customSqlSegment}</if> </script>")
    List<TbApplyInfoBean> selectApplyInfo(@Param(Constants.WRAPPER)Wrapper<TbApplyInfoBean> ew);
}
