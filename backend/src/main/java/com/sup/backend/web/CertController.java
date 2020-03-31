package com.sup.backend.web;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.google.common.collect.ImmutableMap;
import com.sup.backend.bean.*;
import com.sup.backend.core.LoginInfo;
import com.sup.backend.core.LoginRequired;
import com.sup.backend.mapper.*;
import com.sup.backend.service.RedisClient;
import com.sup.backend.util.LangUtil;
import com.sup.backend.util.ToolUtils;
import com.sup.common.bean.*;
import com.sup.common.loan.*;
import lombok.extern.java.Log;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static com.sup.common.loan.ApplyMaterialTypeEnum.*;
import static com.sup.common.loan.DocumentaryImageObjectEnum.*;

/**
 * Created by xidongzhou1 on 2019/9/5.
 */

@RestController
@RequestMapping(value = "/cert")
public class CertController {
  public static Logger logger = Logger.getLogger(CertController.class);
  @Autowired
  RedisClient rc;
  @Autowired
  TbUserRegistInfoMapper tb_user_regist_info_mapper;
  @Autowired
  TbUserCitizenIdentityCardInfoMapper tb_user_citizen_identity_card_info_mapper;
  @Autowired
  TbUserBasicInfoMapper tb_user_basic_info_mapper;
  @Autowired
  TbUserEmploymentInfoMapper tb_user_employment_info_mapper;
  @Autowired
  TbUserEmergencyContactMapper tb_user_emergency_contact_mapper;
  @Autowired
  TbUserBankAccountInfoMapper tb_user_bank_account_mapper;
  @Autowired
  TbApplyMaterialInfoMapper tb_apply_info_material_mapper;
  @Autowired
  TbUserDocumentaryImageMapper tb_user_documentary_image_mapper;
  @Autowired
  TbApplyInfoMapper tb_apply_info_mapper;
  @Autowired
  private LangUtil trans;

  private boolean CheckDuplicateSubmit(String uri, Integer user_id) {
    String dup_key = String.format("submit|%d_%s", user_id, uri);
    return rc.SetEx(dup_key, "ok", 5l, TimeUnit.SECONDS);
  }

  private void ClearDuplicateSubmit(String uri, Integer user_id) {
    String dup_key = String.format("submit|%d_%s", user_id, uri);
    rc.Delete(dup_key);
  }

  //////////////////////////////
  // 申请资料CRUD接口
  //////////////////////////////
    /*
  1. check is used to apply,
    1.1   no, return the one !
    2.2   check expire_time and now
      2.2.1   expire > now, copy return the copy_one !!
      2.2.2   expire < now, return null !!
   */
  private TbUserDocumentaryImageBean GetImageRepository(String info_id, Integer user_id, Integer object) {
    QueryWrapper query = new QueryWrapper<TbUserDocumentaryImageBean>()
        .eq("user_id", user_id)
        .eq("info_id", info_id)
        .eq("image_object", object)
        .last("limit 1");
    return tb_user_documentary_image_mapper.selectOne(query);
  }

  private Integer UpdateImageRepository(Integer user_id, String info_id, String image_key, Integer category, Integer object, Integer upload_type, String rel_id) {
    if (StringUtils.isEmpty(image_key)) {
      return null;
    }

    TbUserDocumentaryImageBean user_img = new TbUserDocumentaryImageBean();
    user_img.setCreate_time(new Date());
    user_img.setImage_category(category);
    user_img.setImage_object(object);
    user_img.setImage_key(image_key);
    user_img.setInfo_id(info_id);
    user_img.setUpload_type(upload_type);
    user_img.setUpload_user(user_id);
    user_img.setImage_rel_id(rel_id);
    user_img.setUser_id(user_id);

    QueryWrapper query = new QueryWrapper<TbUserDocumentaryImageBean>()
        .eq("user_id", user_img.getUser_id())
        .eq("info_id", user_img.getInfo_id())
        .eq("image_object", user_img.getImage_object())
        .last("limit 1");
    TbUserDocumentaryImageBean old_image_bean = tb_user_documentary_image_mapper.selectOne(query);
    if (old_image_bean == null) {
      tb_user_documentary_image_mapper.insert(user_img);
    } else {
      user_img.setId(old_image_bean.getId());
      tb_user_documentary_image_mapper.updateById(user_img);
    }
    return user_img.getId();
  }

