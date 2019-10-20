package com.sup.core.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.sup.core.bean.CollectionRepayBean;
import com.sup.core.bean.CollectionStatBean;
import com.sup.core.bean.OperationTaskJoinBean;
import org.apache.ibatis.annotations.Select;

import java.util.Date;
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
            "b.status as applyStatus" +
            " from tb_operation_task a" +
            " join tb_apply_info b on a.apply_id=b.id" +
            " where a.task_type= ${task_type}" +
            "  and a.create_time >=${start} and a.create_time< ${end} ")
    List<OperationTaskJoinBean> getOperationTaskJoinByTask(Date start, Date  end, Integer   tasktype );


    @Select("select " +
            " a.apply_id as applyId " +
            "a.create_time as createTime," +
            "b.replay_sum as repay_amt" +
            " from   tb_cms_collection_record a" +
            " join " +
            "( select  apply_id, sum(replay_amount)  as repay_sum from   tb_repay_history " +
            " where   repay_time > ${start} and  repay_time < ${end} and repay_status =1 group by apply_id) as b " +
            " on a.apply_id=b.apply_id" +
            " where  a.create_time >=${start} and a.create_time< ${end} ")
    List<CollectionRepayBean> getCollectionRepay(Date start, Date  end);

    @Select("select " +
            "a.apply_id as applyId, a.create_time as create_time, " +
            "b.need_total  as  total,   b.act_total as  total_actual" +
            " from tb_operation_task a" +
            " join tb_repay_stat b on a.apply_id=b.apply_id" +
            " where a.task_type= ${task_type}" +
            " and create_time< ${end} ")
    List<CollectionStatBean>  getCollectionStats(Date end, Integer tasktype);

}
