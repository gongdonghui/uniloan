package com.sup.cms.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.google.common.collect.Maps;
import com.sup.cms.bean.po.ProductInfoBean;
import com.sup.cms.bean.vo.ProductGetListParams;
import com.sup.cms.mapper.ProductInfoMapper;
import com.sup.cms.util.ResponseUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Map;

/**
 * 产品页面
 *
 * @Author: kouichi
 * @Date: 2019/9/18 16:33
 */
@RequestMapping("/product")
@RestController
public class ProductController {

    @Autowired
    private ProductInfoMapper productInfoMapper;

    @PostMapping("/getList")
    public String getList(@Valid @RequestBody ProductGetListParams params) {
        QueryWrapper<ProductInfoBean> qw = new QueryWrapper<>();
        Map m = Maps.newHashMap();
        m.put("name", params.getName());
        m.put("status", params.getStatus());
        qw.allEq(m);
        Integer offset = (params.getPage() - 1) * params.getPageSize();
        Integer rows = params.getPageSize();
        qw.last("limit " + offset + "," + rows);
        List<ProductInfoBean> list = productInfoMapper.selectList(qw);
        return ResponseUtil.success(list);
    }

    @PostMapping("/insert")
    public String insert(@Valid @RequestBody ProductInfoBean bean) {
        if (productInfoMapper.insert(bean) > 0) {
            return ResponseUtil.success();
        } else {
            return ResponseUtil.failed();
        }
    }

    @PostMapping("/update")
    public String update(@Valid @RequestBody ProductInfoBean bean) {
        if (productInfoMapper.updateById(bean) > 0) {
            return ResponseUtil.success();
        } else {
            return ResponseUtil.failed();
        }
    }

    @GetMapping("/delete")
    public String delete(@RequestParam("id") Integer id) {
        if (productInfoMapper.deleteById(id) > 0) {
            return ResponseUtil.success();
        } else {
            return ResponseUtil.failed();
        }
    }

}
