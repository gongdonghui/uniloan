package com.sup.cms.bean.dto;

import lombok.Data;

/**
 * @Author: kouichi
 * @Date: 2019/10/3 18:18
 */
@Data
public class AuthResourceList {
    private Integer id;
    private String level1;
    private String level2;
    private String level3;
    private String name;
    private String comment;
}
