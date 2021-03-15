package com.sup.core.mapper;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.sup.common.bean.TbApplyInfoBean;
import com.sup.common.bean.TbUserBankAccountInfoBean;
import com.sup.core.bean.CustomerInfoBean;
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

    @Select("select * from tb_user_bank_account_info " +
            " where info_id in ( " +
            "  select info_id from tb_apply_material_info " +
            "  where apply_id=#{applyId} and info_type=4 " +
            " ) order by create_time desc limit 1")
    TbUserBankAccountInfoBean getUserBankAccountInfo(@Param(value = "applyId") Integer applyId);

    @Select(" select " +
            " uri.id as userId" +
            " ,uri.name as name" +
            " ,uri.mobile as mobile" +
            " ,cid.cid_no as cidNo" +
            " ,uri.create_time as registDate" +
            " from ( select * from tb_user_regist_info where id=#{userId} ) uri" +
            " left join (" +
            "    select * from tb_user_citizen_identity_card_info where user_id=#{userId}" +
            " ) cid on uri.id=cid.user_id" +
            " left join (" +
            "    select info_id from tb_apply_material_info where apply_id=#{applyId} and info_type=0" +
            "    order by create_time desc limit 1" +
            " ) ami on cid.info_id=ami.info_id" +
            " where ami.info_id is not null"
    )
    CustomerInfoBean getCustomerInfo(@Param(value = "userId") Integer userId, @Param(value = "applyId") Integer applyId);


}