  @LoginRequired
  @ResponseBody
  @RequestMapping(value = "cert_status", produces = "application/json;charset=UTF-8")
  public Object GetCertStatus(@LoginInfo LoginInfoCtx li) {
    AppUserCertStatus ret = new AppUserCertStatus();
    Integer user_id = li.getUser_id();
    QueryWrapper<TbApplyInfoBean> wrapper = new QueryWrapper<>();
    wrapper.eq("user_id", user_id);
    wrapper.in("status", ApplyStatusEnum.APPLY_INIT.getCode()
        , ApplyStatusEnum.APPLY_AUTO_PASS.getCode()
        , ApplyStatusEnum.APPLY_FIRST_PASS.getCode()
        , ApplyStatusEnum.APPLY_SECOND_PASS.getCode()
        , ApplyStatusEnum.APPLY_FINAL_PASS.getCode()
        , ApplyStatusEnum.APPLY_AUTO_LOANING.getCode()
        , ApplyStatusEnum.APPLY_AUTO_LOAN_FAILED.getCode()
        , ApplyStatusEnum.APPLY_LOAN_SUCC.getCode()
        , ApplyStatusEnum.APPLY_REPAY_PART.getCode()
        , ApplyStatusEnum.APPLY_OVERDUE.getCode()
    );
    wrapper.orderByDesc("create_time").last("limit 1");
    String lang = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest().getHeader("lang");
    TbApplyInfoBean apply = tb_apply_info_mapper.selectOne(wrapper);
    if (apply != null) {
      ret.setForbidden_new_apply(true);
      ret.setCurrent_apply_status(trans.GetTrans("apply_status", apply.getStatus().toString(), lang));
      ret.setCert_info(new HashMap<>());
      return ToolUtils.succ(ret);
    }

    ret.setCurrent_apply_status("");
    ret.setForbidden_new_apply(false);
    ret.setCert_info(new HashMap<>());

    // fetch idc info;
    {
      Wrapper<TbUserCitizenIdentityCardInfoBean> cond = new QueryWrapper<TbUserCitizenIdentityCardInfoBean>().eq("user_id", li.getUser_id()).orderByDesc("create_time").last("limit 1");
      TbUserCitizenIdentityCardInfoBean old_bean = tb_user_citizen_identity_card_info_mapper.selectOne(cond);
      if (old_bean != null && old_bean.getExpire_time().getTime() > System.currentTimeMillis()) {
        TbApplyMaterialInfoBean apply_material = tb_apply_info_material_mapper.selectOne(new QueryWrapper<TbApplyMaterialInfoBean>().eq("info_type", APPLY_MATERIAL_IDC.getCode()).eq("info_id", old_bean.getInfo_id()).last("limit 1"));
        if (apply_material != null) {
          old_bean.setInfo_id(ToolUtils.getToken());
          old_bean.setId(null);
          old_bean.setCreate_time(new Date());
          old_bean.setExpire_time(Date.from(LocalDateTime.now().plusYears(1l).atZone(ZoneId.systemDefault()).toInstant()));
          tb_user_citizen_identity_card_info_mapper.insert(old_bean);
        }
        ret.getCert_info().put(APPLY_MATERIAL_IDC.getCode(), old_bean.getInfo_id());
      }
    }

    // fetch basic
    {
      Wrapper<TbUserBasicInfoBean> cond = new QueryWrapper<TbUserBasicInfoBean>().eq("user_id", li.getUser_id()).orderByDesc("create_time").last("limit 1");
      TbUserBasicInfoBean old_bean = tb_user_basic_info_mapper.selectOne(cond);
      if (old_bean != null && old_bean.getExpire_time().getTime() > System.currentTimeMillis()) {
        TbApplyMaterialInfoBean apply_material = tb_apply_info_material_mapper.selectOne(new QueryWrapper<TbApplyMaterialInfoBean>().eq("info_type", APPLY_MATERIAL_BASIC.getCode()).eq("info_id", old_bean.getInfo_id()).last("limit 1"));
        if (apply_material != null) {
          old_bean.setId(null);
          old_bean.setInfo_id(ToolUtils.getToken());
          old_bean.setCreate_time(new Date());
          old_bean.setExpire_time(Date.from(LocalDateTime.now().plusYears(1l).atZone(ZoneId.systemDefault()).toInstant()));
          tb_user_basic_info_mapper.insert(old_bean);
        }
        ret.getCert_info().put(APPLY_MATERIAL_BASIC.getCode(), old_bean.getInfo_id());
      }
    }

    // fetch contact !
    {
      Wrapper<TbUserEmergencyContactBean> cond = new QueryWrapper<TbUserEmergencyContactBean>().eq("user_id", li.getUser_id()).orderByDesc("create_time").last("limit 1");
      TbUserEmergencyContactBean old_bean = tb_user_emergency_contact_mapper.selectOne(cond);
      if (old_bean != null && old_bean.getExpire_time().getTime() > System.currentTimeMillis()) {
        List<TbUserEmergencyContactBean> cands = tb_user_emergency_contact_mapper.selectList(new QueryWrapper<TbUserEmergencyContactBean>().eq("info_id", old_bean.getInfo_id()));
        TbApplyMaterialInfoBean apply_material = tb_apply_info_material_mapper.selectOne(new QueryWrapper<TbApplyMaterialInfoBean>().eq("info_type", APPLY_MATERIAL_CONTACT.getCode()).eq("info_id", old_bean.getInfo_id()).last("limit 1"));
        if (apply_material != null) {
          final String new_info_id = ToolUtils.getToken();
          cands.stream().forEach(v -> {
            v.setId(null);
            v.setInfo_id(new_info_id);
            v.setCreate_time(new Date());
            v.setExpire_time(Date.from(LocalDateTime.now().plusYears(1l).atZone(ZoneId.systemDefault()).toInstant()));
            tb_user_emergency_contact_mapper.insert(v);
          });
        }
        ret.getCert_info().put(APPLY_MATERIAL_CONTACT.getCode(), cands.get(0).getInfo_id());
      }
    }

    // fetch employment
    {
      //APPLY_MATERIAL_EMPLOYMENT
      Wrapper<TbUserEmploymentInfoBean> cond = new QueryWrapper<TbUserEmploymentInfoBean>().eq("user_id", li.getUser_id()).orderByDesc("create_time").last("limit 1");
      TbUserEmploymentInfoBean old_bean = tb_user_employment_info_mapper.selectOne(cond);
      if (old_bean != null && old_bean.getExpire_time().getTime() > System.currentTimeMillis()) {
        TbApplyMaterialInfoBean apply_material = tb_apply_info_material_mapper.selectOne(new QueryWrapper<TbApplyMaterialInfoBean>().eq("info_type", APPLY_MATERIAL_EMPLOYMENT.getCode()).eq("info_id", old_bean.getInfo_id()).last("limit 1"));
        if (apply_material != null) {
          old_bean.setId(null);
          old_bean.setInfo_id(ToolUtils.getToken());
          old_bean.setCreate_time(new Date());
          old_bean.setExpire_time(Date.from(LocalDateTime.now().plusYears(1l).atZone(ZoneId.systemDefault()).toInstant()));
          tb_user_employment_info_mapper.insert(old_bean);
        }
        ret.getCert_info().put(APPLY_MATERIAL_EMPLOYMENT.getCode(), old_bean.getInfo_id());
      }
    }

    // fetch APPLY_MATERIAL_BANK
    {
      Wrapper<TbUserBankAccountInfoBean> cond = new QueryWrapper<TbUserBankAccountInfoBean>().eq("user_id", li.getUser_id()).orderByDesc("create_time").last("limit 1");
      TbUserBankAccountInfoBean old_bean = tb_user_bank_account_mapper.selectOne(cond);
      if (old_bean != null && old_bean.getExpire_time().getTime() > System.currentTimeMillis()) {
        TbApplyMaterialInfoBean apply_material = tb_apply_info_material_mapper.selectOne(new QueryWrapper<TbApplyMaterialInfoBean>().eq("info_type", APPLY_MATERIAL_BANK.getCode()).eq("info_id", old_bean.getInfo_id()).last("limit 1"));
        if (apply_material != null) {
          old_bean.setId(null);
          old_bean.setInfo_id(ToolUtils.getToken());
          old_bean.setCreate_time(new Date());
          old_bean.setExpire_time(Date.from(LocalDateTime.now().plusYears(1l).atZone(ZoneId.systemDefault()).toInstant()));
          tb_user_bank_account_mapper.insert(old_bean);
        }
        ret.getCert_info().put(APPLY_MATERIAL_BANK.getCode(), old_bean.getInfo_id());
      }
    }
    return ToolUtils.succ(ret);
  }

