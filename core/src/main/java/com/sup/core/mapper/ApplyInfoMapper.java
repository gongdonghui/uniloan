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

    @Select("select * from tb_apply_info\n" +
            " where user_id in (\n" +
            "  select distinct user_id\n" +
            "  from tb_user_citizen_identity_card_info uc\n" +
            "  where cid_no in (\n" +
            "    select distinct cid_no from tb_user_citizen_identity_card_info where user_id=#{userId}\n" +
            "  )\n" +
            " ) and status in (0,1,2,3,4,10,11,12,13,15,16)")
    List<TbApplyInfoBean> getApplyInprogress(@Param(value = "userId") Integer userId);

}
