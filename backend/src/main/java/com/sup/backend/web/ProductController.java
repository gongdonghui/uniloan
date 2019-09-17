package com.sup.backend.web;

import com.sup.backend.bean.LoginInfoCtx;
import com.sup.backend.core.LoginInfo;
import com.sup.backend.core.LoginRequired;
import com.sup.backend.mapper.TbProductInfoMapper;
import com.sup.common.bean.TbProductInfoBean;
import com.sup.common.util.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Created by xidongzhou1 on 2019/9/17.
 */

@RestController
@RequestMapping(value = "/product")
public class ProductController {
  @Autowired
  TbProductInfoMapper tb_product_info_mapper;

  @LoginRequired
  @ResponseBody
  @RequestMapping(value = "list", produces = "application/json;charset=UTF-8")
  public Object getProduct(@LoginInfo LoginInfoCtx li) {
    List<TbProductInfoBean> beans = tb_product_info_mapper.selectList(null);
    return Result.succ(beans);
  }
}
