package com.sup.backend.web;

import com.sup.backend.service.SSDBClient;
import com.sup.backend.util.ToolUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;


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
  @RequestMapping(value = "set", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
  public Object Test(@RequestParam String key, @RequestParam String val) {
    boolean r = ssdbClient.Set(key, val);
    return ToolUtils.succ(r);
  }
}

