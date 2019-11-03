package com.sup.core.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.sup.core.bean.JirongBLResponse;
import com.sup.core.mapper.JirongBLResponseMapper;
import com.sup.core.util.HttpClient;
import lombok.extern.log4j.Log4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Value;

import java.util.HashMap;
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
    private String secretKey;
    @Value("${thirdparty.jirong.url}")
    private String url;
    @Value("${thirdparty.jirong.token}")
    private String token;

    @Autowired
    private JirongBLResponseMapper jirongBLResponseMapper;


    public boolean callJirong(String id, String name, String phone, String apply_id) {

        Map<String, String> params = new HashMap<String, String>();
        params.put("token", token);
        params.put("secretKey", secretKey);
        params.put("idNumber", id);
        params.put("name", name);
        params.put("countryCode", "84");
        params.put("number", phone);
        params.put("areaCode", "");

        try {
            String response = null;
            response = HttpClient.httpPostWithParams(url, params);
            JirongBLResponse jirongBLResponse = new JirongBLResponse();
            JSONObject jsonObject = (JSONObject) JSON.parse(response);
            jirongBLResponse.setApply_id(apply_id);
            jirongBLResponse.setStatus(jsonObject.getString("status"));
            jirongBLResponse.setTransactionId(jsonObject.getString("transactionId"));
            jirongBLResponse.setHitResult(jsonObject.getString("hitResult"));
            jirongBLResponse.setPricingStrategy(jsonObject.getString("pricingStrategy"));
            jirongBLResponse.setMessage(jsonObject.getString("message"));
            this.jirongBLResponseMapper.insert(jirongBLResponse);
            String ret = jsonObject.getString("hitResult");
            return ret.equals("REJECT");
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return false;
    }
}
