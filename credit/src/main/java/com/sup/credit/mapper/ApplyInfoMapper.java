package com.sup.credit.mapper;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.sup.credit.bean.ApplyInfoBean;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface ApplyInfoMapper extends BaseMapper<ApplyInfoBean> {

    @Select("<script> select * from apply_info <if test='ew != null'>${ew.customSqlSegment}</if> </script>")
    List<ApplyInfoBean> selectApplyInfo(@Param(Constants.WRAPPER)Wrapper<ApplyInfoBean> ew);
}
