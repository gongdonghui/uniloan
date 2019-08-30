package com.sup.web;

import com.sup.service.TestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by xidongzhou1 on 2019/8/30.
 */
@RestController
public class TestController {
  @Autowired
  TestService test_service;

  @ResponseBody
  @RequestMapping(value = "/test", produces = "application/json;charset=UTF-8")
  public Object TestHandler() {
    return test_service.getCus();
  }
}
