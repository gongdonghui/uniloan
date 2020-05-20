package com.sup.backend.web;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.sup.backend.service.RedisClient;
import com.sup.backend.util.ToolUtils;
import com.sup.common.bean.paycenter.vo.BankInfoVO;
import com.sup.common.param.LoanCalculatorParam;
import com.sup.common.service.CoreService;
import com.sup.common.service.PayCenterService;
import com.sup.common.util.Result;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.annotation.PostConstruct;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Created by xidongzhou1 on 2019/9/5.
 */

@RestController
@RequestMapping(value = "/info")
public class InfoController {
  public static Logger logger = Logger.getLogger(InfoController.class);
  private JSONObject dict;
  private JSONObject const_map;
  private JSONObject bank_trans;

  @Autowired
  RedisClient rc;

  @Autowired
  CoreService core;

  @Autowired
  PayCenterService pay;

  private String LoadResourceFile(String path) throws Exception {
    ClassPathResource resource = new ClassPathResource(path);
    InputStream inputStream = resource.getInputStream();
    BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));
    String line;
    StringBuffer sb = new StringBuffer();
    while ((line = br.readLine()) != null) {
      sb.append(line);
    }
    br.close();
    return sb.toString();
  }

  @PostConstruct
  public void Init() throws Exception {
    dict = JSON.parseObject(LoadResourceFile("app.dict"));
    const_map = JSON.parseObject(LoadResourceFile("const_map.dict"));
    bank_trans = JSON.parseObject(LoadResourceFile("bank_trans.dcit"));
  }

  @ResponseBody
  @RequestMapping(value = "calc", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
  public Object CaculateAmount(@RequestBody LoanCalculatorParam param) {
    Result<LoanCalculatorParam> ret = core.calculator(param);
    logger.info("param:" + JSON.toJSONString(param) + ", return: " + JSON.toJSONString(ret));
    return ret;
  }

  @ResponseBody
  @RequestMapping(value = "dict/getf", produces = MediaType.TEXT_PLAIN_VALUE)
  public byte[] GetFile(@RequestParam("key") String key) {
    String result = dict.toJSONString();
    return result.getBytes();
  }

  @ResponseBody
  @RequestMapping(value = "dict/get_bank_list", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
  public Object GetBankList() {
    if (!rc.Exist("pay_center_bank_list")) {
      JSONObject result = new JSONObject();
      result.put("id", new JSONArray());
      result.put("en", new JSONArray());
      result.put("vi", new JSONArray());
      result.put("zh", new JSONArray());
      result.put("attr", new JSONArray());
      Result<List<BankInfoVO>> bank_list_ret = pay.getBankList("0");
      for (BankInfoVO bank : bank_list_ret.getData()) {
        if (!bank_trans.containsKey(bank.getBankNo())) {
          continue;
        }
        String eng_name = bank_trans.getString(bank.getBankNo());
        result.getJSONArray("id").add(Integer.valueOf(bank.getBankNo()));
        result.getJSONArray("en").add(eng_name);
        result.getJSONArray("vi").add(eng_name);
        result.getJSONArray("zh").add(eng_name);
        result.getJSONArray("attr").add(bank.getCardSupport() + bank.getAccountSupport()*2);
      }
      rc.Set("pay_center_bank_list", result.toJSONString(), 1L, TimeUnit.DAYS);
    }
    JSONObject cache_result = JSON.parseObject(rc.Get("pay_center_bank_list"));
    return Result.succ(cache_result);
  }

  @ResponseBody
  @RequestMapping(value = "dict/get", produces = "application/json;charset=UTF-8")
  public Object QueryDict() {
    return Result.succ(dict);
  }

  @ResponseBody
  @RequestMapping(value = "const_map/get", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
  public Object GetConstMap() {
    return ToolUtils.succ(const_map);
  }

  @ResponseBody
  @RequestMapping(value = "test", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
  public Object Test() {
    return ToolUtils.succ("test", "login_succ");
  }
}