  @LoginRequired
  @ResponseBody
  @RequestMapping(value = {"voucher/add", "voucher/update"},  produces = "application/json;charset=UTF-8")
  public Object UpdateVaucher(@LoginInfo LoginInfoCtx li, @RequestBody List<AppVoucherImage> images) {
    logger.info("------ recv bean : " + JSON.toJSON(images));
    if (images == null || images.isEmpty()) {
      return ToolUtils.fail("empty parameter");
    }

    // check parameters !!
    long ok_cnt = images.stream().filter(x -> StringUtils.isNotEmpty(x.getImage_key())).filter(x -> DocumentaryImageObjectEnum.getStatusByCode(x.getImage_object()) != null).count();
    long org_cnt = images.size();
    if (ok_cnt != org_cnt) {
      return ToolUtils.fail("parameter error");
    }

    List<Map> result = new ArrayList<>();
    Map<Integer, String> apply_to_info = new HashMap<>();
    for (AppVoucherImage x : images) {
      if (x.getId() != null) {
        UpdateWrapper<TbUserDocumentaryImageBean> upd_wrapper = new UpdateWrapper<>();
        upd_wrapper.eq("id", x.getId());
        upd_wrapper.set("image_key", x.getImage_key());
        tb_user_documentary_image_mapper.update(null, upd_wrapper);
        result.add(ImmutableMap.of("id", x.getId(), "info_id", tb_user_documentary_image_mapper.selectById(x.getId()).getInfo_id()));
      } else {
        // case1: add to normal  case2: add to apply
        String info_id = "";
        if (x.getApply_id() != null) {
          // case1: add to apply !!
          if (!apply_to_info.containsKey(x.getApply_id())) {
            QueryWrapper<TbApplyMaterialInfoBean> mat_query = new QueryWrapper<TbApplyMaterialInfoBean>().eq("apply_id", x.getApply_id()).eq("info_type", ApplyMaterialTypeEnum.APPLY_MATERIAL_DOCUMENTARY_IMAGE).last("limit 1");
            apply_to_info.put(x.getApply_id(), tb_apply_info_material_mapper.selectOne(mat_query).getInfo_id());
          }
          info_id = apply_to_info.get(x.getApply_id());
        }
        // case2: modify !!
        if (StringUtils.isNotEmpty(x.getInfo_id())) {
          info_id = x.getInfo_id();
        }

        Integer id = UpdateImageRepository(li.getUser_id(),
          info_id,
          x.getImage_key(),
          DocumentaryImageCategoryEnum.EXTEND.getCode(),
          x.getImage_object(),
          x.getApply_id() != null ? DocumentaryImageUploadTypeEnum.UPLOAD_BY_USER_AFTER_APPLY.getCode() : DocumentaryImageUploadTypeEnum.UPLOAD_BY_USER_NORMAL.getCode(),
          x.getApply_id() != null ? x.getApply_id().toString() : "");
        result.add(ImmutableMap.of("id", id, "info_id", tb_user_documentary_image_mapper.selectById(id).getInfo_id()));
      }
    }
    logger.info("------ return bean : " + JSON.toJSON(result));
    return result;
  }

