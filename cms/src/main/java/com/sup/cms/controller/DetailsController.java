package com.sup.cms.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.sup.cms.bean.po.*;
import com.sup.cms.mapper.*;
import com.sup.common.util.ResponseUtil;
import com.sup.cms.util.SSDBClient;
import com.sup.common.bean.*;
import com.sup.common.util.GsonUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 详情页面
 *
 * @Author: kouichi
 * @Date: 2019/10/2 14:53
 */
@RequestMapping("/details")
@RestController
@Slf4j
public class DetailsController {

    @Autowired
    private TbApplyInfoMapper applyInfoMapper;
    @Autowired
    private TbApplyMaterialInfoMapper applyMaterialInfoMapper;
    @Autowired
    private TbUserBasicInfoMapper userBasicInfoMapper;
    @Autowired
    private TbUserCitizenIdentityCardInfoMapper userCitizenIdentityCardInfoMapper;
    @Autowired
    private TbUserEmergencyContactMapper userEmergencyContactMapper;
    @Autowired
    private TbUserEmploymentInfoMapper userEmploymentInfoMapper;
    @Autowired
    private TbUserBankAccountInfoMapper userBankAccountInfoMapper;
    @Autowired
    private CrazyJoinMapper crazyJoinMapper;

    @Autowired
    private TbBlackListBeanMapper blackListBeanMapper;

    @Autowired
    private  TbUserRegisterInfoMapper   userRegisterInfoMapper;


    @Autowired
    private SSDBClient ssdbClient;

    /**
     * 进件信息
     */
    @GetMapping("/applyInfo")
    public String applyInfo(@RequestParam("applyId") Integer applyId) {
        DetailsApplyInfoBean bean = crazyJoinMapper.detailsApplyInfo(applyId);
        return ResponseUtil.success(bean);
    }

    /**
     * 待还信息
     */
    @GetMapping("/toBeRepay")
    public String toBeRepay(@RequestParam("applyId") Integer applyId) {
        DetailsToBeRepayBean bean = crazyJoinMapper.detailsToBeRepay(applyId);
        List<DetailsToBeRepayList> list = crazyJoinMapper.detailsToBeRepayList(applyId);
        bean.setList(list);
        return ResponseUtil.success(bean);
    }

    /**
     * 还款记录
     */
    @GetMapping("/repayRecord")
    public String repayRecord(@RequestParam("applyId") Integer applyId) {
        List<DetailsRepayRecordBean> list = crazyJoinMapper.detailsRepayRecord(applyId);
        return ResponseUtil.success(list);
    }

