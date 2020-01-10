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
            "  substr(ot.create_time,1,10) as task_dt" +
            "  ,ot.operator_id as operator_id" +
            "  ,cau.name as name" +
            "  ,ot.apply_id as apply_id" +
            "  ,ai.status as status" +
            "  ,ai.grant_quota as grant_quota" +
            "  ,rs.need_total as need_total" +
            "  ,rs.act_total as act_total" +
            "  ,rs.normal_repay as normal_repay" +
            "  ,(rs.need_total - rs.normal_repay) as overdue_amount" +
            "  ,(rs.act_total - rs.normal_repay) as recall_amount" +
            " from (" +
            "   select * from tb_operation_task where task_type=3 and has_owner=1" +
            " ) ot" +
            " left join tb_apply_info ai on ot.apply_id=ai.id" +
            " left join tb_repay_stat rs on ot.apply_id=rs.apply_id" +
            " left join tb_cms_auth_user cau on ot.operator_id = cau.id" +
            " where ot.has_owner=1" +
            " ${conditions}")
    List<TbReportOverdueDetailBean> getReportOverdueDetail(@Param(value = "conditions") String conditions);
}
