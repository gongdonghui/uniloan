package com.sup.core.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.sup.common.bean.OperationTaskConfigBean;
import com.sup.common.bean.TbOperationTaskBean;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * gongshuai
 * <p>
 * 2020/2/22
 */
public interface OperationTaskConfigBeanMapper extends BaseMapper<OperationTaskConfigBean> {

    @Select("select a.id " +
            "from tb_cms_auth_user as a" +
            "left join (select * from  tb_cms_operation_task_config  where credit_level=${creditLevel} ) as b" +
            "on a.group_id =b.group_id")
    public List<Integer> getOperatorsByLevel(Integer creditLevel);
    @Select("select  * from  (select * from tb_apply_info where credit_level =${creditLevel}) as  b     left join   tb_operation_task as a   on  a.apply_id = b.id  where  a.operation_task=3")
    public List<TbOperationTaskBean>   getOperationTaskByLevel(Integer  creditLevel);
}
