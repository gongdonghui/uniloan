package com.sup.cms.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.sup.cms.bean.po.*;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 各种大连表合集
 *
 * @Author: kouichi
 * @Date: 2019/10/6 17:04
 */
public interface CrazyJoinMapper extends BaseMapper {
    @Select("select " +
            "a.id,a.apply_id,a.create_time,a.task_type,a.status,a.operator_id,a.update_time," +
            "b.credit_class,b.create_time as apply_create_time,b.expire_time as apply_expire_time," +
            "c.name as product_name" +
            "d.cid_no,d.name," +
            "e.mobile" +
            " from tb_operation_task a" +
            " join tb_apply_info b on a.apply_id=b.id" +
            " join tb_produt_info c on b.product_id=c.id" +
            " join tb_user_citizen_identity_card_info d on b.user_id=d.user_id" +
            " join tb_user_regist_info e on b.user_id=e.id" +
            " where 1=1" +
            "${conditions}" +
            " limit #{offset},#{rows}")
    List<ApplyApprovalGetListBean> applyApprovalGetList(String conditions, Integer offset, Integer rows);

    @Select("select " +
            "a.id as applyId,a.status as status,a.apply_quota as amount,1 as jiekuanqixian,a.fee_type as huanKuanFangShi,'' as shangHuiMingCheng,a.create_time as dealDate,a.create_time as createTime,a.update_time as updateTime" +
            "b.name as productName," +
            "c.name as name," +
            "d.APP_NAME as appName" +
            " from tb_apply_info a" +
            " left join tb_product_info b on a.product_id=b.id" +
            " left join tb_user_citizen_identity_card_info c on a.user_id=c.user_id" +
            " left join tb_app_version d on a.app_id=d.id" +
            " where 1=1" +
            "${conditions}" +
            " limit #{offset},#{rows}")
    List<ApplyManagementGetListBean> applyManagementGetList(String conditions, Integer offset, Integer rows);

    @Select("select" +
            " a.status,a.id as applyId,a.create_time as createTIme,c.name as productName,a.apply_quota as applyQuota,a.rate as applyRate,a.fee_type as feeType,a.grant_quota as grantQuota,a.rate as rate,a.id as loanId,b.purpose as purpose,a.quota as quota,d.APP_NAME as appName,b.credit_level as creditLevel,e.name as channel" +
            " from tb_apply_info a left join tb_user_basic_info b on a.user_id=b.user_id left join tb_product_info c on a.product_id=c.id left join tb_app_version d on a.app_id=d.id left join tb_channel_info e on a.channel_id=e.id" +
            " where (select info_id from tb_apply_material_info where apply_id=#{applyId} and info_type=1) and a.id=#{applyId}")
    DetailsApplyInfoBean detailsApplyInfo(Integer applyId);

    @Select("select" +
            " a.fee_type as feeType,a.rate as rate,b.repay_end_date as endDate" +
            " from tb_apply_info a left join tb_repay_plan b on a.id=b.apply_id" +
            " where a.id=#{applyId}")
    DetailsToBeRepayBean detailsToBeRepay(Integer applyId);

    @Select("select" +
            " seq_no as seqNo,repay_start_date as startDate,need_total as needTotal,(need_principal-act_principal) as remainPrincipal,(need_interest-act_interest) as remainInterest,(need_penalty_interest-act_penalty_interest) as remainPenaltyInterest,(need_other-act_other) as other from tb_repay_plan" +
            " where apply_id=#{applyId}")
    List<DetailsToBeRepayList> detailsToBeRepayList(Integer applyId);

    @Select("select" +
            " repay_time as repayTime,act_total as actTotal,(need_total-act_total) as remainTotal,act_principal as actPrincipal,act_interest as actInterest,act_penalty_interest as actPenaltyInterest,(need_other-act_other) as remainOther from tb_repay_plan" +
            " where apply_id=#{applyId}")
    List<DetailsRepayRecordBean> detailsRepayRecord(Integer applyId);
}
