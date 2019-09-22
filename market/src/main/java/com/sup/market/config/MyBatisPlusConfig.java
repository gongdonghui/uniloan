package com.sup.market.config;

import com.baomidou.mybatisplus.extension.parsers.DynamicTableNameParser;
import com.baomidou.mybatisplus.extension.parsers.ITableNameHandler;
import com.baomidou.mybatisplus.extension.plugins.PaginationInterceptor;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by xidongzhou1 on 2019/8/30.
 */
@Configuration
@MapperScan("com.sup.market.mapper")
public class MyBatisPlusConfig {
  @Bean
  public PaginationInterceptor paginationInterceptor() {
    PaginationInterceptor pc = new PaginationInterceptor();
    DynamicTableNameParser dmp = new DynamicTableNameParser();
    Map<String, ITableNameHandler> map = new HashMap<>();
    map.put("group_items", ((metaObject, sql, table_name) -> {
      return  null;
    }));
    dmp.setTableNameHandlerMap(map);
    pc.setSqlParserList(Collections.singletonList(dmp));
    return pc;
  }
}
