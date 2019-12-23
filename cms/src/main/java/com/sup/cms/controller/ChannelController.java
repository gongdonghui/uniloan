package com.sup.cms.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.google.common.base.Strings;
import com.google.common.collect.Maps;
import com.sup.cms.bean.po.ProductInfoBean;
import com.sup.cms.bean.vo.ChannelInfoGetListParams;
import com.sup.cms.bean.vo.ChannelProductParams;
import com.sup.cms.bean.vo.ProductGetListParams;
import com.sup.cms.mapper.ProductInfoMapper;
import com.sup.cms.mapper.TbChannelInfoMapper;
import com.sup.cms.mapper.TbChannelProductMapper;
import com.sup.cms.util.GsonUtil;
import com.sup.cms.util.ResponseUtil;
import com.sup.common.bean.TbChannelInfoBean;
import com.sup.common.bean.TbChannelProductBean;
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
@RequestMapping("/channel")
@RestController
public class ChannelController {

    @Autowired
    private TbChannelInfoMapper channelInfoMapper;

    @Autowired
    private TbChannelProductMapper channelProductMapper;


    @PostMapping("/getList")
    public String getList(@Valid @RequestBody ChannelInfoGetListParams params) {
        Map result = Maps.newHashMap();
        QueryWrapper<TbChannelInfoBean> qw = new QueryWrapper<>();
        Map m = Maps.newHashMap();
        if (!Strings.isNullOrEmpty(params.getName())) {
            m.put("name", params.getName());
        }
        if (params.getStatus() != null) {
            m.put("status", params.getStatus());
        }
        if (m.size() > 0) {
            qw.allEq(m);
        }
        result.put("total", channelInfoMapper.selectCount(qw));
        Integer offset = Math.max((params.getPage() - 1) * params.getPageSize(), 0);
        Integer rows = Math.max(params.getPageSize(), 1);

        qw.last("limit " + offset + "," + rows);
        List<TbChannelInfoBean> list = channelInfoMapper.selectList(qw);
        result.put("list", list);
        return ResponseUtil.success(result);
    }

    @PostMapping("/insert")
    public String insert(@Valid @RequestBody TbChannelInfoBean bean) {
        bean.setCreate_time(new Date());
        if (channelInfoMapper.insert(bean) > 0) {
            return ResponseUtil.success();
        } else {
            return ResponseUtil.failed();
        }
    }

    @PostMapping("/update")
    public String update(@Valid @RequestBody TbChannelInfoBean bean) {
        if (channelInfoMapper.updateById(bean) > 0) {
            return ResponseUtil.success();
        } else {
            return ResponseUtil.failed();
        }
    }

    @GetMapping("/delete")
    public String delete(@RequestParam("id") Integer id) {
        if (channelInfoMapper.deleteById(id) > 0) {
            return ResponseUtil.success();
        } else {
            return ResponseUtil.failed();
        }
    }

    @PostMapping("/addProduct")
    public String addProduct(@Valid @RequestBody ChannelProductParams param) {
        log.info("addProduct param:" + GsonUtil.toJson(param));
        TbChannelProductBean bean = new TbChannelProductBean();
        bean.setChannel_id(param.getChannelId());
        bean.setCreate_time(new Date());
        for (Integer productId: param.getProductIdList()) {
            bean.setId(null);
            bean.setProduct_id(productId);
            if (channelProductMapper.insert(bean) <= 0) {
                log.error("failed to addProduct, bean=" + GsonUtil.toJson(bean));
                return ResponseUtil.failed();
            }
        }
        return ResponseUtil.success();
    }

    @PostMapping("/deleteProduct")
    public String deleteProduct(@Valid @RequestBody ChannelProductParams param) {
        log.info("deleteProduct param:" + GsonUtil.toJson(param));
        for (Integer productId: param.getProductIdList()) {
            QueryWrapper<TbChannelProductBean> wrapper = new QueryWrapper<>();
            wrapper.eq("channel_id", param.getChannelId());
            wrapper.eq("product_id", productId);
            channelProductMapper.delete(wrapper);
        }
        return ResponseUtil.success();
    }

    @PostMapping("/getProduct")
    public String getProduct(@RequestParam("channelId") Integer channelId) {
        log.info("getProduct channelId:" + channelId);
        QueryWrapper<TbChannelProductBean> wrapper = new QueryWrapper<>();
        wrapper.eq("channel_id", channelId);
        List<TbChannelProductBean> productBeans = channelProductMapper.selectList(wrapper);

        return ResponseUtil.success(productBeans);
    }
}
