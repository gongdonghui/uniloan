package com.sup.cms.controller;

import com.google.common.base.Strings;
import com.google.common.collect.Maps;
import com.sup.cms.bean.po.AfterLoanOverdueGetListBean;
import com.sup.cms.bean.po.CustomerInfoBean;
import com.sup.cms.bean.vo.AfterLoanOverdueGetListParams;
import com.sup.cms.bean.vo.CustomerGetListParams;
import com.sup.cms.mapper.CrazyJoinMapper;
import com.sup.cms.util.DateUtil;
import com.sup.cms.util.ResponseUtil;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;
import java.util.Map;

/**
 * @Author: kouichi
 * @Date: 2019/10/13 19:47
 */
@RestController
@RequestMapping("/customer")
public class CustomerController {

    @Autowired
    private CrazyJoinMapper crazyJoinMapper;

    @PostMapping("/getList")
    public String getCustomers(@RequestBody @Valid CustomerGetListParams params) {
        StringBuilder sb = new StringBuilder();

        sb.append(!Strings.isNullOrEmpty(params.getName()) ? " and uri.name like \"%" + params.getName() + "%\"" : "");
        sb.append(!Strings.isNullOrEmpty(params.getCidNo()) ? " and cid.cid_no=\"" + params.getCidNo() + "\"" : "");
        sb.append(!Strings.isNullOrEmpty(params.getMobile()) ? " and uri.mobile=\"" + params.getMobile() + "\"" : "");

        sb.append(params.getRegistStartDate() != null ? " and uri.create_time >= \"" + DateUtil.formatDateTime(params.getRegistStartDate()) + "\"" : "");
        sb.append(params.getRegistEndDate() != null ? " and uri.create_time <= \"" + DateUtil.formatDateTime(params.getRegistEndDate()) + "\"" : "");

        Integer offset = (params.getPage() - 1) * params.getPageSize();
        Integer rows = params.getPageSize();
        List<CustomerInfoBean> l = crazyJoinMapper.getCustomers(sb.toString(), offset, rows);
        Map m = Maps.newHashMap();
        m.put("total",crazyJoinMapper.getCustomersCount(sb.toString()));
        m.put("list",l);
        return ResponseUtil.success(m);
    }
}
