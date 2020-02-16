package com.sup.kalapa.controller;

import com.google.common.base.Strings;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.sup.common.util.RedisClient;
import com.sup.common.util.Result;
import com.sup.kalapa.bean.dto.*;
import com.sup.kalapa.bean.kalapa.*;
import com.sup.kalapa.util.GsonUtil;
import com.sup.kalapa.util.OkBang;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.cache.CacheProperties;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.concurrent.TimeUnit;


/**
 * @Author: kouichi
 * @Date: 2019/9/11 21:35
 */
@RestController
@RequestMapping("/")
@Slf4j
public class KalapaController {

    @Autowired
    private RedisClient redisClient;

    @Value("${kalapa.token}")
    private String token;
    private String baseUrl = "http://api.kalapa.vn/user-profile";
    private static final Long EXPIRE = 90l;

    private String getKey(Integer userId, String type) {

        StringBuilder key = new StringBuilder("user_");
        key.append(userId);
        key.append("_");
        key.append(type);
        return key.toString();


    }


    @GetMapping(value = "getVSSInfo")
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
        String familyResult = "";
        String incomeResult = "";
        if (params.getUserId() != null) {
            String key = getKey(params.getUserId(), "vssinfo_income");
            incomeResult = this.redisClient.Get(key);

            String key2 = getKey(params.getUserId(), "vssinfo_family");
            familyResult = this.redisClient.Get(key2);

            if (Strings.isNullOrEmpty(incomeResult)) {
                incomeResult = OkBang.get(baseUrl + "/income/get/", params1, header);
                if (Strings.isNullOrEmpty(incomeResult)) {
                    return Result.fail(Result.kError, "外部服务异常");
                }
                this.redisClient.Set(key, incomeResult, EXPIRE, TimeUnit.DAYS);
            }
            IncomeInfo incomeInfo = GsonUtil.fromJson(incomeResult, IncomeInfo.class);

            if (Strings.isNullOrEmpty(familyResult)) {
                Map<String, String> params2 = Maps.newHashMap();
                params2.put("id", params.getId());
                familyResult = OkBang.get(baseUrl + "/family/get/", params2, header);
                if (Strings.isNullOrEmpty(familyResult)) {
                    return Result.fail(Result.kError, "外部服务异常");
                }
                this.redisClient.Set(key2, familyResult, EXPIRE, TimeUnit.DAYS);
            }
            FamilyInfo familyInfo = GsonUtil.fromJson(familyResult, FamilyInfo.class);
            VSSResult vss = new VSSResult();
            vss.setIncomeInfo(incomeInfo);
            vss.setFamilyInfo(familyInfo);
            return Result.succ(vss);
        } else {
           return Result.fail(Result.kError, "not input userid");
        }
    }

    @GetMapping(value = "getCICBInfo")
    public Result<CICBResult> getCICBInfo(@RequestBody GetCICBInfoParams params) {
        Map header = ImmutableMap.of("Authorization", token);
        Map<String, String> params1 = Maps.newHashMap();
        params1.put("id", params.getId());

        if (params.getUserId() != null) {
            String scoreResult = "";
            String creditResult = "";
            String key = getKey(params.getUserId(), "cicb_score");
            String key2 = getKey(params.getUserId(), "cicb_credit");
            scoreResult = this.redisClient.Get(key);
            creditResult = this.redisClient.Get(key2);

            if (Strings.isNullOrEmpty(scoreResult)) {
                scoreResult = OkBang.get(baseUrl + "/scoring/get/", params1, header);
                if (Strings.isNullOrEmpty(scoreResult)) {
                    return Result.fail(Result.kError, "外部服务异常");
                }
                this.redisClient.Set(key, scoreResult, EXPIRE, TimeUnit.DAYS);

            }
            CreditScore creditScore = GsonUtil.fromJson(scoreResult, CreditScore.class);
            if (Strings.isNullOrEmpty(creditResult)) {
                creditResult = OkBang.get(baseUrl + "/credit/get/", params1, header);
                if (Strings.isNullOrEmpty(creditResult)) {
                    return Result.fail(Result.kError, "外部服务异常");
                }
                this.redisClient.Set(key2, creditResult, EXPIRE, TimeUnit.DAYS);
            }
            CreditStatus creditStatus = GsonUtil.fromJson(creditResult, CreditStatus.class);
            CICBResult cicbResult = new CICBResult();
            cicbResult.setCreditScore(creditScore);
            cicbResult.setCreditStatus(creditStatus);
            return Result.succ(cicbResult);
        } else {
            return Result.fail(Result.kError, "not input userid");
        }
    }

    @GetMapping(value = "getFBInfo")
    public Result<SocialNetworkInfo> getFBInfo(@RequestBody GetFBInfoParams params) {
        Map header = ImmutableMap.of("Authorization", token);
        Map<String, String> params1 = Maps.newHashMap();
        if (!Strings.isNullOrEmpty(params.getFbid())) {
            params1.put("fbid", params.getFbid());
        } else {
            params1.put("mobile", params.getMobile());
        }
        if (params.getUserId() != null) {
            String key = this.getKey(params.getUserId(), "fb");
            String socialResult = "";
            socialResult = this.redisClient.Get(key);
            if (Strings.isNullOrEmpty(socialResult)) {
                socialResult = OkBang.get(baseUrl + "/social/get/", params1, header);
                if (Strings.isNullOrEmpty(socialResult)) {
                    return Result.fail(Result.kError, "外部服务异常");
                }
                this.redisClient.Set(key, socialResult, EXPIRE, TimeUnit.DAYS);
            }
            SocialNetworkInfo socialNetworkInfo = GsonUtil.fromJson(socialResult, SocialNetworkInfo.class);
            return Result.succ(socialNetworkInfo);
        } else {
            return Result.fail(Result.kError, "not input userid");
        }
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