  @LoginRequired
  @ResponseBody
  @RequestMapping(value = "voucher/get", produces = "application/json;charset=UTF-8")
  public Object GetVaucher(@LoginInfo LoginInfoCtx li,Integer apply_id) {
    DocumentaryImageObjectEnum[] targets = new DocumentaryImageObjectEnum[]{
      //IDC_HEAD,
      //IDC_TAIL,
      //OWNER_WITH_IDC,
      //WORK_ENV,
      //REPAY_RECORED,
      //OTHERS,
      DRIVER_LICENSE,
      SOCIAL_CARD
    };
    List<AppVoucherImage> result = new ArrayList<>();
    if (apply_id != null) {
      QueryWrapper<TbApplyMaterialInfoBean> mat_query = new QueryWrapper<TbApplyMaterialInfoBean>().eq("apply_id", apply_id).eq("info_type", ApplyMaterialTypeEnum.APPLY_MATERIAL_DOCUMENTARY_IMAGE).last("limit 1");
      String info_id = tb_apply_info_material_mapper.selectOne(mat_query).getInfo_id();
      for (DocumentaryImageObjectEnum image_object : targets) {
        TbUserDocumentaryImageBean v = GetImageRepository(info_id, li.getUser_id(), image_object.getCode());
        if (v != null) {
          result.add(new AppVoucherImage().setApply_id(apply_id).setId(v.getId()).setImage_key(v.getImage_key()).setImage_object(v.getImage_object()).setInfo_id(v.getInfo_id()));
        }
      }
    } else {
      for (DocumentaryImageObjectEnum image_object : targets) {
        TbUserDocumentaryImageBean cond = new TbUserDocumentaryImageBean();
        cond.setUser_id(li.getUser_id());
        cond.setImage_object(image_object.getCode());
        cond.setInfo_id("");
        TbUserDocumentaryImageBean exist_bean = tb_user_documentary_image_mapper.selectOne(new QueryWrapper<>(cond).lambda().orderByDesc(TbUserDocumentaryImageBean::getCreate_time).last("limit 1"));
        if (exist_bean != null) {
          result.add(new AppVoucherImage().setId(exist_bean.getId()).setInfo_id(exist_bean.getInfo_id()).setImage_key(exist_bean.getImage_key()).setImage_object(exist_bean.getImage_object()));
        } else {
          cond.setInfo_id(null);
          exist_bean = tb_user_documentary_image_mapper.selectOne(new QueryWrapper<>(cond).lambda().orderByDesc(TbUserDocumentaryImageBean::getCreate_time).last("limit 1"));
          if (exist_bean != null && exist_bean.getExpire_time().getTime() > System.currentTimeMillis()) {
            result.add(new AppVoucherImage().setImage_key(exist_bean.getImage_key()).setImage_object(exist_bean.getImage_object()));
          }
        }
      }
    }
    return ToolUtils.succ(result);
  }

