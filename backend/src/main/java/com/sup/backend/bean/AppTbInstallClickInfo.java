package com.sup.backend.bean;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;

@Data
public class AppTbInstallClickInfo {
  private String install_referrer;
  private Long referrer_click_date;
  private Long install_begin_date;
  private String device_id;
  private String mobile;
}
