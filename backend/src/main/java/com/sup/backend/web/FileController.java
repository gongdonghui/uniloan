package com.sup.backend.web;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.sup.backend.bean.LoginInfoCtx;
import com.sup.backend.core.LoginInfo;
import com.sup.backend.core.LoginRequired;
import com.sup.backend.mapper.TbAppSdkContractInfoMapper;
import com.sup.backend.mapper.TbAppSdkLocationInfoMapper;
import com.sup.backend.mapper.TbUserRegistInfoMapper;
import com.sup.backend.service.SSDBClient;
import com.sup.common.bean.TbAppSdkContractInfoBean;
import com.sup.common.bean.TbAppSdkLocationInfoBean;
import com.sup.common.bean.TbUserRegistInfoBean;
import com.sup.common.util.Result;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by xidongzhou1 on 2019/9/5.
 */

@RestController
@RequestMapping(value = "/file")
public class FileController {
  public static Logger logger = Logger.getLogger(FileController.class);

  @Autowired
  SSDBClient ssdbClient;

  @ResponseBody
  @RequestMapping(value = "image/get", produces = MediaType.IMAGE_JPEG_VALUE)
  public byte[] GetFile(@RequestParam("key") String key) {
    return ssdbClient.GetBytes(key);
  }
}