  // add/update/get user IDCard info
  @LoginRequired
  @ResponseBody
  @RequestMapping(value = {"idcard/add", "idcard/update"}, produces = "application/json;charset=UTF-8")
  public Object UpdateCIC(@LoginInfo LoginInfoCtx li, @RequestBody @Valid AppTbUserCitizenIdentityCardInfoBean app_bean) {
    logger.info("----- recv bean: " + JSON.toJSONString(app_bean));
    if (!CheckDuplicateSubmit("idcard", li.getUser_id())) {
      return ToolUtils.fail(1, "duplicate_submit");
    }

    TbUserCitizenIdentityCardInfoBean bean = new TbUserCitizenIdentityCardInfoBean();
    BeanUtils.copyProperties(app_bean, bean);

    if (bean.getInfo_id() == null) {
      bean.setInfo_id(ToolUtils.getToken());
      bean.setUser_id(li.getUser_id());
      bean.setCreate_time(new Date());
    }

    // save to documentary !!
    UpdateImageRepository(li.getUser_id(),
      "",
        bean.getPic_1(),
        DocumentaryImageCategoryEnum.NORMAL.getCode(),
        IDC_HEAD.getCode(),
        DocumentaryImageUploadTypeEnum.UPLOAD_BY_USER_NORMAL.getCode(),
        bean.getInfo_id());

    UpdateImageRepository(li.getUser_id(),
      "",
        bean.getPic_2(),
        DocumentaryImageCategoryEnum.NORMAL.getCode(),
        IDC_TAIL.getCode(),
        DocumentaryImageUploadTypeEnum.UPLOAD_BY_USER_NORMAL.getCode(),
        bean.getInfo_id());

    UpdateImageRepository(li.getUser_id(),
      "",
        bean.getPic_3(),
        DocumentaryImageCategoryEnum.NORMAL.getCode(),
        OWNER_WITH_IDC.getCode(),
        DocumentaryImageUploadTypeEnum.UPLOAD_BY_USER_NORMAL.getCode(),
        bean.getInfo_id());

    if (bean.getId() == null) {
      tb_user_citizen_identity_card_info_mapper.insert(bean);
    } else {
      tb_user_citizen_identity_card_info_mapper.updateById(bean);
    }

    TbUserRegistInfoBean regist_info = tb_user_regist_info_mapper.selectById(li.getUser_id());
    if (StringUtils.isEmpty(regist_info.getName()) && (!StringUtils.isEmpty(bean.getName()))) {
      UpdateWrapper<TbUserRegistInfoBean> upd_wrapper = new UpdateWrapper<>();
      upd_wrapper.eq("id", regist_info.getId());
      upd_wrapper.set("name", bean.getName());
      tb_user_regist_info_mapper.update(null, upd_wrapper);
      logger.info(String.format("update user_id: %d, name: (%s) => (%s)", li.getUser_id(), regist_info.getName(), bean.getName()));
    }
    ClearDuplicateSubmit("idcard", li.getUser_id());
    return ToolUtils.succ(ImmutableMap.of("id", bean.getId(), "info_id", bean.getInfo_id()));
  }

  @LoginRequired
  @ResponseBody
  @RequestMapping(value = "idcard/get", produces = "application/json;charset=UTF-8")
  public Object getUserIdCardInfo(@LoginInfo LoginInfoCtx li) {
    BaseMapper<TbUserCitizenIdentityCardInfoBean> mapper = tb_user_citizen_identity_card_info_mapper;
    Wrapper<TbUserCitizenIdentityCardInfoBean> cond = new QueryWrapper<TbUserCitizenIdentityCardInfoBean>().eq("user_id", li.getUser_id()).orderByDesc("create_time").last("limit 1");
    TbUserCitizenIdentityCardInfoBean old_bean = mapper.selectOne(cond);
    if (old_bean == null || old_bean.getExpire_time().getTime() < System.currentTimeMillis()) {
      return ToolUtils.succ(null);
    }
    TbApplyMaterialInfoBean apply_material = tb_apply_info_material_mapper.selectOne(new QueryWrapper<TbApplyMaterialInfoBean>().eq("info_id", old_bean.getInfo_id()).last("limit 1"));
    if (apply_material != null) {
      String new_info_id = ToolUtils.getToken();
      old_bean.setId(null);
      old_bean.setInfo_id(new_info_id);
      old_bean.setCreate_time(new Date());
      old_bean.setExpire_time(Date.from(LocalDateTime.now().plusYears(1l).atZone(ZoneId.systemDefault()).toInstant()));
      mapper.insert(old_bean);
    }
    return ToolUtils.succ(old_bean);
  }

