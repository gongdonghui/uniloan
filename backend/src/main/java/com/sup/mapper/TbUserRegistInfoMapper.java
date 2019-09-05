package com.sup.mapper;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.sup.bean.ActivityInfoBean;
import com.sup.bean.TbUserRegistInfoBean;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * Created by xidongzhou1 on 2019/8/30.
 */
public interface TbUserRegistInfoMapper extends BaseMapper<TbUserRegistInfoBean> {
}
