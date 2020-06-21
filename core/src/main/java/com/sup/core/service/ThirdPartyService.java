package com.sup.core.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.google.common.base.Strings;
import com.sup.common.util.GsonUtil;
import com.sup.common.bean.BlackListBean;
import com.sup.core.mapper.BlackListMapper;
import com.sup.core.status.BlackListStatusEnum;
import com.sup.core.util.HttpClient;
import com.sup.core.util.ToolUtils;
import lombok.extern.log4j.Log4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Value;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;
import java.util.concurrent.ExecutionException;

/**
 * gongshuai
 * <p>
 * 2019/11/3
 */
@Log4j
@Service
public class ThirdPartyService {

    @Value("${thirdparty.jirong.merchantID}")
    private String jirongMerchantID;
    @Value("${thirdparty.jirong.secretKey}")
    private String jirongSecretKey;
    @Value("${thirdparty.jirong.blacklist.url.v1}")
    private String jirongBlackListUrl;
    @Value("${thirdparty.jirong.blacklist.url.v2}")
    private String jirongBlackListUrlV2;
    @Value("${thirdparty.jirong.token}")
    private String jirongToken;
    @Value("${thirdparty.jirong.countryCode}")
    private String jirongCountryCode;
    @Value("${thirdparty.jirong.region}")
    private String jirongRegion;


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

