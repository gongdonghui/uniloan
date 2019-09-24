package com.sup.backend.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.sup.common.bean.TbUserRegistInfoBean;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * Created by xidongzhou1 on 2019/8/30.
 */
public interface TbUserRegistInfoMapper extends BaseMapper<TbUserRegistInfoBean> {
  @Select("update tb_user_regist_info set name = ${name} where id = ${id}")
  void updateName(@Param("id") Integer user_id,  @Param("name") String name);
}