  // add/update/get user basic info
  @LoginRequired
  @ResponseBody
  @RequestMapping(value = {"basic/add", "basic/update"}, produces = "application/json;charset=UTF-8")
  public Object updateBasicInfo(@LoginInfo LoginInfoCtx li, @RequestBody @Valid AppTbUserBasicInfoBean app_bean) {
    logger.info("----- recv bean: " + JSON.toJSONString(app_bean));
    if (!CheckDuplicateSubmit("basic", li.getUser_id())) {
      return ToolUtils.fail(1, "duplicate_submit");
    }
    TbUserBasicInfoBean bean = new TbUserBasicInfoBean();
    BeanUtils.copyProperties(app_bean, bean);

    if (bean.getInfo_id() == null) {
      bean.setInfo_id(ToolUtils.getToken());
      bean.setUser_id(li.getUser_id());
      bean.setCreate_time(new Date());
    }

    // save to documentary !!
    UpdateImageRepository(li.getUser_id(),
        "",
        bean.getMedical_insurance_pic(),
        DocumentaryImageCategoryEnum.NORMAL.getCode(),
        SOCIAL_CARD.getCode(),
        DocumentaryImageUploadTypeEnum.UPLOAD_BY_USER_NORMAL.getCode(),
        bean.getInfo_id());

    UpdateImageRepository(li.getUser_id(),
        "",
        bean.getRefund_proof_pic(),
        DocumentaryImageCategoryEnum.NORMAL.getCode(),
        REPAY_RECORED.getCode(),
        DocumentaryImageUploadTypeEnum.UPLOAD_BY_USER_NORMAL.getCode(),
        bean.getInfo_id());

    UpdateImageRepository(li.getUser_id(),
        "",
        bean.getDriving_license_pic(),
        DocumentaryImageCategoryEnum.NORMAL.getCode(),
        DRIVER_LICENSE.getCode(),
        DocumentaryImageUploadTypeEnum.UPLOAD_BY_USER_NORMAL.getCode(),
        bean.getInfo_id());

    if (bean.getId() == null) {
      tb_user_basic_info_mapper.insert(bean);
    } else {
      tb_user_basic_info_mapper.updateById(bean);
    }
    ClearDuplicateSubmit("basic", li.getUser_id());
    return ToolUtils.succ(ImmutableMap.of("id", bean.getId(), "info_id", bean.getInfo_id()));
  }

  @LoginRequired
  @ResponseBody
  @RequestMapping(value = "basic/get", produces = "application/json;charset=UTF-8")
  public Object getUserBasicInfo(@LoginInfo LoginInfoCtx li) {
    BaseMapper<TbUserBasicInfoBean> mapper = tb_user_basic_info_mapper;
    Wrapper<TbUserBasicInfoBean> cond = new QueryWrapper<TbUserBasicInfoBean>().eq("user_id", li.getUser_id()).orderByDesc("create_time").last("limit 1");
    TbUserBasicInfoBean old_bean = mapper.selectOne(cond);
    if (old_bean == null || old_bean.getExpire_time().getTime() < System.currentTimeMillis()) {
      return ToolUtils.succ(null);
    }
    TbApplyMaterialInfoBean apply_material = tb_apply_info_material_mapper.selectOne(new QueryWrapper<TbApplyMaterialInfoBean>().eq("info_id", old_bean.getInfo_id()).last("limit 1"));
    if (apply_material != null) {
      String new_info_id = ToolUtils.getToken();
      old_bean.setId(null);
      old_bean.setInfo_id(new_info_id);
      old_bean.setCreate_time(new Date());
      old_bean.setExpire_time(Date.from(LocalDateTime.now().plusYears(1l).atZone(ZoneId.systemDefault()).toInstant()));
      mapper.insert(old_bean);
    }
    return ToolUtils.succ(old_bean);
  }

  @LoginRequired
  @ResponseBody
  @RequestMapping(value = {"contact/add", "contact/update"}, produces = "application/json;charset=UTF-8")
  public Object updateEmergencyContact(@LoginInfo LoginInfoCtx li, @RequestBody List<TbUserEmergencyContactBean> beans, HttpServletRequest req) {
    logger.info("----- recv bean: " + JSON.toJSONString(beans));
    if (!CheckDuplicateSubmit("contact", li.getUser_id())) {
      return ToolUtils.fail(1, "duplicate_submit");
    }

    if (beans == null || beans.size() < 2) {
      return ToolUtils.fail(1, "param_check_fail");
    }

    String info_id = null;
    for (TbUserEmergencyContactBean bean : beans) {
      if (bean.getInfo_id() != null) {
        info_id = bean.getInfo_id();
        break;
      }
    }

    boolean check_failed = beans.stream().anyMatch(x -> (x.getRelationship() == null));
    if (check_failed) {
      logger.error(String.format("[update_contract_error]: user_id => %d, app-id: %s, channel-id: %s, info: %s", li.getUser_id(),  req.getHeader("app-id"), req.getHeader("channel-id"), JSON.toJSONString(beans)));
    }

    Date new_create_time = new Date();
    final String the_info_id = info_id;
    beans.forEach(bean -> {
      bean.setInfo_id(the_info_id);
      if (bean.getUser_id() == null) {
        bean.setUser_id(li.getUser_id());
      }
      if (bean.getCreate_time() == null) {
        bean.setCreate_time(new_create_time);
      }
    });

    List<Map> rets = new ArrayList<>();
    String new_info_id = ToolUtils.getToken();
    beans.forEach(bean -> {
      if (bean.getInfo_id() == null) {
        bean.setUser_id(li.getUser_id());
        bean.setInfo_id(new_info_id);
        bean.setCreate_time(new_create_time);
      }
      if (bean.getId() == null) {
        tb_user_emergency_contact_mapper.insert(bean);
      } else {
        tb_user_emergency_contact_mapper.updateById(bean);
      }
      rets.add(ImmutableMap.of("id", bean.getId(), "info_id", bean.getInfo_id()));
    });
    ClearDuplicateSubmit("contact", li.getUser_id());
    logger.info("----- return bean: " + JSON.toJSONString(rets));
    return ToolUtils.succ(rets);
  }

