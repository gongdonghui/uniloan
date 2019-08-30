package com.sup.service;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.sup.bean.ActivityInfoBean;
import com.sup.mapper.ActivityInfoMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by xidongzhou1 on 2019/8/30.
 */
@Service
@DS("test")
public class TestService {
  @Autowired
  ActivityInfoMapper act_mapper;

  public List<ActivityInfoBean> getAll() {
    return act_mapper.selectList(null);
  }

  public List<ActivityInfoBean> getCus() {
    return act_mapper.selectCus("activity_info", new QueryWrapper<ActivityInfoBean>().eq("id", 1));
  }
}
