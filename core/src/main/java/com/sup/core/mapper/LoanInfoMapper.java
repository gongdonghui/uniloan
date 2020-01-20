package com.sup.core.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.sup.common.bean.TbLoanInfoBean;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * Project:uniloan
 * Class:  RepayStatMapper
 * <p>
 * Author: guanfeng
 * Create: 2019-09-09
 */

public interface LoanInfoMapper extends BaseMapper<TbLoanInfoBean> {
    
    @Select("select" +
            " rs.apply_id as apply_id" +
            " ,ai.user_id as user_id" +
            " ,ai.product_id as product_id" +
            " ,ai.channel_id as channel_id" +
            " ,ai.app_id as app_id" +
            " ,rs.current_seq as current_seq" +
            " ,ai.status as status" +
            " ,rp.is_overdue as is_overdue" +
            " ,rs.overdue_days_max as max_overdue_days" +
            " ,uri.create_time as regist_time" +
            " ,ai.create_time as apply_time" +
            " ,ai.loan_time as loan_time" +
            " ,rp.repay_end_date as repay_end_date" +
            " ,rp.repay_time as repay_time" +
            " ,ai.grant_quota as contract_amount" +
            " ,ai.inhand_quota as inhand_amount" +
            " ,rs.need_principal as need_principal" +
            " ,rs.act_principal as act_principal" +
            " ,rs.need_total as need_total" +
            " ,rs.act_total as act_total" +
            " ,rs.normal_repay as normal_repay" +
            " ,rs.reduction_fee as reduction_fee" +
            " ,rs.need_penalty_interest as need_penalty_interest" +
            " ,rs.act_penalty_interest as act_penalty_interest" +
            " ,case when rp.is_overdue=1 then (rs.need_total - rs.normal_repay) else 0 end as overdue_amount" +
            " ,rs.need_total - rs.act_total as remain_overdue_amount" +
            " from tb_repay_stat rs" +
            " left join tb_repay_plan rp on rs.apply_id=rp.apply_id and rs.current_seq=rp.seq_no" +
            " left join tb_apply_info ai on rs.apply_id=ai.id" +
            " left join tb_user_regist_info uri on ai.user_id=uri.id" +
            " order by rs.apply_id" +
            " limit #{offset},#{rows}")
    List<TbLoanInfoBean> getLoanInfo(Integer offset, Integer rows);

    @Select("select count(rs.apply_id)" +
            " from tb_repay_stat rs" +
            " left join tb_repay_plan rp on rs.apply_id=rp.apply_id and rs.current_seq=rp.seq_no" +
            " left join tb_apply_info ai on rs.apply_id=ai.id" +
            " left join tb_user_regist_info uri on ai.user_id=uri.id")
    Integer getLoanInfoCount();

}
