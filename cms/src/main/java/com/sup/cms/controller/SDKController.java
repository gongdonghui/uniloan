package com.sup.cms.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.sup.cms.mapper.TbAppSdkAppListInfoMapper;
import com.sup.cms.mapper.TbAppSdkContractInfoMapper;
import com.sup.cms.mapper.TbApplyMaterialInfoMapper;
import com.sup.cms.util.ToolUtils;
import com.sup.common.bean.TbAppSdkAppListInfoBean;
import com.sup.common.bean.TbAppSdkContractInfoBean;
import com.sup.common.bean.TbApplyMaterialInfoBean;
import com.sup.common.loan.ApplyMaterialTypeEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

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

    @Autowired
    TbApplyMaterialInfoMapper tbApplyMaterialInfoMapper;

    @ResponseBody
    @RequestMapping(value = "/contact/get", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public Object QueryContract(@RequestParam("apply_id") Integer apply_id) {
        return ToolUtils.succ(getContact(apply_id));
    }

    @ResponseBody
    @RequestMapping(value = "/applist/get", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public Object QueryApplist(@RequestParam("apply_id") Integer apply_id) {
        return ToolUtils.succ(getAppList(apply_id));
    }

    private List<TbAppSdkContractInfoBean> getContact(Integer apply_id) {
        List<TbAppSdkContractInfoBean> result = new ArrayList<>();
        QueryWrapper<TbApplyMaterialInfoBean> wrapper = new QueryWrapper();
        wrapper.eq("apply_id", apply_id);
        wrapper.eq("info_type", ApplyMaterialTypeEnum.APPLY_MATERIAL_SDK_CONTACT_LIST);

        TbApplyMaterialInfoBean infoBean = tbApplyMaterialInfoMapper.selectOne(wrapper);
        if (infoBean == null) {
            return result;
        }
        String infoId = infoBean.getInfo_id();
        QueryWrapper<TbAppSdkContractInfoBean> contractWrapper = new QueryWrapper<>();
        contractWrapper.eq("info_id", infoId);
        result = tb_app_sdk_contract_mapper.selectList(contractWrapper);
        result.forEach(v -> v.setSignature(v.calcSignature()));
        return result;
    }

    private List<TbAppSdkAppListInfoBean> getAppList(Integer apply_id) {
        List<TbAppSdkAppListInfoBean> result = new ArrayList<>();
        QueryWrapper<TbApplyMaterialInfoBean> wrapper = new QueryWrapper();
        wrapper.eq("apply_id", apply_id);
        wrapper.eq("info_type", ApplyMaterialTypeEnum.APPLY_MATERIAL_SDK_APP_LIST);

        TbApplyMaterialInfoBean infoBean = tbApplyMaterialInfoMapper.selectOne(wrapper);
        if (infoBean == null) {
            return result;
        }
        String infoId = infoBean.getInfo_id();
        QueryWrapper<TbAppSdkAppListInfoBean> contractWrapper = new QueryWrapper<>();
        contractWrapper.eq("info_id", infoId);
        result = tb_app_sdk_app_list_info_mapper.selectList(contractWrapper);
        result.forEach(v -> v.setSignature(v.calcSignature()));
        return result;
    }
}
