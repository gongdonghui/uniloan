package com.sup.backend.config;

import com.baomidou.mybatisplus.extension.parsers.DynamicTableNameParser;
import com.baomidou.mybatisplus.extension.parsers.ITableNameHandler;
import com.baomidou.mybatisplus.extension.plugins.PaginationInterceptor;
import com.baomidou.mybatisplus.extension.plugins.PerformanceInterceptor;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by xidongzhou1 on 2019/8/30.
 */
@EnableTransactionManagement
@Configuration
@MapperScan("com.sup.backend.mapper")
public class MyBatisPlusConfig {
  @Bean
  public PaginationInterceptor paginationInterceptor() {
    PaginationInterceptor pc = new PaginationInterceptor();
    DynamicTableNameParser dmp = new DynamicTableNameParser();
    Map<String, ITableNameHandler> map = new HashMap<>();
    map.put("tb_user_regist_info", ((metaObject, sql, table_name) -> {
      return  null;
    }));
    dmp.setTableNameHandlerMap(map);
    pc.setSqlParserList(Collections.singletonList(dmp));
    return pc;
  }

  @Bean
  public PerformanceInterceptor performanceInterceptor() {
    PerformanceInterceptor pm = new PerformanceInterceptor();
    pm.setWriteInLog(false);
    return pm;
  }
}
