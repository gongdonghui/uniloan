package com.sup.backend.web;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.sup.backend.bean.AppProductInfo;
import com.sup.backend.bean.LoginInfoCtx;
import com.sup.backend.core.LoginInfo;
import com.sup.backend.core.LoginRequired;
import com.sup.backend.mapper.TbProductInfoMapper;
import com.sup.backend.mapper.TbUserRegistInfoMapper;
import com.sup.backend.util.ToolUtils;
import com.sup.common.bean.TbProductInfoBean;
import com.sup.common.util.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by xidongzhou1 on 2019/9/17.
 */

@RestController
@RequestMapping(value = "/product")
public class ProductController {
  @Autowired
  TbProductInfoMapper tb_product_info_mapper;

  @Autowired
  TbUserRegistInfoMapper tb_user_regist_info_mapper;

  @LoginRequired
  @ResponseBody
  @RequestMapping(value = "list", produces = "application/json;charset=UTF-8")
  public Object getProduct(@LoginInfo LoginInfoCtx li) {
    int credit_level = tb_user_regist_info_mapper.selectById(li.getUser_id()).getCredit_level();
    List<TbProductInfoBean> beans = tb_product_info_mapper.selectList(new QueryWrapper<TbProductInfoBean>().eq("status", 1));
    List<AppProductInfo> ret_beans = new ArrayList<>();
    beans.forEach(bean -> {
      AppProductInfo api = new AppProductInfo();
      api.setId(bean.getId());
      api.setName(bean.getName());
      api.setMin_period(bean.getMin_period());
      api.setMax_period(bean.getMax_period());
      api.setMin_quota(bean.getMin_quota());
      api.setMax_quota(bean.getMax_quota());
      api.setRate(bean.getRate());
      api.setStatus(bean.getCredit_level().equals(credit_level) ? 1: 0);
      api.setPeriod_type(bean.getPeriod_type());
      api.setDesc(bean.getProduct_desc());
      api.setMaterial_needed(bean.getMaterial_needed());
      ret_beans.add(api);
    });
    return ToolUtils.succ(ret_beans);
  }
}
