package com.sup.backend.web;

import com.sup.backend.bean.LoginInfoCtx;
import com.sup.backend.core.LoginInfo;
import com.sup.backend.core.LoginRequired;
import com.sup.backend.service.SSDBClient;
import com.sup.backend.util.ToolUtils;
import org.apache.commons.lang3.RandomUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.text.SimpleDateFormat;
import java.util.Date;


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

  @ResponseBody
  @RequestMapping("upload")
  public Object uploadFile(@RequestParam("upload_file") MultipartFile file) throws Exception {
    String tag = String.format("%d_%s_%s", RandomUtils.nextInt(1000, 9999), "systemu", (new SimpleDateFormat("yyyyMMddHHmmss")).format(new Date()));
    byte[] binary = file.getBytes();
    logger.info(String.format("tag => %s, file_size => %d", tag, binary.length));
    boolean succ = ssdbClient.SetBytes(tag, binary);
    if (succ) {
      return ToolUtils.succ(tag);
    } else {
      return ToolUtils.fail("save_to_ssdb_error");
    }
  }

  @ResponseBody
  @RequestMapping(value = "set", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
  public Object Test(@RequestParam String key, @RequestParam String val) {
    boolean r = ssdbClient.Set(key, val);
    return ToolUtils.succ(r);
  }
}

