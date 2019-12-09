package com.sup.cms.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.google.common.collect.Maps;
import com.sup.cms.bean.po.LoanRepayInfoGetListBean;
import com.sup.cms.bean.po.LoanUnRepayInfoGetListBean;
import com.sup.cms.bean.vo.LoanRepayInfoGetListParams;
import com.sup.cms.bean.vo.LoanUnRepayInfoGetListParams;
import com.sup.cms.mapper.CrazyJoinMapper;
import com.sup.cms.mapper.TbAppSdkAppListInfoMapper;
import com.sup.cms.mapper.TbAppSdkContractInfoMapper;
import com.sup.cms.util.ResponseUtil;
import com.sup.cms.util.ToolUtils;
import com.sup.common.bean.TbAppSdkAppListInfoBean;
import com.sup.common.bean.TbAppSdkContractInfoBean;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 贷款管理下面的子页面
 *
 * @Author: kouichi
 * @Date: 2019/10/13 17:41
 */
@Slf4j
@RestController
@RequestMapping("/sdk")
public class SDKController {

    @Autowired
    TbAppSdkContractInfoMapper tb_app_sdk_contract_mapper;

    @Autowired
    TbAppSdkAppListInfoMapper tb_app_sdk_app_list_info_mapper;

    @ResponseBody
    @RequestMapping(value = "/contact/get", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public Object QueryContract(@RequestParam("mobile") String mobile) {
        return ToolUtils.succ(QueryLatestContact(mobile));
    }

    @ResponseBody
    @RequestMapping(value = "/applist/get", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public Object QueryApplist(@RequestParam("mobile") String mobile) {
        return ToolUtils.succ(QueryLatestAppList(mobile));
    }

    private List<TbAppSdkContractInfoBean> QueryLatestContact(String mobile) {
        List<TbAppSdkContractInfoBean> result = new ArrayList<>();
        TbAppSdkContractInfoBean first_bean = tb_app_sdk_contract_mapper.selectOne(new QueryWrapper<TbAppSdkContractInfoBean>().eq("mobile", mobile).orderByDesc("id").last("limit 1"));
        if (first_bean == null) {
            return result;
        }
        QueryWrapper<TbAppSdkContractInfoBean> query = new QueryWrapper<TbAppSdkContractInfoBean>().eq("mobile", mobile).eq("create_time", first_bean.getCreate_time()).orderByAsc("id");
        result = tb_app_sdk_contract_mapper.selectList(query);
        result.forEach(v -> v.setSignature(v.calcSignature()));
        return result;
    }

    private List<TbAppSdkAppListInfoBean> QueryLatestAppList(String mobile) {
        List<TbAppSdkAppListInfoBean> result = new ArrayList<>();
        TbAppSdkAppListInfoBean first_bean = tb_app_sdk_app_list_info_mapper.selectOne(new QueryWrapper<TbAppSdkAppListInfoBean>().eq("mobile", mobile).orderByDesc("id").last("limit 1"));
        if (first_bean == null) {
            return result;
        }
        QueryWrapper<TbAppSdkAppListInfoBean> query = new QueryWrapper<TbAppSdkAppListInfoBean>().eq("mobile", mobile).eq("create_time", first_bean.getCreate_time()).orderByAsc("id");
        result = tb_app_sdk_app_list_info_mapper.selectList(query);
        result.forEach(v -> v.setSignature(v.calcSignature()));
        return result;
    }
}
