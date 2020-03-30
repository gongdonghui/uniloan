package com.sup.backend.service;

import com.alibaba.fastjson.JSON;
import com.sup.backend.util.LangUtil;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

@Component
public class LangUtilService {
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

  @Bean
  public LangUtil GetLangUtil() throws Exception {
    return LangUtil.of(JSON.parseObject(LoadResourceFile("app.dict")));
  }
}
