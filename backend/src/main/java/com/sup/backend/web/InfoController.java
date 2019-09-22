package com.sup.backend.web;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.sup.backend.bean.LoginInfoCtx;
import com.sup.backend.core.LoginInfo;
import com.sup.backend.core.LoginRequired;
import com.sup.backend.mapper.TbAppSdkContractInfoMapper;
import com.sup.backend.mapper.TbAppSdkLocationInfoMapper;
import com.sup.backend.mapper.TbUserRegistInfoMapper;
import com.sup.common.bean.TbAppSdkContractInfoBean;
import com.sup.common.bean.TbAppSdkLocationInfoBean;
import com.sup.common.bean.TbUserRegistInfoBean;
import com.sup.common.util.Result;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.MediaType;
import org.springframework.util.ResourceUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.PostConstruct;
import java.io.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by xidongzhou1 on 2019/9/5.
 */

@RestController
@RequestMapping(value = "/info")
public class InfoController {
  public static Logger logger = Logger.getLogger(InfoController.class);
  private JSONObject dict;

  @PostConstruct
  public void Init() throws Exception {

    ClassPathResource resource = new ClassPathResource("app.dict");
    InputStream inputStream = resource.getInputStream();
    BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));
    String line = null;
    StringBuffer sb = new StringBuffer();
    while ((line = br.readLine()) != null) {
      sb.append(line);
    }
    br.close();
    dict = JSON.parseObject(sb.toString());
    logger.info("read_app_dict_succ: " + JSON.toJSONString(dict));
  }

  @ResponseBody
  @RequestMapping(value = "dict/getf", produces = MediaType.TEXT_PLAIN_VALUE)
  public byte[] GetFile(@RequestParam("key") String key) {
    String result = dict.toJSONString();
    return result.getBytes();
  }

  @ResponseBody
  @RequestMapping(value = "dict/get", produces = "application/json;charset=UTF-8")
  public Object QueryDict() {
    return Result.succ(dict);
  }
}

