package com.sup.kalapa.controller;

import com.google.common.base.Strings;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.sup.common.util.Result;
import com.sup.kalapa.bean.dto.*;
import com.sup.kalapa.bean.kalapa.*;
import com.sup.kalapa.util.GsonUtil;
import com.sup.kalapa.util.OkBang;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.concurrent.TimeUnit;


/**
 * @Author: kouichi
 * @Date: 2019/9/11 21:35
 */
@RestController
@RequestMapping("/kalapa")
@Slf4j
public class KalapaController {

    @Autowired
    private StringRedisTemplate redis;

    @Value("${kalapa.token}")
    private String token;
    private String baseUrl = "https://api.kalapa.vn/user-profile";
    private static final Long EXPIRE = 90L;

    private String getKey(Integer userId, String type) {
        StringBuilder key = new StringBuilder("user_");
        key.append(userId);
        key.append("_");
        key.append(type);
        return key.toString();
    }

    private String get(Integer userId, String type) {
        return redis.opsForValue().get(getKey(userId, type));
    }

    private void put(Integer userId, String type, String msg) {
        redis.opsForValue().set(getKey(userId, type), msg, EXPIRE, TimeUnit.DAYS);
    }

    @PostMapping(value = "getVSSInfo")
    public Result<VSSResult> getVSSInfo(@RequestBody GetVSSInfoParams params) {
        Map header = ImmutableMap.of("Authorization", token);
        Map<String, String> params1 = Maps.newHashMap();
        if (params.getGetMaxLatestJobs() != null) {
            params1.put("get_max_latest_jobs", params.getGetMaxLatestJobs().toString());
        }
        if (params.getGetHRInfo() != null) {
            params1.put("hr_info", params.getGetHRInfo().toString());
        }
        params1.put("id", params.getId());
        String incomeResult = get(params.getUserId(), "vssinfo_income");
        if (Strings.isNullOrEmpty(incomeResult)) {
            incomeResult = OkBang.get(baseUrl + "/income/get/", params1, header);
            if (Strings.isNullOrEmpty(incomeResult) || incomeResult.contains("error")) {
                return Result.fail(Result.kError, "外部服务异常");
            }
            put(params.getUserId(), "vssinfo_income", incomeResult);
        }
        String familyResult = get(params.getUserId(), "vssinfo_family");
        if (Strings.isNullOrEmpty(familyResult)) {
            Map<String, String> params2 = ImmutableMap.of("id", params.getId());
            familyResult = OkBang.get(baseUrl + "/family/get/", params2, header);
            if (Strings.isNullOrEmpty(familyResult) || familyResult.contains("error")) {
                return Result.fail(Result.kError, "外部服务异常");
            }
            put(params.getUserId(), "vssinfo_family", familyResult);
        }
        IncomeInfo incomeInfo = GsonUtil.fromJson(incomeResult, IncomeInfo.class);
        FamilyInfo familyInfo = GsonUtil.fromJson(familyResult, FamilyInfo.class);
        VSSResult vss = new VSSResult();
        vss.setIncomeInfo(incomeInfo);
        vss.setFamilyInfo(familyInfo);
        return Result.succ(vss);
    }

    @PostMapping(value = "getCICBInfo")
    public Result<CICBResult> getCICBInfo(@RequestBody GetCICBInfoParams params) {
        Map header = ImmutableMap.of("Authorization", token);
        Map<String, String> params1 = ImmutableMap.of("id", params.getId());
        String scoreResult = get(params.getUserId(), "cicb_score");
        if (Strings.isNullOrEmpty(scoreResult)) {
            scoreResult = OkBang.get(baseUrl + "/scoring/get/", params1, header);
            if (Strings.isNullOrEmpty(scoreResult) || scoreResult.contains("error")) {
                return Result.fail(Result.kError, "外部服务异常");
            }
            put(params.getUserId(), "cicb_score", scoreResult);
        }
        String creditResult = get(params.getUserId(), "cicb_credit");
        if (Strings.isNullOrEmpty(creditResult)) {
            creditResult = OkBang.get(baseUrl + "/credit/get/", params1, header);
            if (Strings.isNullOrEmpty(creditResult) || creditResult.contains("error")) {
                return Result.fail(Result.kError, "外部服务异常");
            }
            put(params.getUserId(), "cicb_credit", creditResult);
        }
        CreditScore creditScore = GsonUtil.fromJson(scoreResult, CreditScore.class);
        CreditStatus creditStatus = GsonUtil.fromJson(creditResult, CreditStatus.class);
        CICBResult cicbResult = new CICBResult();
        cicbResult.setCreditScore(creditScore);
        cicbResult.setCreditStatus(creditStatus);
        return Result.succ(cicbResult);
    }

    @PostMapping(value = "getFBInfo")
    public Result<SocialNetworkInfo> getFBInfo(@RequestBody GetFBInfoParams params) {
        Map header = ImmutableMap.of("Authorization", token);
        Map<String, String> params1 = Maps.newHashMap();
        if (!Strings.isNullOrEmpty(params.getFbid())) {
            params1.put("fbid", params.getFbid());
        } else {
            params1.put("mobile", params.getMobile());
        }
        String socialResult = get(params.getUserId(), "fb");
        if (Strings.isNullOrEmpty(socialResult)) {
            socialResult = OkBang.get(baseUrl + "/social/get/", params1, header);
            if (Strings.isNullOrEmpty(socialResult) || socialResult.contains("error")) {
                return Result.fail(Result.kError, "外部服务异常");
            }
            put(params.getUserId(), "fb", socialResult);
        }
        SocialNetworkInfo socialNetworkInfo = GsonUtil.fromJson(socialResult, SocialNetworkInfo.class);
        return Result.succ(socialNetworkInfo);
    }

    /**
     * 社会局
     *
     * √    /income/get/ Get income status
     *
     *      /career/get/ Get career status   目测没用  和income有重复
     *
     * √    /family/get/ Get family info
     *
     *
     * cicb
     * √    /scoring/get/    personal credit score
     *
     * √    /credit/get/ Get credit status
     *
     *      /credit/r11/get/ Get full credit info (current debt, ). Limited by token. Please contact with Kalapa team for unlocking this api
     *
     *
     * fb
     * √    /social/get/    这是取所有信息 get social info from mobile number or facebook id
     *
     *      /friend/get/ 这是取给定人朋友的信息 get facebook friend
     *
     *      /facebook/get/   这是取给定人的信息 get facebook link from mobile number or facebook id
     *
     *
     *
     * tax
     *      /personal_tax/get/ Get personal tax infos
     *
     *      /enterprise_tax/get/ Get enterprise tax infos
     *
     *
     * insurance
     *      /medical/get/ Get medical insurance status
     *
     */

}
