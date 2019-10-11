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
            "a.id as id, a.apply_id as applyId, a.create_time as createTime, a.task_type as taskType," +
            "a.status as status, a.operator_id as operatorId, a.update_time as updateTime," +
            "b.credit_class as creditClass, b.apply_quota as applyQuota, b.create_time as applyCreateTime, b.expire_time as applyExpireTime," +
            "c.name as productName, e.cid_no as cidNo, e.name as name, f.mobile" +
            " from tb_operation_task a" +
            " join tb_apply_info b on a.apply_id=b.id" +
            " join tb_product_info c on b.product_id=c.id" +
            " left join tb_apply_material_info d on b.id=d.apply_id" +
            " left join tb_user_citizen_identity_card_info e on d.info_id=e.info_id" +
            " join tb_user_regist_info f on b.user_id=f.id" +
            " where d.info_type=0" +
            " ${conditions}" +
            " limit #{offset},#{rows}")
    List<ApplyApprovalGetListBean> applyApprovalGetList(String conditions, Integer offset, Integer rows);

    @Select("select " +
            "a.id as applyId,a.status as status,a.apply_quota as amount,1 as jieKuanQiXian,a.fee_type as huanKuanFangShi,'' as shangHuiMingCheng,a.create_time as dealDate,a.create_time as createTime,a.update_time as updateTime," +
            "b.name as productName," +
            "d.name as name," +
            "e.APP_NAME as appName" +
            " from tb_apply_info a" +
            " left join tb_product_info b on a.product_id=b.id" +
            " left join tb_apply_material_info c on a.id=c.apply_id" +
            " left join tb_user_citizen_identity_card_info d on c.info_id=d.info_id" +
            " left join tb_app_version e on a.app_id=d.id" +
            " where c.info_type=0" +
            "${conditions}" +
            " limit #{offset},#{rows}")
    List<ApplyManagementGetListBean> applyManagementGetList(String conditions, Integer offset, Integer rows);

//    @Select("select" +
//            " a.status,a.id as applyId,a.create_time as createTIme,c.name as productName,a.apply_quota as applyQuota,a.rate as applyRate,a.fee_type as feeType,a.grant_quota as grantQuota,a.rate as rate,a.id as loanId,b.purpose as purpose,a.quota as quota,d.APP_NAME as appName,b.credit_level as creditLevel,e.name as channel" +
//            " from tb_apply_info a left join tb_user_basic_info b on a.user_id=b.user_id left join tb_product_info c on a.product_id=c.id left join tb_app_version d on a.app_id=d.id left join tb_channel_info e on a.channel_id=e.id" +
//            " where (select info_id from tb_apply_material_info where apply_id=#{applyId} and info_type=1) and a.id=#{applyId}")
    @Select("select " +
            "a.status, a.id as applyId, a.create_time as createTIme,d.name as productName,a.apply_quota as applyQuota,a.rate as applyRate,a.fee_type as feeType" +
            ", a.grant_quota as grantQuota,a.rate as rate,a.id as loanId" +
            ", c.purpose as purpose,a.quota as quota,e.APP_NAME as appName,c.credit_level as creditLevel,f.name as channel" +
            " from (select * from tb_apply_info where id=#{applyId}) a" +
            " left join ( select * from tb_apply_material_info where info_type=1) b on a.id = b.apply_id" +
            " left join tb_user_basic_info c on a.user_id=c.user_id and b.info_id = c.info_id" +
            " left join tb_product_info d on a.product_id=d.id" +
            " left join tb_app_version e on a.app_id=e.id" +
            " left join tb_channel_info f on a.channel_id=f.id")
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

    @Select("select a.rule_status as ruleStatus,a.rule_hit_type as ruleHitType,b.variable_name as variableName from tb_core_risk_decesion_result_detail a left join tb_core_risk_rules b on a.rule_id=b.id" +
            " where a.apply_id=#{applyId}")
    DetailsRiskDecisionBean detailsRiskDecision(Integer applyId);

    @Select("select " +
            " a.id as id," +
            " b.id as applyId," +
            " a.expire_time as lastAllocateDate," +
            " e.name," +
            " g.mobile as mobile," +
            " a.comment as status," +
            " c.name as productName," +
            " b.period as period," +
            " 1 as currentTerm, 1 as totalTerms," +
            // "'未知' as type," +
            // "'未知' as overdueLevel," +
            " h.overdue_days_max as overdueDays," +
            " f.need_total as shouldRepayAmount," +
            " f.repay_end_date as shouldRepayDate," +
            " b.loan_time as payDate," +
            " a.update_time as updateTime " +
            " from tb_operation_task a " +
            " left join tb_apply_info b on a.apply_id=b.id " +
            " left join tb_product_info c on b.product_id=c.id " +
            " left join tb_apply_material_info d on b.id=d.apply_id " +
            " left join tb_user_citizen_identity_card_info e on d.info_id=e.info_id " +
            " left join tb_repay_plan f on b.id=f.apply_id " +
            " left join tb_user_regist_info g on b.user_id=g.id " +
            " left join tb_repay_stat h on b.id=h.apply_id " +
            " where a.task_type=3 and a.has_owner=0 and d.info_type=0 and f.seq_no=1 " +
            " ${conditions} " +
            " limit #{offset},#{rows}")
    List<CollectionAllocateGetListBean> collectionAllocateGetList(String conditions, Integer offset, Integer rows);

    @Select("select " +
            " a.id as id," +
            " b.id as applyId," +
            " g.mobile as mobile," +
            " a.expire_time as lastAllocateDate," +
            " f.repay_status as periodStatus," +
            " a.comment as status," +
            " f.repay_status as partialRepay," +
            " j.APP_NAME as appName," +
            " c.name as productName," +
            " b.period as period," +
            " 1 as currentTerm, 1 as totalTerms," +
            " e.name," +
            " f.repay_end_date as shouldRepayDate," +
            " h.overdue_days_max as overdueDays," +
            " f.need_total as shouldRepayAmount," +
            " i.name as collector," +
            " a.update_time as lastCollectDate" +
            " from tb_operation_task a " +
            " left join tb_apply_info b on a.apply_id=b.id " +
            " left join tb_product_info c on b.product_id=c.id " +
            " left join tb_apply_material_info d on b.id=d.apply_id " +
            " left join tb_user_citizen_identity_card_info e on d.info_id=e.info_id " +
            " left join tb_repay_plan f on b.id=f.apply_id " +
            " left join tb_user_regist_info g on b.user_id=g.id " +
            " left join tb_repay_stat h on b.id=h.apply_id " +
            " left join tb_cms_auth_user i on i.id=a.operator_id " +
            " left join tb_app_version j on j.id=b.app_id " +
            " where a.task_type=3 and d.info_type=0 and f.seq_no=1" +
            " ${conditions}" +
            " limit #{offset},#{rows}")
    List<CollectionArchivesGetListBean> collectionArchivesGetList(String conditions, Integer offset, Integer rows);
}
