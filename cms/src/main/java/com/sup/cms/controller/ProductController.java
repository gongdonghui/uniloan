package com.sup.cms.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.google.common.collect.Maps;
import com.sup.cms.bean.po.ProductInfoBean;
import com.sup.cms.bean.vo.ProductGetListParams;
import com.sup.cms.mapper.ProductInfoMapper;
import com.sup.common.util.GsonUtil;
import com.sup.common.util.ResponseUtil;
import lombok.extern.log4j.Log4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 产品页面
 *
 * @Author: kouichi
 * @Date: 2019/9/18 16:33
 */
@Log4j
@RequestMapping("/product")
@RestController
public class ProductController {

    @Autowired
    private ProductInfoMapper productInfoMapper;

    @PostMapping("/getList")
    public String getList(@Valid @RequestBody ProductGetListParams params) {
        Map result = Maps.newHashMap();
        QueryWrapper<ProductInfoBean> qw = new QueryWrapper<>();
        Map m = Maps.newHashMap();
        if (params.getName() != null) {
            m.put("name", params.getName());
        }
        if (params.getStatus() != null) {
            m.put("status", params.getStatus());
        }
        if (m.size() > 0) {
            qw.allEq(m);
        }
        result.put("total", productInfoMapper.selectCount(qw));
        Integer offset = (params.getPage() - 1) * params.getPageSize();
        Integer rows = params.getPageSize();

        qw.last("limit " + offset + "," + rows);
        List<ProductInfoBean> list = productInfoMapper.selectList(qw);
        result.put("list", list);
        return ResponseUtil.success(result);
    }

    @PostMapping("/insert")
    public String insert(@Valid @RequestBody ProductInfoBean bean) {
        log.info("insert product: " + GsonUtil.toJson(bean));
        bean.setCreateTime(new Date());
        if (productInfoMapper.insert(bean) > 0) {
            return ResponseUtil.success();
        } else {
            return ResponseUtil.failed();
        }
    }

    @PostMapping("/update")
    public String update(@Valid @RequestBody ProductInfoBean bean) {
        ProductInfoBean oldBean = productInfoMapper.selectById(bean.getId());
        log.info("update product, old bean: " + GsonUtil.toJson(oldBean));
        log.info("update product, new bean: " + GsonUtil.toJson(bean));
        if (productInfoMapper.updateById(bean) > 0) {
            return ResponseUtil.success();
        } else {
            return ResponseUtil.failed();
        }
    }

    @GetMapping("/delete")
    public String delete(@RequestParam("id") Integer id) {
        ProductInfoBean bean = productInfoMapper.selectById(id);
        log.info("delete bean: " + GsonUtil.toJson(bean));
        if (productInfoMapper.deleteById(id) > 0) {
            return ResponseUtil.success();
        } else {
            return ResponseUtil.failed();
        }
    }

}
