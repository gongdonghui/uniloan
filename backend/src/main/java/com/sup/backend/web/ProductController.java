package com.sup.backend.web;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.sup.backend.bean.AppProductInfo;
import com.sup.backend.bean.LoginInfoCtx;
import com.sup.backend.core.LoginInfo;
import com.sup.backend.core.LoginRequired;
import com.sup.backend.mapper.TbChannelProductMapper;
import com.sup.backend.mapper.TbProductInfoMapper;
import com.sup.backend.mapper.TbUserRegistInfoMapper;
import com.sup.backend.util.ToolUtils;
import com.sup.common.bean.TbChannelProductBean;
import com.sup.common.bean.TbProductInfoBean;
import com.sup.common.util.Result;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

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

  @Autowired
  TbChannelProductMapper tb_channel_product_mapper;

  @LoginRequired
  @ResponseBody
  @RequestMapping(value = "list", produces = "application/json;charset=UTF-8")
  public Object getProduct(@LoginInfo LoginInfoCtx li, HttpServletRequest req) {
    if (li == null) {
      List<AppProductInfo> r = tb_channel_product_mapper.selectList(new QueryWrapper<TbChannelProductBean>().eq("channel_id", 1)).stream().map(x -> tb_product_info_mapper.selectById(x.getProduct_id())).map(bean -> {
        AppProductInfo api = new AppProductInfo();
        api.setId(bean.getId());
        api.setName(bean.getName());
        api.setMin_period(bean.getMin_period());
        api.setMax_period(bean.getMax_period());
        api.setMin_quota(bean.getMin_quota());
        api.setMax_quota(bean.getMax_quota());
        api.setRate(ToolUtils.formatRate(bean.getRate()));
        api.setStatus(1);
        api.setPeriod_type(bean.getPeriod_type());
        api.setDesc(bean.getProduct_desc());
        api.setMaterial_needed(bean.getMaterial_needed());
        return api;
      }).collect(Collectors.toList());
      return ToolUtils.succ(r);
    }

    int credit_level = tb_user_regist_info_mapper.selectById(li.getUser_id()).getCredit_level();
    List<TbProductInfoBean> beans = tb_product_info_mapper.selectList(new QueryWrapper<TbProductInfoBean>().eq("status", 1));

    int channel = -1;
    if (StringUtils.isNotEmpty(req.getHeader("channel-id"))) {
      channel = Integer.parseInt(req.getHeader("channel-id"));
    }
    if (beans == null) {
      beans = new ArrayList<>();
    }

    final int f_channel_id = channel;
    List<AppProductInfo> ret_beans = beans.stream().filter(bean -> {
      if (f_channel_id < 0) {
        return true;
      }
      return (null != tb_channel_product_mapper.selectOne(new QueryWrapper<TbChannelProductBean>().eq("channel_id", f_channel_id).eq("product_id", bean.getId()).last("limit 1")));
    }).map(bean -> {
      AppProductInfo api = new AppProductInfo();
      api.setId(bean.getId());
      api.setName(bean.getName());
      api.setMin_period(bean.getMin_period());
      api.setMax_period(bean.getMax_period());
      api.setMin_quota(bean.getMin_quota());
      api.setMax_quota(bean.getMax_quota());
      api.setRate(ToolUtils.formatRate(bean.getRate()));
      api.setStatus(bean.getCredit_level().compareTo(credit_level) <= 0 ? 1: 0);
      api.setPeriod_type(bean.getPeriod_type());
      api.setDesc(bean.getProduct_desc());
      api.setMaterial_needed(bean.getMaterial_needed());
      return api;
    }).collect(Collectors.toList());
    return ToolUtils.succ(ret_beans);
  }
}
