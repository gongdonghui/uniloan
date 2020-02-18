package com.sup.cms.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.sup.cms.bean.po.*;
import com.sup.common.bean.OperationTaskJoinBean;
import com.sup.common.bean.OperatorInfoBean;
import com.sup.common.bean.TbReportCheckOperatorDaily;
import org.apache.ibatis.annotations.Param;
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

    @Select("select count(a.id)" +
            " from tb_operation_task a" +
            " join tb_apply_info b on a.apply_id=b.id" +
            " join tb_product_info c on b.product_id=c.id" +
            " left join tb_apply_material_info d on b.id=d.apply_id" +
            " left join tb_user_citizen_identity_card_info e on d.info_id=e.info_id" +
            " join tb_user_regist_info f on b.user_id=f.id" +
            " where d.info_type=0" +
            " ${conditions}")
    Integer applyApprovalGetListForPaging(@Param(value = "conditions") String conditions);

    @Select("select " +
            "a.user_id as userId" +
            ",a.channel_id as channelId" +
            ",a.id as applyId" +
            ",a.status as status" +
            ",a.apply_quota as amount" +
            ",a.inhand_quota as inhandAmount" +
            ",1 as jieKuanQiXian" +
            ",a.fee_type as huanKuanFangShi" +
            ",'' as shangHuiMingCheng" +
            ",a.create_time as dealDate" +
            ",a.create_time as createTime" +
            ",a.update_time as updateTime" +
            ",b.name as productName" +
            ",d.name as name" +
            ",d.cid_no as cidNo" +
            ",e.APP_NAME as appName" +
            ",f.mobile as mobile" +
            " from tb_apply_info a" +
            " left join tb_product_info b on a.product_id=b.id" +
            " left join tb_apply_material_info c on a.id=c.apply_id" +
            " left join tb_user_citizen_identity_card_info d on c.info_id=d.info_id" +
            " left join tb_app_version e on a.app_id=d.id" +
            " left join tb_user_regist_info as f on a.user_id=f.id" +
            " where c.info_type=0" +
            " ${conditions}" +
            " order by applyId desc" +
            " limit #{offset},#{rows}")
    List<ApplyManagementGetListBean> applyManagementGetList(String conditions, Integer offset, Integer rows);

    @Select("select count(a.id)" +
            " from tb_apply_info a" +
            " left join tb_product_info b on a.product_id=b.id" +
            " left join tb_apply_material_info c on a.id=c.apply_id" +
            " left join tb_user_citizen_identity_card_info d on c.info_id=d.info_id" +
            " left join tb_app_version e on a.app_id=d.id" +
            " left join tb_user_regist_info as f on a.user_id=f.id" +
            " where c.info_type=0" +
            " ${conditions}")
    Integer applyManagementGetListForPaging(@Param(value = "conditions") String conditions);

    //    @Select("select" +