    /**
     * 用户基本信息
     *
     * @param applyId
     * @return
     */
    @GetMapping("/basicInfo")
    public String basicInfo(@RequestParam("applyId") Integer applyId) {
        QueryWrapper<TbApplyMaterialInfoBean> qw1 = new QueryWrapper<>();
        qw1.eq("apply_id", applyId);
        qw1.eq("info_type", 0);
        QueryWrapper<TbApplyMaterialInfoBean> qw2 = new QueryWrapper<>();
        qw2.eq("apply_id", applyId);
        qw2.eq("info_type", 1);
        QueryWrapper<TbApplyMaterialInfoBean> qw3 = new QueryWrapper<>();
        qw3.eq("apply_id", applyId);
        qw3.eq("info_type", 2);
        QueryWrapper<TbApplyMaterialInfoBean> qw4 = new QueryWrapper<>();
        qw4.eq("apply_id", applyId);
        qw4.eq("info_type", 3);
        TbApplyMaterialInfoBean citizen = applyMaterialInfoMapper.selectOne(qw1);
        TbApplyMaterialInfoBean basic = applyMaterialInfoMapper.selectOne(qw2);
        TbApplyMaterialInfoBean emergency = applyMaterialInfoMapper.selectOne(qw3);
        TbApplyMaterialInfoBean employment = applyMaterialInfoMapper.selectOne(qw4);
        QueryWrapper<TbUserCitizenIdentityCardInfoBean> citizenQw = new QueryWrapper<>();
        citizenQw.eq("info_id", citizen.getInfo_id());
        QueryWrapper<TbUserBasicInfoBean> basicQw = new QueryWrapper<>();
        basicQw.eq("info_id", basic.getInfo_id());
        QueryWrapper<TbUserEmergencyContactBean> emergencyQw = new QueryWrapper<>();
        emergencyQw.eq("info_id", emergency.getInfo_id());
        QueryWrapper<TbUserEmploymentInfoBean> employmentQw = new QueryWrapper<>();
        employmentQw.eq("info_id", employment.getInfo_id());

        TbUserCitizenIdentityCardInfoBean citizenBean = userCitizenIdentityCardInfoMapper.selectOne(citizenQw);
        TbUserBasicInfoBean basicBean = userBasicInfoMapper.selectOne(basicQw);
        List<TbUserEmergencyContactBean> emergencyList = userEmergencyContactMapper.selectList(emergencyQw);
        TbUserEmploymentInfoBean employmentBean = userEmploymentInfoMapper.selectOne(employmentQw);

        if (citizenBean == null || basicBean == null || emergencyList == null || employmentBean == null) {
            log.error("some basic info is NULL. applyId = " + applyId);
            return ResponseUtil.failed();
        }

        DetailsBasicInfoBean bean = new DetailsBasicInfoBean();
        bean.setUserId(citizenBean.getUser_id());
        bean.setName(citizenBean.getName());
        bean.setCidNo(citizenBean.getCid_no());
        bean.setGender(citizenBean.getGender());
        bean.setCity(basicBean.getResidence_city());
        bean.setAddr(basicBean.getResidence_addr());
        bean.setDuration(basicBean.getResidence_duration());
        bean.setEducation(basicBean.getEducation());
        bean.setMarriage(basicBean.getMarriage());
        bean.setPurpose(basicBean.getPurpose());
        bean.setChildrenCount(basicBean.getChildren_count());
        bean.setFacebookLink(basicBean.getFacebook_link());
        bean.setCompany(employmentBean.getCompany());
        bean.setCompanyCity(employmentBean.getCompany_city());
        bean.setCompanyAddr(employmentBean.getCompany_addr());
        bean.setPhone(employmentBean.getPhone());
        bean.setJob(employmentBean.getJob_occupation());
        bean.setIncome(employmentBean.getIncome());
        bean.setWork_period(employmentBean.getWork_period());

        List<DetailsEmergencyContact> list = Lists.newArrayList();
        emergencyList.forEach(x -> {
            DetailsEmergencyContact b = new DetailsEmergencyContact();
            b.setName(x.getName());
            b.setPhone(x.getMobile());
            b.setRelationship(x.getRelationship());
            list.add(b);
        });
        bean.setList(list);
        return ResponseUtil.success(bean);
    }

    /**
     * 资料信息 一大堆图片那个
     */
    @GetMapping("/information")
    public String information(@RequestParam("applyId") Integer applyId) {
        QueryWrapper<TbApplyMaterialInfoBean> qw1 = new QueryWrapper<>();
        qw1.eq("apply_id", applyId);
        qw1.eq("info_type", 0);
        QueryWrapper<TbApplyMaterialInfoBean> qw2 = new QueryWrapper<>();
        qw2.eq("apply_id", applyId);
        qw2.eq("info_type", 3);
        TbApplyMaterialInfoBean citizen = applyMaterialInfoMapper.selectOne(qw1);
        TbApplyMaterialInfoBean employment = applyMaterialInfoMapper.selectOne(qw2);
        QueryWrapper<TbUserCitizenIdentityCardInfoBean> citizenQw = new QueryWrapper<>();
        citizenQw.eq("info_id", citizen.getInfo_id());
        QueryWrapper<TbUserEmploymentInfoBean> employmentQw = new QueryWrapper<>();
        employmentQw.eq("info_id", employment.getInfo_id());

        TbUserCitizenIdentityCardInfoBean citizenBean = userCitizenIdentityCardInfoMapper.selectOne(citizenQw);
        TbUserEmploymentInfoBean employmentBean = userEmploymentInfoMapper.selectOne(employmentQw);

        DetailsInformationBean b = new DetailsInformationBean();
        b.setPic1(citizenBean.getPic_1());
        b.setPic2(citizenBean.getPic_2());
        b.setPic3(citizenBean.getPic_3());
        b.setPic4(employmentBean.getWork_pic());
        return ResponseUtil.success(b);
    }

//    /**
//     * 银行账户信息-受托账户
//     */
//    @GetMapping("/bankAccountBeEntrusted")
//    public String bankAccountBeEntrusted(@RequestParam("applyId") Integer applyId) {
//        QueryWrapper<TbApplyMaterialInfoBean> qw = new QueryWrapper<>();
//        qw.eq("apply_id", applyId);
//        qw.eq("info_type", 4);
//        TbApplyMaterialInfoBean bank = applyMaterialInfoMapper.selectOne(qw);
//        if (bank == null) {
//            log.error("No bank info found for applyId:" + applyId);
//            return ResponseUtil.failed("No bank info found!");
//        }
//        QueryWrapper<TbUserBankAccountInfoBean> bankQw = new QueryWrapper<>();
//        bankQw.eq("info_id", bank.getInfo_id());
//        TbUserBankAccountInfoBean bankBean = userBankAccountInfoMapper.selectOne(bankQw);
//        ApplyBankAccountInfoBean b = new ApplyBankAccountInfoBean();
//        // log.info(bank.toString());
//        log.info("applyId = " + applyId + ", info_id = " + bank.getInfo_id() + ", bankBean == null: " + (bankBean == null));
//        b.setName(bankBean.getName());
//        b.setAccount(bankBean.getAccount_id());
//        b.setBank(bankBean.getBank());
//        b.setAccount_type(bankBean.getAccount_type());
//        return ResponseUtil.success(b);
//    }