  @LoginRequired
  @ResponseBody
  @RequestMapping(value = "contact/get", produces = "application/json;charset=UTF-8")
  public Object getUserContactInfo(@LoginInfo LoginInfoCtx li) {
    logger.info("recv request ...");
    BaseMapper<TbUserEmergencyContactBean> mapper = tb_user_emergency_contact_mapper;
    Wrapper<TbUserEmergencyContactBean> cond = new QueryWrapper<TbUserEmergencyContactBean>().eq("user_id", li.getUser_id()).orderByDesc("create_time").last("limit 1");
    TbUserEmergencyContactBean old_bean = mapper.selectOne(cond);
    if (old_bean == null) {
      return ToolUtils.succ(null);
    }
    if (old_bean == null || old_bean.getExpire_time().getTime() < System.currentTimeMillis()) {
      return ToolUtils.succ(null);
    }

    List<TbUserEmergencyContactBean> cands = tb_user_emergency_contact_mapper.selectList(new QueryWrapper<TbUserEmergencyContactBean>().eq("info_id", old_bean.getInfo_id()));
    TbApplyMaterialInfoBean apply_material = tb_apply_info_material_mapper.selectOne(new QueryWrapper<TbApplyMaterialInfoBean>().eq("info_id", old_bean.getInfo_id()).last("limit 1"));
    if (apply_material != null) {
      String new_info_id = ToolUtils.getToken();
      for (int i = 0; i < cands.size(); ++i) {
        TbUserEmergencyContactBean b = cands.get(i);
        b.setId(null);
        b.setInfo_id(new_info_id);
        b.setCreate_time(new Date());
        b.setExpire_time(Date.from(LocalDateTime.now().plusYears(1l).atZone(ZoneId.systemDefault()).toInstant()));
        tb_user_emergency_contact_mapper.insert(b);
      }
    }
    return ToolUtils.succ(cands);
  }

  @LoginRequired
  @ResponseBody
  @RequestMapping(value = "contact/del", produces = "application/json;charset=UTF-8")
  public Object deleteEmergencyContact(@LoginInfo LoginInfoCtx li, @RequestBody TbUserEmergencyContactBean bean) {
    logger.info("----- recv bean: " + JSON.toJSONString(bean));
    if (bean.getId() != null) {
      tb_user_emergency_contact_mapper.deleteById(bean.getId());
    }
    return ToolUtils.succ("contact_del_succ");
  }

  // add/update/get user employment info
  @LoginRequired
  @ResponseBody
  @RequestMapping(value = {"employment/add", "employment/update"}, produces = "application/json;charset=UTF-8")
  public Object updateEmploymentInfo(@LoginInfo LoginInfoCtx li, @RequestBody TbUserEmploymentInfoBean bean) {
    logger.info("----- recv bean: " + JSON.toJSONString(bean));
    if (!CheckDuplicateSubmit("employment", li.getUser_id())) {
      return ToolUtils.fail(1, "duplicate_submit");
    }

    if (bean.getInfo_id() == null) {
      bean.setInfo_id(ToolUtils.getToken());
      bean.setUser_id(li.getUser_id());
      bean.setCreate_time(new Date());
    }

    UpdateImageRepository(li.getUser_id(),
        "",
        bean.getWork_pic(),
        DocumentaryImageCategoryEnum.NORMAL.getCode(),
        DocumentaryImageObjectEnum.WORK_ENV.getCode(),
        DocumentaryImageUploadTypeEnum.UPLOAD_BY_USER_NORMAL.getCode(),
        bean.getInfo_id());

    if (bean.getId() == null) {
      tb_user_employment_info_mapper.insert(bean);
    } else {
      tb_user_employment_info_mapper.updateById(bean);
    }
    ClearDuplicateSubmit("employment", li.getUser_id());
    return ToolUtils.succ(ImmutableMap.of("id", bean.getId(), "info_id", bean.getInfo_id()));
  }

