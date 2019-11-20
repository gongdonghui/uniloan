package com.sup.core.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.google.common.base.Function;
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

    class JirongPhoneParam {
        String countryCode;
        String areaCode;
        String number;
        public JirongPhoneParam(String cc, String ac, String nb) {
            this.countryCode = cc;
            this.areaCode = ac;
            this.number = nb;
        }
    }

    public boolean checkBlackListInJirong(String id, String name, String phone, String apply_id) {
        if (Strings.isNullOrEmpty(id + name + phone)) {
            log.error("Param is null!");
            return false;
        }
        if (hitLocalBlackList(id, name, phone)) {
            return true;
        }

        Map<String, String> params = new HashMap<String, String>();
        params.put("token", jirongToken);
        params.put("secretKey", jirongSecretKey);
        params.put("idNumber", id);
        if (!Strings.isNullOrEmpty(name)) {
            params.put("name", name.toUpperCase());
        }
        if (!Strings.isNullOrEmpty(phone)) {
            params.put("phoneNumber", GsonUtil.toJson(new JirongPhoneParam(jirongCountryCode, "", phone)));
        }
        // params.put("countryCode", jirongCountryCode);
        // params.put("number", phone);
        // params.put("areaCode", "");
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
            BlackListBean blackListBean = new BlackListBean();
            if (!Strings.isNullOrEmpty(apply_id)) {
                blackListBean.setApply_id(Integer.valueOf(apply_id));
            }
            blackListBean.setCid_no(id);
            blackListBean.setName(name.toUpperCase());
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
            if (isBlack) {
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
        if (hitLocalBlackList(id, name, phone)) {
            return true;
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

            BlackListBean blackListBean = new BlackListBean();
            if (!Strings.isNullOrEmpty(apply_id)) {
                blackListBean.setApply_id(Integer.valueOf(apply_id));
            }
            blackListBean.setCid_no(id);
            blackListBean.setName(name.toUpperCase());
            blackListBean.setMobile(phone);
            blackListBean.setStatus(isBlack ? BlackListStatusEnum.BL_BLACK.getCode() : BlackListStatusEnum.BL_NORMAL.getCode());
            blackListBean.setPlatform("XINGTAN");
            blackListBean.setOrigin_message(response);
            blackListBean.setCreate_time(new Date());
            if (isBlack) {
                this.blackListMapper.insert(blackListBean);
            }
            return isBlack;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }


    private boolean hitLocalBlackList(String cid, String name, String mobile) {
        QueryWrapper<BlackListBean> wrapper = new QueryWrapper<>();
        QueryWrapper<BlackListBean> subWrapper = new QueryWrapper<>();
        // TODO: using or ??
        if (!Strings.isNullOrEmpty(cid)) {
            subWrapper.or().eq("cid_no", cid);
        }
        if (!Strings.isNullOrEmpty(name)) {
            subWrapper.or().eq("name", name.toUpperCase());
        }
        if (!Strings.isNullOrEmpty(mobile)) {
            subWrapper.or().eq("mobile", mobile);
        }
        wrapper.ge("expire_time", new Date());
        wrapper.eq("status", BlackListStatusEnum.BL_BLACK.getCode());
        wrapper.and(
                new Function<QueryWrapper<BlackListBean>, QueryWrapper<BlackListBean>>() {
                    @Override
                    public QueryWrapper<BlackListBean> apply(QueryWrapper<BlackListBean> blackListBeanQueryWrapper) {
                        return subWrapper;
                    }
                }
        );
        // log.info("hitLocalBlackList sql: " + wrapper.getSqlSegment());
        //      expire_time >= #{ew.paramNameValuePairs.MPGENVAL1}
        //          AND status = #{ew.paramNameValuePairs.MPGENVAL2}
        //          AND ( cid_no = #{ew.paramNameValuePairs.MPGENVAL1} OR mobile = #{ew.paramNameValuePairs.MPGENVAL2} )
        List<BlackListBean> beans = blackListMapper.selectList(wrapper);
        return beans != null && beans.size() > 0;
    }
}
