package com.sup.common.bean;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.util.Date;

/**
 * Created by xidongzhou1 on 2019/9/5.
 */
@Data
@NoArgsConstructor
@Accessors(chain = true)
@TableName("tb_user_employment_info")
public class TbUserEmploymentInfoBean {
  @TableId(type = IdType.AUTO)
  private Integer id;
  private String info_id;
  private Integer user_id;

  private String company;
  private Integer company_province;
  private Integer company_city;
  private Integer company_country;
  private String company_addr;
  private String phone;
  private Integer job_occupation;
  private Integer work_period;
  private Integer income;
  private String work_pic;

  private Date create_time;
  private Date expire_time;
}
