package com.sup.core.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.sup.common.bean.OperationTaskConfigBean;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * gongshuai
 * <p>
 * 2020/2/22
 */
public interface OperationTaskConfigBeanMapper extends BaseMapper<OperationTaskConfigBean> {

    // @Select("select a.id   from tb_cms_auth_user as a  left join (select * from  tb_cms_operation_task_config  where asset_level=${asset_level}  and enabled =1) as b  on a.group_id =b.group_id ")
    @Select("select b.id  from (select * from  tb_cms_operation_task_config   where  enabled =1 and   asset_level=${asset_level} ) as a  left  join  tb_cms_auth_user as b   on   b.group_id =  a.group_id;")
    public List<Integer> getOperatorsByLevel(@Param(value = "asset_level") Integer asset_level);
}
