package com.sup.core.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.sup.common.bean.OperationTaskConfigBean;
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
            "left join (select * from  tb_cms_operation_task_config  where credit_level=${creditLevel}  and enable =1) as b" +
            "on a.group_id =b.group_id")
    public List<Integer> getOperatorsByLevel(Integer creditLevel);
}
