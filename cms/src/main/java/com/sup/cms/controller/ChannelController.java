package com.sup.cms.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.google.common.base.Strings;
import com.google.common.collect.Maps;
import com.sup.cms.bean.po.ProductInfoBean;
import com.sup.cms.bean.vo.ChannelInfoGetListParams;
import com.sup.cms.bean.vo.ProductGetListParams;
import com.sup.cms.mapper.ProductInfoMapper;
import com.sup.cms.mapper.TbChannelInfoMapper;
import com.sup.cms.mapper.TbChannelProductMapper;
import com.sup.cms.util.ResponseUtil;
import com.sup.common.bean.TbChannelInfoBean;
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
        Integer offset = (params.getPage() - 1) * params.getPageSize();
        Integer rows = params.getPageSize();

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

}