//            " a.status,a.id as applyId,a.create_time as createTIme,c.name as productName,a.apply_quota as applyQuota,a.rate as applyRate,a.fee_type as feeType,a.grant_quota as grantQuota,a.rate as rate,a.id as loanId,b.purpose as purpose,a.quota as quota,d.APP_NAME as appName,b.credit_level as creditLevel,e.name as channel" +
//            " from tb_apply_info a left join tb_user_basic_info b on a.user_id=b.user_id left join tb_product_info c on a.product_id=c.id left join tb_app_version d on a.app_id=d.id left join tb_channel_info e on a.channel_id=e.id" +
//            " where (select info_id from tb_apply_material_info where apply_id=#{applyId} and info_type=1) and a.id=#{applyId}")
    @Select("select " +
            "a.status, a.id as applyId, a.user_id as userId, a.create_time as createTIme,d.name as productName,a.apply_quota as applyQuota,a.rate as applyRate,a.fee_type as feeType" +
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
    DetailsToBeRepayBean detailsToBeRepay(@Param(value = "applyId") Integer applyId);

    @Select("select" +
            " seq_no as seqNo,repay_start_date as startDate,need_total as needTotal,(need_principal-act_principal) as remainPrincipal,(need_interest-act_interest) as remainInterest,(need_penalty_interest-act_penalty_interest) as remainPenaltyInterest,(need_other-act_other) as other from tb_repay_plan" +
            " where apply_id=#{applyId}")
    List<DetailsToBeRepayList> detailsToBeRepayList(@Param(value = "applyId") Integer applyId);

    @Select("select" +
            " repay_time as repayTime,act_total as actTotal,(need_total-act_total) as remainTotal,act_principal as actPrincipal,act_interest as actInterest,act_penalty_interest as actPenaltyInterest,(need_other-act_other) as remainOther from tb_repay_plan" +
            " where apply_id=#{applyId}")
    List<DetailsRepayRecordBean> detailsRepayRecord(@Param(value = "applyId") Integer applyId);

    @Select("select a.rule_status as ruleStatus,a.rule_hit_type as ruleHitType,b.variable_name as variableName from tb_core_risk_decesion_result_detail a left join tb_core_risk_rules b on a.rule_id=b.id" +
            " where a.apply_id=#{applyId}")
    List<DetailsRiskDecisionBean> detailsRiskDecision(@Param(value = "applyId") Integer applyId);

    @Select("select " +
            " a.id as id," +
            " b.id as applyId," +
            " b.user_id as userId," +
            " a.create_time as lastAllocateDate," +
            " e.name," +
            " e.cid_no as cidNo," +
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
            " where a.task_type=3 and b.status!=16 and a.has_owner=0 and d.info_type=0 and f.seq_no=1 " +
            " ${conditions} " +
            " limit #{offset},#{rows}")
    List<CollectionAllocateGetListBean> collectionAllocateGetList(String conditions, Integer offset, Integer rows);

    @Select("select count(a.id)" +
            " from tb_operation_task a " +
            " left join tb_apply_info b on a.apply_id=b.id " +
            " left join tb_product_info c on b.product_id=c.id " +
            " left join tb_apply_material_info d on b.id=d.apply_id " +
            " left join tb_user_citizen_identity_card_info e on d.info_id=e.info_id " +
            " left join tb_repay_plan f on b.id=f.apply_id " +
            " left join tb_user_regist_info g on b.user_id=g.id " +
            " left join tb_repay_stat h on b.id=h.apply_id " +
            " where a.task_type=3 and b.status!=16 and a.has_owner=0 and d.info_type=0 and f.seq_no=1 " +
            " ${conditions}")
    Integer collectionAllocateGetListForPaging(@Param(value = "conditions") String conditions);

    @Select("select " +
            " a.id as id," +
            " b.id as applyId," +
            " b.user_id as userId," +
            " g.mobile as mobile," +
            " a.create_time as lastAllocateDate," +
            " f.repay_status as periodStatus," +
            " a.comment as status," +
            " f.repay_status as partialRepay," +
            " j.APP_NAME as appName," +
            " c.name as productName," +
            " b.period as period," +
            " 1 as currentTerm, 1 as totalTerms," +
            " e.name," +
            " e.cid_no as cidNo," +
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
            " where a.task_type=3 and b.status!=16 and d.info_type=0 and f.seq_no=1" +
            " ${conditions}" +
            " limit #{offset},#{rows}")
    List<CollectionArchivesGetListBean> collectionArchivesGetList(String conditions, Integer offset, Integer rows);

    @Select("select count(a.id)" +
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
            " where a.task_type=3 and b.status!=16 and d.info_type=0 and f.seq_no=1" +
            " ${conditions}")
    Integer collectionArchivesGetListForPaging(@Param(value = "conditions") String conditions);


    @Select("select distinct" +
            " a.id as applyId," +
            " a.user_id as userId," +
            " d.mobile as mobile," +
            " e.name as productName," +
            " c.name as name," +
            " c.cid_no as cidNo," +
            " case " +
            " when a.status=16 then (f.need_total-f.act_total-f.reduction_fee)" +
            " else 0 end writeOffAmount," +
            " a.grant_quota as loanAmount," +
            " f.need_total as shouldRepayAmount," +
            " f.act_total as repayAmount," +
            " case when g.status = 0 then 1 else 0 end as repayNeedConfirm," +
            " a.loan_time as loanDate," +
            " f.repay_end_date as endDate," +
            " f.repay_time as repayDate," +
            " f.update_time as updateTime," +
            " f.seq_no as period," +
            " f.seq_no as repayPeriod" +
            " from tb_apply_info a " +
            " left join tb_apply_material_info b on a.id=b.apply_id " +
            " left join tb_user_citizen_identity_card_info c on b.info_id=c.info_id " +
            " left join tb_user_regist_info d on d.id=a.user_id " +
            " left join tb_product_info e on a.product_id=e.id " +
            " left join tb_repay_plan f on a.id=f.apply_id " +
            " left join (select * from tb_manual_repay where status = 0) g on f.id = g.plan_id and f.apply_id = g.apply_id" +
            " where (a.status in (13,14,16) or ((a.status=12 or a.status=15) and g.status = 0)) and b.info_type=0 " +
            " ${conditions} " +
            " limit #{offset},#{rows}")
    List<LoanRepayInfoGetListBean> loanRepayInfoGetList(String conditions, Integer offset, Integer rows);

    @Select("select count(distinct a.id) " +
            " from tb_apply_info a " +
            " left join tb_apply_material_info b on a.id=b.apply_id " +
            " left join tb_user_citizen_identity_card_info c on b.info_id=c.info_id " +
            " left join tb_user_regist_info d on d.id=a.user_id " +
            " left join tb_product_info e on a.product_id=e.id " +
            " left join tb_repay_plan f on a.id=f.apply_id " +
            " left join (select * from tb_manual_repay where status = 0) g on f.id = g.plan_id and f.apply_id = g.apply_id" +
            " where (a.status in (13,14,16) or ((a.status=12 or a.status=15) and g.status = 0)) and b.info_type=0 " +
            " ${conditions}")
    Integer loanRepayInfoGetListForPaging(@Param(value = "conditions") String conditions);

    @Select("select " +
            " a.id as applyId," +
            " a.user_id as userId," +
            " d.mobile as mobile," +
            " e.name as productName," +
            " c.name as name," +
            " c.cid_no as cidNo," +
            " a.grant_quota as loanAmount," +
            " f.need_total as shouldRepayAmount," +
            " a.loan_time as loanDate," +
            " f.repay_end_date as endDate," +
            " f.seq_no as period" +
            " from tb_apply_info a " +
            " left join tb_apply_material_info b on a.id=b.apply_id " +
            " left join tb_user_citizen_identity_card_info c on b.info_id=c.info_id " +
            " left join tb_user_regist_info d on d.id=a.user_id " +
            " left join tb_product_info e on a.product_id=e.id " +
            " left join tb_repay_plan f on a.id=f.apply_id " +
            " left join (select * from tb_manual_repay where status = 0) g on f.id = g.plan_id and f.apply_id = g.apply_id" +
            " where a.status=12 and (g.status is null or g.status!=0) and b.info_type=0" +
            " ${conditions} " +
            " limit #{offset},#{rows}")
    List<LoanUnRepayInfoGetListBean> loanUnRepayInfoGetList(String conditions, Integer offset, Integer rows);

    @Select("select count(a.id) " +
            " from tb_apply_info a " +
            " left join tb_apply_material_info b on a.id=b.apply_id " +
            " left join tb_user_citizen_identity_card_info c on b.info_id=c.info_id " +
            " left join tb_user_regist_info d on d.id=a.user_id " +
            " left join tb_product_info e on a.product_id=e.id " +
            " left join tb_repay_plan f on a.id=f.apply_id " +
            " left join (select * from tb_manual_repay where status = 0) g on f.id = g.plan_id and f.apply_id = g.apply_id" +
            " where a.status=12 and (g.status is null or g.status!=0) and b.info_type=0" +
            " ${conditions}")
    Integer loanUnRepayInfoGetListForPaging(@Param(value = "conditions") String conditions);

    @Select("select " +
            " a.id as applyId," +
            " g.APP_NAME as appName," +
            " e.name as productName," +
            " c.name as name," +
            " d.mobile as mobile," +
            " c.cid_no as cidNo," +
            " a.grant_quota as loanAmount," +
            " f.need_total as shouldRepayAmount," +
            " case when f.is_overdue=1 then (f.need_total-f.act_total) else 0 end as overdueAmount," +
            " a.loan_time as loanDate," +
            " f.repay_end_date as endDate," +
            " f.repay_time as shouldRepayDate," +
            " f.seq_no as period" +
            " from tb_apply_info a " +
            " left join tb_apply_material_info b on a.id=b.apply_id " +
            " left join tb_user_citizen_identity_card_info c on b.info_id=c.info_id " +
            " left join tb_user_regist_info d on d.id=a.user_id " +
            " left join tb_product_info e on a.product_id=e.id " +
            " left join tb_repay_plan f on a.id=f.apply_id " +
            " left join tb_app_version g on a.app_id=g.ID" +
            " where b.info_type=0 and f.is_overdue=1" +
            " ${conditions} " +
            " limit #{offset},#{rows}")
    List<AfterLoanOverdueGetListBean> afterLoanOverdueGetList(String conditions, Integer offset, Integer rows);

    @Select("select " +
            " count(a.id) " +
            " from tb_apply_info a " +
            " left join tb_apply_material_info b on a.id=b.apply_id " +
            " left join tb_user_citizen_identity_card_info c on b.info_id=c.info_id " +
            " left join tb_user_regist_info d on d.id=a.user_id " +
            " left join tb_product_info e on a.product_id=e.id " +
            " left join tb_repay_plan f on a.id=f.apply_id " +
            " left join tb_app_version g on a.app_id=g.ID" +
            " where b.info_type=0 and f.is_overdue=1" +
            " ${conditions}")
    Integer afterLoanOverdueGetListForPaging(@Param(value = "conditions") String conditions);

    @Select("select " +
            " a.id as applyId," +
            " e.name as productName," +
            " c.name as name," +
            " d.mobile as mobile," +
            " a.grant_quota as loanAmount," +
            " a.inhand_quota as actAmount," +
            " 0 as otherAmount," +
            " a.period as period," +
            " count(distinct f.id) as terms," +
            " case " +
            " when f.repay_status=2 then 1" +
            " else 0 end alreadyRepay" +
            " from tb_apply_info a " +
            " left join tb_apply_material_info b on a.id=b.apply_id " +
            " left join tb_user_citizen_identity_card_info c on b.info_id=c.info_id " +
            " left join tb_user_regist_info d on d.id=a.user_id " +
            " left join tb_product_info e on a.product_id=e.id " +
            " left join tb_repay_plan f on a.id=f.apply_id " +
            " where b.info_type=0 and a.id=#{applyId}")
    DetailsRepayBean detailsRepay(@Param(value = "applyId") String applyId);

    @Select("select " +
            "a.id as planId," +
            " a.seq_no as seqNo," +
            " a.need_total as shouldRepayAmount," +
            " (a.need_total-a.act_total-a.reduction_fee) as remainShouldRepayAmount," +
            " (a.need_principal-a.act_principal) as remainPrincipal," +
            " (a.need_interest-a.act_interest) as remainInterest," +
            " a.act_total as actRepayAmount," +
            " a.repay_end_date as shouldRepayDate," +
            " a.repay_time as actRepayDate," +
            " (a.need_penalty_interest-a.act_penalty_interest) as remainPenaltyInterestAmount," +
            " (a.need_breach_fee-a.act_breach_fee) as remainBreachFeeAmount," +
            " a.reduction_fee as reductionFee," +
            " a.repay_status as status" +
            " from " +
            "(select * from tb_repay_plan where apply_id=#{applyId}) a" +
            " left join tb_apply_info b on a.apply_id=b.id" +
            " left join tb_product_info c on b.product_id=c.id")
    List<DetailsRepayListBean> detailsRepayList(@Param(value = "applyId") String applyId);

    @Select("select distinct " +
            " uri.id as userId" +
            " ,uri.name as name" +
            " ,uri.mobile as mobile" +
            " ,cid.cid_no as cidNo" +
            " ,uri.create_time as registDate" +
            " from tb_user_regist_info uri" +
            " left join (" +
            "    select distinct user_id, cid_no" +
            "    from tb_user_citizen_identity_card_info" +
            " ) cid on uri.id = cid.user_id" +
            " where uri.type = 0" +
            " ${conditions} " +
            " limit #{offset},#{rows}")
    List<CustomerInfoBean> getCustomers(String conditions, Integer offset, Integer rows);

    @Select("select " +
            " count(distinct uri.id)" +
            " from tb_user_regist_info uri" +
            " left join (" +
            "    select distinct user_id, cid_no" +
            "    from tb_user_citizen_identity_card_info" +
            " ) cid on uri.id = cid.user_id" +
            " where uri.type = 0" +
            " ${conditions} ")
    Integer getCustomersCount(@Param(value = "conditions") String conditions);

    @Select("select" +
            "  ai.id as applyId" +
            "  ,rp.repay_end_date as shouldRepayDate" +
            "  ,b.name as name" +
            "  ,b.gender as gender" +
            "  ,c.age as age" +
            "  ,uri.mobile as mobile" +
            "  ,b.cid_no as cidNo" +
            "  ,av.app_name as appName" +
            "  ,pi.name as productName" +
            "  ,(rs.need_total-rs.act_total) as remainAmount" +
            "  ,(rs.act_total-rs.normal_repay) as callBackAmount" +
            "  ,ai.grant_quota as loanAmount" +
            "  ,rs.need_total as shouldRepayAmount" +
            "  ,case when rp.is_overdue=1 then (rs.need_total-rs.normal_repay) else 0 end as overdueAmount" +
            "  ,rs.need_penalty_interest as penaltyInterest" +
            "  ,rs.overdue_days_max as overdueDays" +
            "  ,ot.status as taskStatus" +
            "  ,ai.loan_time as loanDate" +
            "  ,case when ot.operator_id is not null then ot.update_time else null end as taskDate" +
            "  ,ot.operator_id as operatorId" +
            "  ,cau.name as operatorName" +
            "  ,case when ot.operator_id is null then 1 else 0 end as label" +
            " from (select * from tb_apply_info where status in (12, 13, 15)) ai" +
            " left join (" +
            "  select apply_id, name, gender, cid_no from (" +
            "    select * from tb_apply_material_info where info_type=0" +
            "  ) ami left join tb_user_citizen_identity_card_info ucid on ami.info_id=ucid.info_id" +
            ") b on ai.id=b.apply_id" +
            " left join (" +
            "  select apply_id, age from (" +
            "    select * from tb_apply_material_info where info_type=1" +
            "  ) ami left join tb_user_basic_info ubi on ami.info_id=ubi.info_id" +
            ") c on ai.id=c.apply_id" +
            " left join tb_user_regist_info uri on ai.user_id=uri.id" +
            " left join tb_app_version av on ai.app_id=av.id" +
            " left join tb_product_info pi on ai.product_id=pi.id" +
            " left join tb_repay_stat rs on ai.id=rs.apply_id" +
            " left join tb_repay_plan rp on ai.id=rp.apply_id" +
            " left join (" +
            "  select distinct apply_id,status,task_type,operator_id,update_time from tb_operation_task where task_type=3" +
            ") ot on ai.id=ot.apply_id" +
            " left join tb_cms_auth_user cau on ot.operator_id=cau.id" +
            " where uri.type = 0" +
            " ${conditions}" +
            " order by label, ai.id desc" +
            " limit #{offset},#{rows}")
    List<OverdueGetListBean> getPoolList(String conditions, Integer offset, Integer rows);

    @Select("select count(ai.id)" +
            "from (select * from tb_apply_info where status in (12, 13, 15)) ai" +
            " left join (" +
            "  select apply_id, name, gender, cid_no from (" +
            "    select * from tb_apply_material_info where info_type=0" +
            "  ) ami left join tb_user_citizen_identity_card_info ucid on ami.info_id=ucid.info_id" +
            ") b on ai.id=b.apply_id" +
            " left join (" +
            "  select apply_id, age from (" +
            "    select * from tb_apply_material_info where info_type=1" +
            "  ) ami left join tb_user_basic_info ubi on ami.info_id=ubi.info_id" +
            ") c on ai.id=c.apply_id" +
            " left join tb_user_regist_info uri on ai.user_id=uri.id" +
            " left join tb_app_version av on ai.app_id=av.id" +
            " left join tb_product_info pi on ai.product_id=pi.id" +
            " left join tb_repay_stat rs on ai.id=rs.apply_id" +
            " left join tb_repay_plan rp on ai.id=rp.apply_id" +
            " left join (" +
            "  select distinct apply_id,status,task_type,operator_id,update_time from tb_operation_task where task_type=3" +
            ") ot on ai.id=ot.apply_id" +
            " left join tb_cms_auth_user cau on ot.operator_id=cau.id" +
            " where uri.type = 0" +
            " ${conditions}")
    Integer getPoolListCount(@Param(value = "conditions") String conditions);

    @Select("select" +
            "  ai.id as applyId" +
            "  ,rp.repay_end_date as shouldRepayDate" +
            "  ,b.name as name" +
            "  ,b.gender as gender" +
            "  ,c.age as age" +
            "  ,uri.mobile as mobile" +
            "  ,b.cid_no as cidNo" +
            "  ,av.app_name as appName" +
            "  ,pi.name as productName" +
            "  ,(rs.need_total-rs.act_total) as remainAmount" +
            "  ,(rs.act_total-rs.normal_repay) as callBackAmount" +
            "  ,ai.grant_quota as loanAmount" +
            "  ,rs.need_total as shouldRepayAmount" +
            "  ,case when rp.is_overdue=1 then (rs.need_total-rs.normal_repay) else 0 end as overdueAmount" +
            "  ,rs.need_penalty_interest as penaltyInterest" +
            "  ,rs.overdue_days_max as overdueDays" +
            "  ,ot.status as taskStatus" +
            "  ,ai.loan_time as loanDate" +
            "  ,ot.update_time as taskDate" +
            "  ,ot.operator_id as operatorId" +
            "  ,cau.name as operatorName" +
            " from (" +
            "  select distinct apply_id,status,task_type,operator_id,update_time from tb_operation_task where task_type=3 and has_owner=1 and status!=1" +
            ") ot left join (" +
            "  select * from tb_apply_info where status in (12, 13, 15)" +
            ") ai on ot.apply_id=ai.id" +
            " left join (" +
            "  select apply_id, name, gender, cid_no from (" +
            "    select * from tb_apply_material_info where info_type=0" +
            "  ) ami left join tb_user_citizen_identity_card_info ucid on ami.info_id=ucid.info_id" +
            ") b on ai.id=b.apply_id" +
            " left join (" +
            "  select apply_id, age from (" +
            "    select * from tb_apply_material_info where info_type=1" +
            "  ) ami left join tb_user_basic_info ubi on ami.info_id=ubi.info_id" +
            ") c on ai.id=c.apply_id" +
            " left join tb_user_regist_info uri on ai.user_id=uri.id" +
            " left join tb_app_version av on ai.app_id=av.id" +
            " left join tb_product_info pi on ai.product_id=pi.id" +
            " left join tb_repay_stat rs on ai.id=rs.apply_id" +
            " left join tb_repay_plan rp on ai.id=rp.apply_id" +
            " left join tb_cms_auth_user cau on ot.operator_id=cau.id" +
            " where uri.type = 0" +
            " ${conditions}" +
            " order by ai.id desc" +
            " limit #{offset},#{rows}")
    List<OverdueGetListBean> getTaskList(String conditions, Integer offset, Integer rows);

    @Select("select count(ai.id)" +
            " from (" +
            "  select distinct apply_id,status,task_type,operator_id,update_time from tb_operation_task where task_type=3 and has_owner=1 and status!=1" +
            ") ot left join (" +
            "  select * from tb_apply_info where status in (12, 13, 15)" +
            ") ai on ot.apply_id=ai.id" +
            " left join (" +
            "  select apply_id, name, gender, cid_no from (" +
            "    select * from tb_apply_material_info where info_type=0" +
            "  ) ami left join tb_user_citizen_identity_card_info ucid on ami.info_id=ucid.info_id" +
            ") b on ai.id=b.apply_id" +
            " left join (" +
            "  select apply_id, age from (" +
            "    select * from tb_apply_material_info where info_type=1" +
            "  ) ami left join tb_user_basic_info ubi on ami.info_id=ubi.info_id" +
            ") c on ai.id=c.apply_id" +
            " left join tb_user_regist_info uri on ai.user_id=uri.id" +
            " left join tb_app_version av on ai.app_id=av.id" +
            " left join tb_product_info pi on ai.product_id=pi.id" +
            " left join tb_repay_stat rs on ai.id=rs.apply_id" +
            " left join tb_repay_plan rp on ai.id=rp.apply_id" +
            " left join tb_cms_auth_user cau on ot.operator_id=cau.id" +
            " where uri.type = 0" +
            " ${conditions}" +
            " order by ai.id desc")
    Integer getTaskListCount(@Param(value = "conditions") String conditions);


    @Select("select" +
            "  data_dt as allocDate" +
            "  ,count(apply_id) as taskNum" +
            "  ,sum(need_total-normal_repay) as taskAmount" +
            "  ,sum(case when status=14 then 1 else 0 end) as collectNum" +
            "  ,sum(case when status=14 then act_total-normal_repay else 0 end) as collectAmt" +
            "  ,sum(case when status!=14 and act_total>normal_repay then 1 else 0 end) as partialCollectNum" +
            "  ,sum(case when status!=14 and act_total>normal_repay then act_total-normal_repay else 0 end) as partialCollectAmt" +
            "  ,sum(case when status!=14 and act_total=normal_repay then 1 else 0 end) as noCollectNum" +
            " from tb_report_overdue_detail" +
            " where 1=1 " +
            " ${conditions}" +
            " group by data_dt order by data_dt desc" +
            " limit #{offset},#{rows}")
    List<ReportCollectorBean> getCollectorReportAll(String conditions, Integer offset, Integer rows);

    @Select("select count(*) from (" +
            " select * from tb_report_overdue_detail" +
            " where 1=1 " +
            " ${conditions}" +
            " group by data_dt order by data_dt desc) tb")
    Integer getCollectorReportAllCount(@Param(value = "conditions") String conditions);

    @Select("select" +
            "  data_dt as allocDate" +
            "  ,operator_id as operatorId" +
            "  ,count(apply_id) as taskNum" +
            "  ,sum(need_total-normal_repay) as taskAmount" +
            "  ,sum(case when status=14 then 1 else 0 end) as collectNum" +
            "  ,sum(case when status=14 then act_total-normal_repay else 0 end) as collectAmt" +
            "  ,sum(case when status!=14 and act_total>normal_repay then 1 else 0 end) as partialCollectNum" +
            "  ,sum(case when status!=14 and act_total>normal_repay then act_total-normal_repay else 0 end) as partialCollectAmt" +
            "  ,sum(case when status!=14 and act_total=normal_repay then 1 else 0 end) as noCollectNum" +
            " from tb_report_overdue_detail" +
            " where 1=1 " +
            " ${conditions}" +
            " group by data_dt,operator_id order by data_dt desc" +
            " limit #{offset},#{rows}")
    List<ReportCollectorBean> getCollectorReport(String conditions, Integer offset, Integer rows);

    @Select("select count(*) from (" +
            " select * from tb_report_overdue_detail" +
            " where 1=1 " +
            " ${conditions}" +
            " group by data_dt,operator_id order by data_dt desc) tb")
    Integer getCollectorReportCount(@Param(value = "conditions") String conditions);


    @Select("select" +
            "  rod.apply_id as applyId" +
            "  ,rod.repay_end_date as shouldRepayDate" +
            "  ,rp.repay_time as repayDate" +
            "  ,b.name as name" +
            "  ,b.gender as gender" +
            "  ,c.age as age" +
            "  ,uri.mobile as mobile" +
            "  ,b.cid_no as cidNo" +
            "  ,av.app_name as appName" +
            "  ,pi.name as productName" +
            "  ,(rs.need_total-rs.act_total) as remainAmount" +
            "  ,(rod.act_total-rod.normal_repay) as callBackAmount" +
            "  ,ai.grant_quota as loanAmount" +
            "  ,rs.need_total as shouldRepayAmount" +
            "  ,case when rp.is_overdue=1 then (rs.need_total-rs.normal_repay) else 0 end as overdueAmount" +
            "  ,rs.need_penalty_interest as penaltyInterest" +
            "  ,rs.overdue_days_max as overdueDays" +
            "  ,ot.status as taskStatus" +
            "  ,ai.loan_time as loanDate" +
            "  ,ot.operator_id as operatorId" +
            "  ,rod.name as operatorName" +
            " from ( select * from tb_report_overdue_detail where act_total>normal_repay ) rod" +
            " left join tb_repay_stat rs on rod.apply_id=rs.apply_id" +
            " left join tb_repay_plan rp on rod.apply_id=rp.apply_id and rs.current_seq=rp.seq_no" +
            " left join tb_product_info pi on rp.product_id=pi.id" +
            " left join tb_user_regist_info uri on rp.user_id=uri.id" +
            " left join (select * from tb_apply_info where status in (12, 13, 14, 15)) ai on rod.apply_id=ai.id" +
            " left join tb_app_version av on ai.app_id=av.id" +
            " left join (" +
            "   select apply_id, name, gender, cid_no from (" +
            "    select * from tb_apply_material_info where info_type=0" +
            "   ) ami left join tb_user_citizen_identity_card_info ucid on ami.info_id=ucid.info_id" +
            " ) b on rod.apply_id=b.apply_id" +
            " left join (" +
            "   select apply_id, age from (" +
            "     select * from tb_apply_material_info where info_type=1" +
            "   ) ami left join tb_user_basic_info ubi on ami.info_id=ubi.info_id" +
            " ) c on rod.apply_id=c.apply_id" +
            " left join (" +
            "   select distinct apply_id,status,task_type,operator_id from tb_operation_task where task_type=3" +
            " ) ot on rod.apply_id=ot.apply_id" +
            " where 1=1 " +
            " ${conditions}" +
            " order by rod.apply_id desc" +
            " limit #{offset},#{rows}"
    )
    List<OverdueGetListBean> getRecallList(String conditions, Integer offset, Integer rows);

    @Select("select count(rod.apply_id)" +
            " from ( select * from tb_report_overdue_detail where act_total>normal_repay ) rod" +
            " left join tb_repay_stat rs on rod.apply_id=rs.apply_id" +
            " left join tb_repay_plan rp on rod.apply_id=rp.apply_id and rs.current_seq=rp.seq_no" +
            " left join tb_product_info pi on rp.product_id=pi.id" +
            " left join tb_user_regist_info uri on rp.user_id=uri.id" +
            " left join (select * from tb_apply_info where status in (12, 13, 14, 15)) ai on rod.apply_id=ai.id" +
            " left join tb_app_version av on ai.app_id=av.id" +
            " left join (" +
            "   select apply_id, name, gender, cid_no from (" +
            "    select * from tb_apply_material_info where info_type=0" +
            "   ) ami left join tb_user_citizen_identity_card_info ucid on ami.info_id=ucid.info_id" +
            " ) b on rod.apply_id=b.apply_id" +
            " left join (" +
            "   select apply_id, age from (" +
            "     select * from tb_apply_material_info where info_type=1" +
            "   ) ami left join tb_user_basic_info ubi on ami.info_id=ubi.info_id" +
            " ) c on rod.apply_id=c.apply_id" +
            " left join (" +
            "   select distinct apply_id,status,task_type,operator_id from tb_operation_task where task_type=3" +
            " ) ot on rod.apply_id=ot.apply_id" +
            " where 1=1 " +
            " ${conditions}")
    Integer getRecallListCount(@Param(value = "conditions") String conditions);

    @Select("select" +
            "  DATE(loan_time) as dt" +
            "  ,count(apply_id) as loanNum" +
            "  ,sum(inhand_amount) as principal" +
            "  ,sum(contract_amount) as contractAmt" +
            "  ,sum(normal_repay) as normalRepayAmt" +
            "  ,sum(case when is_overdue=0 and act_total>0 then 1 else 0 END) as normalRepayNum" +
            "  ,sum(is_overdue) as firstOverdueNum" +
            "  ,sum(overdue_amount) as firstOverdueAmt" +
            "  ,sum(case when is_overdue=1 and status=14 then contract_amount else 0 end) as overduedContractAmt" +
            "  ,sum(case when is_overdue=1 and status=14 then act_penalty_interest else 0 end) as overduedPenaltyInterest" +
            "  ,sum(case when is_overdue=1 and status!=14 then contract_amount else 0 end) as overduingContractAmt" +
            "  ,sum(act_total) as repayTotal" +
            "  ,sum(is_overdue)/count(apply_id) as foRate" +
            "  ,sum(case when is_overdue=1 and status!=14 then 1 else 0 end) as overdueNum" +
            "  ,sum(case when is_overdue=1 and status!=14 then 1 else 0 end)/count(apply_id) as overdueRate" +
            "  ,sum(act_total)/sum(inhand_amount) as principalRate" +
            " from tb_loan_info" +
            " where 1=1 " +
            " ${conditions}" +
            " group by DATE(loan_time) having loanNum>0 order by dt desc"
    )
    List<LoanStatBean> getOperationReport(String conditions, Integer offset, Integer rows);

    @Select("select count(*) from (" +
            "  select" +
            "    substr(loan_time,1,10) as dt" +
            "    ,count(apply_id) as loanNum" +
            "  from tb_loan_info" +
            "  where 1=1 " +
            "  ${conditions}" +
            "  group by substr(loan_time,1,10) having loanNum>0 order by dt desc" +
            ") tb"
    )
    Integer getOperationReportCount(@Param(value = "conditions") String conditions);


    @Select("select " +
            "a.id as id, a.apply_id as applyId, a.create_time as createTime, a.task_type as taskType," +
            "a.status as taskStatus, a.operator_id as operatorId,a.has_owner as  hasOwner," +
            "a.update_time as updateTime," +
            "b.status as applyStatus, a.operator_id as  operatorId, b.grant_quota as  loanAmt" +
            " from tb_operation_task a" +
            " join tb_apply_info b on a.apply_id=b.id" +
            " where a.task_type= ${taskType}" +
            "  and a.create_time >='${start}' and a.create_time< '${end}' ")
    List<OperationTaskJoinBean> getOperationTaskJoinByTask(String start, String end, Integer taskType);


    @Select("select *   " +
            " from  tb_report_check_operator_daily " +
            " where 1=1" +
            " ${conditions}" +
            "  and  data_dt>='${start}' and data_dt<= '${end}' order  by  data_dt desc limit ${offset}, ${rows} ")
    List<TbReportCheckOperatorDaily> getOperatorReport(String start, String end, String conditions, Integer offset, Integer rows);

    @Select("select " +
            "a.id as id, a.apply_id as applyId, a.create_time as createTime, a.task_type as taskType," +
            "a.status as taskStatus, a.operator_id as operatorId,a.has_owner as  hasOwner," +
            "a.update_time as updateTime," +
            "b.status as applyStatus, b.grant_quota as  loanAmt" +
            " from tb_operation_task a" +
            " join tb_apply_info b on a.apply_id=b.id" +
            " where (a.task_type =0 or a.task_type=2) and" +
            "  a.update_time >='${start}' and a.update_time< '${end}' ")
    List<OperationTaskJoinBean> getOperationTaskJoin(String start, String end);

    @Select("select b.grant_quota as " +
            "  loanAmt,  a.create_time as createTime, a.status as hisStatus, b.status as curStatus ,a.operator_id as operator" +
            " from  tb_apply_info_history as  a  left join tb_apply_info as b  on a.apply_id = b.id   " +
            " where   a.operator_id <> '' and (a.status =2 or  a.status =4 or  a.status =6 or  a.status =8)  " +
            "and a.create_time > '${start}'")
    List<OperatorInfoBean> getOperatorInfo(String start);


    @Select(" select case when deny_code is  NULL  THEN 'r0001' else  deny_code end as deny_code, " +
            "case  when deny_code is NULL  THEN 'manual_refuse' else variable_name end as variable, " +
            " hit_size  " +
            "from " +
            "(select deny_code, count(*) as  hit_size from tb_apply_info  where create_time >'${start}'   and  create_time < '${end}'     and   deny_code <>'' or   ( status =6  or  status =8) group by  deny_code)  as  a" +
            " left  join  tb_core_risk_rules  as b  on a.deny_code =  concat('r00',b.id) "
    )
    List<RefuseStatBean> getRefuseStat(String start, String end);
}
