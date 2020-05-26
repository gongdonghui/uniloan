package com.sup.core.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.sup.common.bean.TbVirtualCardInfo;
import com.sup.common.bean.paycenter.CreateVCInfo;
import com.sup.core.bean.VCInfo;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * gongshuai
 * <p>
 * 2019/9/22
 */
public interface VirtualCardInfoMapper extends BaseMapper<TbVirtualCardInfo> {
    @Select("select\n" +
            "  vci.id as id,\n" +
            "  vcbi.bank_account_name as accountName,\n" +
            "  vcbi.bank_name as bankName,\n" +
            "  vcbi.branch_name as branchName,\n" +
            "  vcbi.link as bankLink,\n" +
            "  vci.service_fee as serviceFee,\n" +
            "  vci.vc_no as accountNo,\n" +
            "  vci.apply_id as applyId,\n" +
            "  vci.order_no as order_no,\n" +
            "  vci.status as status,\n" +
            "  vci.expire_time as expireTime\n" +
            " from ( select * from tb_virtual_card_info where user_id=${userId}) vci\n" +
            " left join tb_virtual_card_bank_info vcbi\n" +
            " on vci.bank_name=vcbi.bank_name")
    VCInfo getVCInfo(@Param(value = "userId") String userId);

    @Select("select\n" +
            "  user_id as userId, apply_id as orderNo, cid_no as id, name as userName \n" +
            "from (\n" +
            "  select distinct info_id, apply_id\n" +
            "  from tb_apply_material_info where info_type=0 and apply_id=${applyId}\n" +
            " ) ami\n" +
            " left join tb_user_citizen_identity_card_info uci on ami.info_id=uci.info_id\n" +
            " where ami.apply_id is not null and uci.cid_no is not null")
    CreateVCInfo getCreateVCInfo(@Param(value = "applyId") String applyId);

}
