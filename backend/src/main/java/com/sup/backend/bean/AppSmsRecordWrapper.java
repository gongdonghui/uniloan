package com.sup.backend.bean;

import lombok.Data;

import java.util.List;

/**
 * Created by xidongzhou1 on 2020/6/9.
 */
@Data
public class AppSmsRecordWrapper {
  String device_id;
  String mobile;
  List<AppSmsRecord> records;
}
