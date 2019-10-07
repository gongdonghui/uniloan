package com.sup.cms.bean.po;

import lombok.Data;

/**
 * @Author: kouichi
 * @Date: 2019/10/7 18:37
 */
@Data
public class DetailsEmergencyContact {
    //    联系人
    private String name;
    //            电话
    private String phone;
    //    关系 关系 0|父母 1|同事
    private Integer relationship;
}