    public boolean checkInnerBlackList(String cid, String name, String mobile, String apply_id) {
        QueryWrapper<BlackListBean> wrapper = new QueryWrapper<>();
        if (!Strings.isNullOrEmpty(cid)) {
            wrapper.or(w->w.eq("status", BlackListStatusEnum.BL_BLACK.getCode()).eq("cid_no", cid));
        }
        if (!Strings.isNullOrEmpty(name)) {
            wrapper.or(w->w.eq("status", BlackListStatusEnum.BL_BLACK.getCode()).eq("name", name));
        }
        if (!Strings.isNullOrEmpty(mobile)) {
            wrapper.or(w->w.eq("status", BlackListStatusEnum.BL_BLACK.getCode()).eq("mobile", mobile));
        }

        List<BlackListBean> beans = blackListMapper.selectList(wrapper);
        log.info("checkInnerBlackList beans:" + GsonUtil.toJson(beans));
        return beans != null && beans.size() > 0;
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

    public boolean checkBlackListInJirong_v2(String id, String name, String phone, String apply_id) {
        if (Strings.isNullOrEmpty(id)) {
            log.error("ID is null!");
            return false;
        }
        if (hitLocalBlackList(id, name, phone)) {
            return true;
        }

        // String orderNo = id + "_" + apply_id;
        String orderNo = ToolUtils.getToken();
        Map<String, String> params = new HashMap<String, String>();
        params.put("merchantID", jirongMerchantID);
        params.put("token", jirongToken);
        if (!Strings.isNullOrEmpty(name)) {
            params.put("name", name.toUpperCase());
        }
        params.put("identity", id);
        if (!Strings.isNullOrEmpty(phone)) {
            params.put("phone", phone);
        }
        params.put("orderNo", orderNo);
        params.put("region", jirongRegion);
        params.put("version", "1.0.0");
        params.put("sign", getSign_Jirong(jirongSecretKey, params));
        try {
            String response = null;
            JSONObject responseObj = null;
            for (int i = 1; i <= QUERY_RETRY_NUM; ++i) {
                response = HttpClient.httpPost(jirongBlackListUrlV2, GsonUtil.toJson(params));
                responseObj = (JSONObject) JSON.parse(response);
                if (responseObj.getString("code").equals("0")) {
                    break;
                }
                Thread.sleep(1000 * i);
            }
            if (responseObj == null || !responseObj.getString("code").equals("0")) {
                log.error("Failed to query blacklist(jirong)_v2, param: " + GsonUtil.toJson(params)
                        + ", response: " + GsonUtil.toJson(response));
                return false;
            }
            log.info("Query jirong blacklist_v2 response: " + response);
            JSONObject resultObj = (JSONObject)JSON.parse(responseObj.getString("result"));

            String hitResult = resultObj.getString("hitResult");
            if (hitResult == null || !hitResult.toUpperCase().equals("REJECT")) {
                return false;
            }

            String gradeLevel = resultObj.getString("gradeLevel");
            if (gradeLevel != null && gradeLevel.toUpperCase().equals("HIGH")) {
                BlackListBean blackListBean = new BlackListBean();
                if (!Strings.isNullOrEmpty(apply_id)) {
                    blackListBean.setApply_id(Integer.valueOf(apply_id));
                }
                blackListBean.setCid_no(id);
                blackListBean.setName(name.toUpperCase());
                blackListBean.setMobile(phone);
                blackListBean.setPlatform("JIRONG");
                blackListBean.setOrigin_message(response);
                blackListBean.setCreate_time(new Date());
                blackListBean.setStatus(BlackListStatusEnum.BL_BLACK.getCode());
                this.blackListMapper.insert(blackListBean);
                return true;
            }
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

        if (!Strings.isNullOrEmpty(cid)) {
            wrapper.or(w->w.eq("status", BlackListStatusEnum.BL_BLACK.getCode()).eq("cid_no", cid));
        }
        if (!Strings.isNullOrEmpty(name)) {
            wrapper.or(w->w.eq("status", BlackListStatusEnum.BL_BLACK.getCode()).eq("name", name));
        }
        if (!Strings.isNullOrEmpty(mobile)) {
            wrapper.or(w->w.eq("status", BlackListStatusEnum.BL_BLACK.getCode()).eq("mobile", mobile));
        }
//        wrapper.ge("expire_time", new Date());
//        wrapper.eq("status", BlackListStatusEnum.BL_BLACK.getCode());
//        wrapper.and(w -> w.eq("cid_no", cid).or().eq("name", name.toUpperCase()).or().eq("mobile", mobile));

        // log.info("hitLocalBlackList sql: " + wrapper.getSqlSegment());
        //      expire_time >= #{ew.paramNameValuePairs.MPGENVAL1}
        //          AND status = #{ew.paramNameValuePairs.MPGENVAL2}
        //          AND ( cid_no = #{ew.paramNameValuePairs.MPGENVAL1} OR mobile = #{ew.paramNameValuePairs.MPGENVAL2} )
        List<BlackListBean> beans = blackListMapper.selectList(wrapper);
        log.info("hitLocalBlackList beans:" + GsonUtil.toJson(beans));
        return beans != null && beans.size() > 0;
    }

    public static String getSign_Jirong(String secretKey,Map<String, String> map) {
        map.remove("sign");
        //(2) 将keySet放入list,调用sort方法并重写比较器进行升/降序
        ArrayList<String> array2 = new ArrayList<>(map.keySet());
        Collections.sort(array2);
        Iterator<String> iterator = array2.iterator();
        //(3)按照排好的顺序，以连接符号“&”，串联所有的键值对，形成字符串String1。
        StringBuffer string1 = new StringBuffer();
        while ((iterator.hasNext())) {
            String key = iterator.next();
            if (string1.length() > 0) {
                string1.append("&");
            }
            string1.append(key).append("=").append(map.get(key));
        }
        //(4)在String1后面再串入您的secret key，形成String2。
        String string2 = string1.append(secretKey).toString();
        // (5)对String2进行摘要运算（SHA1、SHA256的支持目前仅支持MD5运算，后续会加入），获得到新的字符串String3。
        String string3 =  Md5(string2);
        return string3;
    }

    private static String Md5(String string) {
        if (Strings.isNullOrEmpty(string)) {
            return "";
        }
        MessageDigest md5;
        try {
            md5 = MessageDigest.getInstance("MD5");
            byte[] bytes = md5.digest(string.getBytes());
            StringBuilder sb = new StringBuilder();
            for (byte b : bytes) {
                String temp = Integer.toHexString(b & 0xff);
                if (temp.length() == 1) {
                    temp = "0" + temp;
                }
                sb.append(temp);
            }
            return sb.toString().toUpperCase();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return "";
    }
}