    /**
     * 决策引擎结果
     *
     * @param applyId
     * @return
     */
    @GetMapping("/riskDecision")
    public String riskDecision(@RequestParam("applyId") Integer applyId) {
        List<DetailsRiskDecisionBean> beanList = crazyJoinMapper.detailsRiskDecision(applyId);
        return ResponseUtil.success(beanList);
    }

    /**
     * 定价信息
     */
    @GetMapping("/dingjiaxinxi")
    public String dingjiaxinxi(@RequestParam("applyId") Integer applyId) {
        TbApplyInfoBean b = applyInfoMapper.selectById(applyId);
        Map m = Maps.newHashMap();
        m.put("grantQuota", b.getGrant_quota());
        m.put("rate", b.getRate());
        return ResponseUtil.success(m);
    }

    @ResponseBody
    @RequestMapping(value = "/image/get")
    public String getImages(@RequestParam("keys") List<String> keys) {
        if (keys == null || keys.size() == 0) {
            return ResponseUtil.failed("No keys!");
        }
        ArrayList<byte[]> contents = new ArrayList<>();
        for (String key : keys) {
            byte[] val = ssdbClient.GetBytes(key);
            contents.add(val);
        }
        return ResponseUtil.success(contents);
    }


    @GetMapping("/addBlackList")
    public String addBlackList(@RequestParam("applyId") Integer applyId) {
        List<TbApplyMaterialInfoBean> applyMaterialInfoBeans = this.applyMaterialInfoMapper.
                selectList(new QueryWrapper<TbApplyMaterialInfoBean>().eq("apply_id", applyId ));
        String basic_info_id = "", id_info_id = "";

        //0|身份证信息  1|基本信息 2|紧急联系人 3|职业信息 4|银行卡信息
        for (TbApplyMaterialInfoBean materialInfoBean : applyMaterialInfoBeans) {
            int infoType = materialInfoBean.getInfo_type();
            switch (infoType) {
                case 0:
                    id_info_id = materialInfoBean.getInfo_id();
                    break;
                case 1:
                    basic_info_id = materialInfoBean.getInfo_id();
                    break;

            }
        }
        String cid = "";
        String usermobile = "";

        if (!id_info_id.isEmpty()) {
            TbUserCitizenIdentityCardInfoBean userIdCardInfoBean = this.userCitizenIdentityCardInfoMapper.selectOne(new QueryWrapper<TbUserCitizenIdentityCardInfoBean>().eq("info_id", id_info_id));
            if (userIdCardInfoBean != null) {
                cid = userIdCardInfoBean.getCid_no();
            }
        }
        if (!basic_info_id.isEmpty()) {
            TbUserBasicInfoBean basicInfoBean = this.userBasicInfoMapper.selectOne(new QueryWrapper<TbUserBasicInfoBean>().eq("info_id", basic_info_id));
            Integer userId = basicInfoBean.getUser_id();
            TbUserRegistInfoBean userRegistInfoBean = this.userRegisterInfoMapper.selectById(userId);
            if (userRegistInfoBean != null) {
                usermobile = userRegistInfoBean.getMobile();
            }

        }
        if (cid.isEmpty() && usermobile.isEmpty()) {
            return ResponseUtil.failed("no cid or mobile");
        }
        BlackListBean blackListBean = new BlackListBean();
        blackListBean.setStatus(2);   // 0:正常，1:灰名单, 2:黑名单


        if (!cid.isEmpty()) {
            blackListBean.setCid_no(cid);

        }
        if (!usermobile.isEmpty()) {
            blackListBean.setMobile(usermobile);
        }
        blackListBean.setPlatform("inner");

        this.blackListBeanMapper.insert(blackListBean);
        return ResponseUtil.success();


    }


}