  @LoginRequired
  @ResponseBody
  @RequestMapping(value = "employment/get", produces = "application/json;charset=UTF-8")
  public Object getUserEmploymentInfo(@LoginInfo LoginInfoCtx li) {
    logger.info("recv request ...");
    BaseMapper<TbUserEmploymentInfoBean> mapper = tb_user_employment_info_mapper;
    Wrapper<TbUserEmploymentInfoBean> cond = new QueryWrapper<TbUserEmploymentInfoBean>().eq("user_id", li.getUser_id()).orderByDesc("create_time").last("limit 1");
    TbUserEmploymentInfoBean old_bean = mapper.selectOne(cond);
    if (old_bean == null || old_bean.getExpire_time().getTime() < System.currentTimeMillis()) {
      return ToolUtils.succ(null);
    }
    TbApplyMaterialInfoBean apply_material = tb_apply_info_material_mapper.selectOne(new QueryWrapper<TbApplyMaterialInfoBean>().eq("info_id", old_bean.getInfo_id()).last("limit 1"));
    if (apply_material != null) {
      String new_info_id = ToolUtils.getToken();
      old_bean.setId(null);
      old_bean.setInfo_id(new_info_id);
      old_bean.setCreate_time(new Date());
      old_bean.setExpire_time(Date.from(LocalDateTime.now().plusYears(1l).atZone(ZoneId.systemDefault()).toInstant()));
      mapper.insert(old_bean);
    }
    return ToolUtils.succ(old_bean);
  }


  // add/update/get user bank account info
  @LoginRequired
  @ResponseBody
  @RequestMapping(value = {"bank/add", "bank/update"}, produces = "application/json;charset=UTF-8")
  public Object updateUserBankInfo(@LoginInfo LoginInfoCtx li, @RequestBody TbUserBankAccountInfoBean bean) {
    logger.info("----- recv bean: " + JSON.toJSONString(bean));
    if (!CheckDuplicateSubmit("bank", li.getUser_id())) {
      return ToolUtils.fail(1, "duplicate_submit");
    }

    if (bean.getAccount_type() < 0) {
      return ToolUtils.fail("bad account_type");
    }

    if (bean.getInfo_id() == null) {
      bean.setInfo_id(ToolUtils.getToken());
      bean.setUser_id(li.getUser_id());
      bean.setCreate_time(new Date());
    }
    if (bean.getId() == null) {
      tb_user_bank_account_mapper.insert(bean);
    } else {
      tb_user_bank_account_mapper.updateById(bean);
    }
    ClearDuplicateSubmit("bank", li.getUser_id());
    return ToolUtils.succ(ImmutableMap.of("id", bean.getId(), "info_id", bean.getInfo_id()));
  }

  @LoginRequired
  @ResponseBody
  @RequestMapping(value = "bank/get", produces = "application/json;charset=UTF-8")
  public Object getUserBankInfo(@LoginInfo LoginInfoCtx li) {
    logger.info("recv request ...");
    BaseMapper<TbUserBankAccountInfoBean> mapper = tb_user_bank_account_mapper;
    Wrapper<TbUserBankAccountInfoBean> cond = new QueryWrapper<TbUserBankAccountInfoBean>().eq("user_id", li.getUser_id()).orderByDesc("create_time").last("limit 1");
    TbUserBankAccountInfoBean old_bean = mapper.selectOne(cond);
    if (old_bean == null || old_bean.getExpire_time().getTime() < System.currentTimeMillis()) {
      return ToolUtils.succ(null);
    }
    TbApplyMaterialInfoBean apply_material = tb_apply_info_material_mapper.selectOne(new QueryWrapper<TbApplyMaterialInfoBean>().eq("info_id", old_bean.getInfo_id()).last("limit 1"));
    if (apply_material != null) {
      String new_info_id = ToolUtils.getToken();
      old_bean.setId(null);
      old_bean.setInfo_id(new_info_id);
      old_bean.setCreate_time(new Date());
      old_bean.setExpire_time(Date.from(LocalDateTime.now().plusYears(1l).atZone(ZoneId.systemDefault()).toInstant()));
      mapper.insert(old_bean);
    }
    return ToolUtils.succ(old_bean);
  }

  @LoginRequired
  @ResponseBody
  @RequestMapping(value = "info", produces = "application/json;charset=UTF-8")
  public Object TestMessage(@LoginInfo LoginInfoCtx li) {
    JSONObject parms = new JSONObject();
    parms.put("li", li);
    return ToolUtils.succ(parms);
  }
}

