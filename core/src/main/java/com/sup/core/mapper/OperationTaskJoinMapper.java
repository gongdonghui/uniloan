package com.sup.core.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.sup.common.bean.OperationTaskJoinBean;
import com.sup.core.bean.CollectionRepayBean;
import com.sup.core.bean.CollectionStatBean;
import com.sup.core.bean.RepayJoinBean;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * gongshuai
 * <p>
 * 2019/10/14
 */
public interface OperationTaskJoinMapper extends BaseMapper {


    @Select("select " +
            "a.id as id, a.apply_id as applyId, a.create_time as createTime, a.task_type as taskType," +
            "a.status as taskStatus, a.operator_id as operatorId,a.has_owner as  hasOwner," +
            "a.update_time as updateTime," +
            "b.status as applyStatus, a.operator_id as  operatorId, b.grant_quota as  loanAmt" +
            " from tb_operation_task a" +
            " join tb_apply_info b on a.apply_id=b.id" +
            " where a.task_type= ${taskType}" +
            "  and a.create_time >='${start}' and a.create_time< '${end}' ")
    List<OperationTaskJoinBean> getOperationTaskJoinByTask(String start, String  end, Integer taskType );


    @Select("select " +
            " a.apply_id as applyId " +
            "a.create_time as createTime," +
            "b.replay_sum as repay_amt" +
            " from   tb_cms_collection_record a" +
            " join " +
            "( select  apply_id, sum(replay_amount)  as repay_sum from   tb_repay_history " +
            " where   repay_time > '${start}' and  repay_time < '${end}' and repay_status =1 group by apply_id) as b " +
            " on a.apply_id=b.apply_id" +
            " where  a.create_time >='${start}' and a.create_time< '${end}' ")
    List<CollectionRepayBean> getCollectionRepay(String start, String  end);

    @Select("select " +
            "a.apply_id as applyId, a.create_time as create_time, " +
            "b.need_total  as  total,   b.act_total as  total_actual" +
            " from tb_operation_task a" +
            " join tb_repay_stat b on a.apply_id=b.apply_id" +
            " where a.task_type= ${taskType}" +
            " and create_time< '${end}' ")
    List<CollectionStatBean>  getCollectionStats(String end, Integer taskType);

    @Select("select " +
            "a.repay_start_date  as  repay_start_date, a.repay_end_date  as repay_end_date, " +
            "b.loan_time as loan_time ,c.operator_id  as  operator_id, c.task_type   as  task_type" +
            "from (select * from  tb_repay_plan  where is_overdue = 1 and  repay_start_date >='${start}'  and   repay_start_date <'${end}')as  a " +
            " left join tb_apply_info as b on a.apply_id=b.id" +
            " left join  tb_operation_task as c on  a.apply_id = c.apply_id ")
    List<RepayJoinBean>  getRepayJoinByDate(String start , String end );


}
