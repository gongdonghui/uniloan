package com.sup.core.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.sup.common.bean.TbApplyQuickpassRulesBean;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

public interface ApplyQuickpassRulesMapper extends BaseMapper<TbApplyQuickpassRulesBean> {

    @Select("select count(id) from tb_apply_info where user_id=#{userId} and status=#{status}")
    public Integer getUserApplyCount(Integer userId, Integer status);

}
