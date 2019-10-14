package com.sup.core.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
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
}
