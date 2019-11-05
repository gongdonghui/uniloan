package com.sup.core.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.google.common.base.Strings;
import com.sup.common.util.GsonUtil;
import com.sup.core.bean.BlackListBean;
import com.sup.core.mapper.BlackListMapper;
import com.sup.core.status.BlackListStatusEnum;
import com.sup.core.util.HttpClient;
import lombok.extern.log4j.Log4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Value;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

/**
 * gongshuai
 * <p>
 * 2019/11/3
 */
@Log4j
@Service
public class ThirdPartyService {

    @Value("${thirdparty.jirong.secretKey}")
    private String jirongSecretKey;
    @Value("${thirdparty.jirong.blacklist.url}")
    private String jirongBlackListUrl;
    @Value("${thirdparty.jirong.token}")
    private String jirongToken;
    @Value("${thirdparty.jirong.countryCode}")
    private String jirongCountryCode;

    @Value("${thirdparty.xingtan.blacklist.url}")
    private String xingtanBlackListUrl;
    @Value("${thirdparty.xingtan.token}")
    private String xingtanToken;

    @Value("#{new Integer('${thirdparty.max-retry}')}")
    private Integer QUERY_RETRY_NUM;


    @Autowired
    private BlackListMapper blackListMapper;


    public boolean checkBlackListInJirong(String id, String name, String phone, String apply_id) {
        if (Strings.isNullOrEmpty(id + name + phone)) {
            log.error("Param is null!");
            return false;
        }
        BlackListBean blackListBean = hitLocalBlackList(id, name, phone);
        boolean needUpdate = false;
        if (blackListBean != null) {
            needUpdate = true;
            if (blackListBean.getStatus() == BlackListStatusEnum.BL_BLACK.getCode()) {
                return true;
            }
        } else {
            blackListBean = new BlackListBean();
        }

        Map<String, String> params = new HashMap<String, String>();
        params.put("token", jirongToken);
        params.put("secretKey", jirongSecretKey);
        params.put("idNumber", id);
        params.put("name", name);
        params.put("countryCode", jirongCountryCode);
        params.put("number", phone);
        params.put("areaCode", "");
        try {
            String response = null;
            JSONObject jsonObject = null;
            for (int i = 1; i <= QUERY_RETRY_NUM; ++i) {
                response = HttpClient.httpPostWithParams(jirongBlackListUrl, params);
                jsonObject = (JSONObject) JSON.parse(response);
                if (jsonObject.getString("status").toUpperCase().equals("SUCCESS")) {
                    break;
                }
                Thread.sleep(1000 * i);
            }
            if (!jsonObject.getString("status").toUpperCase().equals("SUCCESS")) {
                log.error("Failed to query blacklist(jirong), param: " + GsonUtil.toJson(params)
                        + ", response: " + GsonUtil.toJson(response));
                return false;
            }
            log.info("Query jirong response: " + response);
            String ret = jsonObject.getString("hitResult").toUpperCase();
            boolean isBlack = false;

            if (!Strings.isNullOrEmpty(apply_id)) {
                blackListBean.setApply_id(Integer.valueOf(apply_id));
            }
            blackListBean.setCid_no(id);
            blackListBean.setName(name);
            blackListBean.setMobile(phone);
            if (ret.equals("PASS")) {
                blackListBean.setStatus(BlackListStatusEnum.BL_NORMAL.getCode());
            } else if (ret.equals("REJECT")) {
                isBlack = true;
                blackListBean.setStatus(BlackListStatusEnum.BL_BLACK.getCode());
            } else {
                blackListBean.setStatus(BlackListStatusEnum.BL_GRAY.getCode());
            }
            blackListBean.setPlatform("JIRONG");
            blackListBean.setOrigin_message(response);
            blackListBean.setCreate_time(new Date());
            if (needUpdate) {
                this.blackListMapper.updateById(blackListBean);
            } else {
                this.blackListMapper.insert(blackListBean);
            }
            return isBlack;
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean checkBlackListInXingtan(String id, String name, String phone, String apply_id) {
        if (Strings.isNullOrEmpty(id + name + phone)) {
            log.error("Param is null!");
            return false;
        }
        BlackListBean blackListBean = hitLocalBlackList(id, name, phone);
        boolean needUpdate = false;
        if (blackListBean != null) {
            needUpdate = true;
            if (blackListBean.getStatus() == BlackListStatusEnum.BL_BLACK.getCode()) {
                return true;
            }
        } else {
            blackListBean = new BlackListBean();
        }

        Map<String, String> headerParams = new HashMap<String, String>();
        Map<String, String> params = new HashMap<String, String>();
        headerParams.put("Content-Type", "application/x-www-form-urlencoded; charset=utf-8");
        headerParams.put("X-AUTH-TOKEN", xingtanToken);
        if (!Strings.isNullOrEmpty(phone)) {
            params.put("mobile", "0" + phone);
            // if (phone.charAt(0) == '0') {
            //     params.put("mobile", phone);
            // } else {
            //     params.put("mobile", "0" + phone);
            // }
        }
        if (!Strings.isNullOrEmpty(name)) {
            params.put("realName", name);
        }
        if (!Strings.isNullOrEmpty(id)) {
            params.put("credentialNo", id);
        }
        try {
            String response = null;
            JSONObject jsonObject = null;
            for (int i = 1; i < QUERY_RETRY_NUM; ++i) {
                response = HttpClient.httpPostWithParams(xingtanBlackListUrl, params, headerParams);
                jsonObject = (JSONObject) JSON.parse(response);
                if (!jsonObject.containsKey("code") || !jsonObject.getString("code").toLowerCase().equals("fail")) {
                    break;
                }
                Thread.sleep(1000 * i);
            }
            if (jsonObject.containsKey("code") && jsonObject.getString("code").toLowerCase().equals("fail")) {
                log.error("Failed to query blacklist(xingtan), param: " + GsonUtil.toJson(params)
                        + ", response: " + GsonUtil.toJson(response));
                return false;
            }
            log.info("Query xingtan response: " + response);
            boolean isBlack = false;
            JSONArray detailList = jsonObject.getJSONArray("reportDetailList");
            for (int i = 0; i < detailList.size(); ++i) {
                JSONObject obj = detailList.getJSONObject(i);
                if (obj.containsKey("reportType") && obj.getString("reportType").toUpperCase().equals("PRC_BLACKLIST")) {
                    isBlack = obj.containsKey("resultData") && obj.getString("resultData").toUpperCase().equals("HIT");
                    break;
                }
            }

            if (!Strings.isNullOrEmpty(apply_id)) {
                blackListBean.setApply_id(Integer.valueOf(apply_id));
            }
            blackListBean.setCid_no(id);
            blackListBean.setName(name);
            blackListBean.setMobile(phone);
            blackListBean.setStatus(isBlack ? BlackListStatusEnum.BL_BLACK.getCode() : BlackListStatusEnum.BL_NORMAL.getCode());
            blackListBean.setPlatform("XINGTAN");
            blackListBean.setOrigin_message(response);
            blackListBean.setCreate_time(new Date());
            if (needUpdate) {
                this.blackListMapper.updateById(blackListBean);
            } else {
                this.blackListMapper.insert(blackListBean);
            }
            return isBlack;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }


    private BlackListBean hitLocalBlackList(String cid, String name, String mobile) {
        QueryWrapper<BlackListBean> wrapper = new QueryWrapper<>();
        if (!Strings.isNullOrEmpty(cid)) {
            wrapper.eq("cid_no", cid);
        }
        if (!Strings.isNullOrEmpty(name)) {
            wrapper.eq("name", name);
        }
        if (!Strings.isNullOrEmpty(mobile)) {
            wrapper.eq("mobile", mobile);
        }
        wrapper.ge("expire_time", new Date());
        wrapper.eq("status", BlackListStatusEnum.BL_BLACK.getCode());
        return blackListMapper.selectOne(wrapper);
    }
}
