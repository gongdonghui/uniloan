package com.sup.core.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.sup.common.bean.TbReportOverdueDetailBean;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * gongshuai
 * <p>
 * 2019/10/6
 */
public interface ReportOverdueDetailMapper extends BaseMapper<TbReportOverdueDetailBean> {

    @Select("select" +
            "  substr(ot.create_time,1,10) as data_dt" +
            "  ,ot.id as task_id" +
            "  ,ot.operator_id as operator_id" +
            "  ,cau.name as name" +
            "  ,ot.apply_id as apply_id" +
            "  ,ai.product_id as product_id" +
            "  ,ai.status as status" +
            "  ,rp.is_overdue as is_overdue" +
            "  ,rs.overdue_days as overdue_days" +
            "  ,ai.grant_quota as grant_quota" +
            "  ,rp.need_total as need_total" +
            "  ,rp.act_total as act_total" +
            "  ,rs.normal_repay as normal_repay" +
            "  ,case when rp.is_overdue=1 then (rs.need_total - rs.normal_repay) else 0 end as overdue_amount" +
            "  ,(rs.act_total - rs.normal_repay) as recall_amount" +
            "  ,rp.repay_end_date as repay_end_date" +
            " from (" +
            "   select * from tb_operation_task where task_type=3 and has_owner=1" +
            " ) ot" +
            " left join tb_apply_info ai on ot.apply_id=ai.id" +
            " left join tb_repay_stat rs on ot.apply_id=rs.apply_id" +
            " left join tb_repay_plan rp on ot.apply_id=rp.apply_id and rs.current_seq=rp.seq_no" +
            " left join tb_cms_auth_user cau on ot.operator_id = cau.id" +
            " where ot.has_owner=1" +
            " ${conditions}")
    List<TbReportOverdueDetailBean> getReportOverdueDetail(@Param(value = "conditions") String conditions);
}
