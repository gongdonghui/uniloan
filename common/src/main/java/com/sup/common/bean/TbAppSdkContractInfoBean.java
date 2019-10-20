package com.sup.common.bean;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

/**
 * Project:uniloan
 * Class:  TbApplyInfoBean
 * <p>
 * Author: guanfeng
 * Create: 2019-09-05
 */

@Data
@TableName("tb_app_sdk_contract_info")
public class TbAppSdkContractInfoBean {
  @TableId(type = IdType.AUTO)
  private Integer id;
  private String device_id;
  private String mobile;
  private String contract_name;
  private String contract_info;
  private String contract_memo;
  private Date create_time;
  private Date update_time;
  @TableField(exist = false)
  private String signature;
  public String calcSignature() {
    return String.format("[%s|%s|%s|%s|%s]", mobile == null? "": mobile, device_id == null ? "": device_id, contract_name == null ? "": contract_name, contract_info == null ? "" : contract_info, contract_memo == null ? "" : contract_memo);
  }
}
