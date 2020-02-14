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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import java.util.Map;


/**
 * @Author: kouichi
 * @Date: 2019/9/11 21:35
 */
@RestController
@RequestMapping("/")
@Slf4j
public class KalapaController {

    @Value("${kalapa.token}")
    private String token;
    private String baseUrl = "http://api.kalapa.vn/user-profile";

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
        String incomeResult = OkBang.get(baseUrl + "/income/get/", params1, header);
        if (Strings.isNullOrEmpty(incomeResult)) {
            return Result.fail(Result.kError, "外部服务异常");
        }
        IncomeInfo incomeInfo = GsonUtil.fromJson(incomeResult, IncomeInfo.class);
        Map<String, String> params2 = Maps.newHashMap();
        params2.put("id", params.getId());
        String familyResult = OkBang.get(baseUrl + "/family/get/", params2, header);
        if (Strings.isNullOrEmpty(familyResult)) {
            return Result.fail(Result.kError, "外部服务异常");
        }
        FamilyInfo familyInfo = GsonUtil.fromJson(familyResult, FamilyInfo.class);
        VSSResult vss = new VSSResult();
        vss.setIncomeInfo(incomeInfo);
        vss.setFamilyInfo(familyInfo);
        return Result.succ(vss);
    }

    @GetMapping(value = "getCICBInfo")
    public Result<CICBResult> getCICBInfo(@RequestBody GetCICBInfoParams params) {
        Map header = ImmutableMap.of("Authorization", token);
        Map<String, String> params1 = Maps.newHashMap();
        params1.put("id", params.getId());
        String scoreResult = OkBang.get(baseUrl + "/scoring/get/", params1, header);
        if (Strings.isNullOrEmpty(scoreResult)) {
            return Result.fail(Result.kError, "外部服务异常");
        }
        CreditScore creditScore = GsonUtil.fromJson(scoreResult, CreditScore.class);
        String creditResult = OkBang.get(baseUrl + "/credit/get/", params1, header);
        if (Strings.isNullOrEmpty(creditResult)) {
            return Result.fail(Result.kError, "外部服务异常");
        }
        CreditStatus creditStatus = GsonUtil.fromJson(creditResult, CreditStatus.class);
        CICBResult cicbResult = new CICBResult();
        cicbResult.setCreditScore(creditScore);
        cicbResult.setCreditStatus(creditStatus);
        return Result.succ(cicbResult);
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
        String socialResult = OkBang.get(baseUrl + "/social/get/", params1, header);
        if (Strings.isNullOrEmpty(socialResult)) {
            return Result.fail(Result.kError, "外部服务异常");
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
