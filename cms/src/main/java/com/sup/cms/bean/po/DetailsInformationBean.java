package com.sup.cms.bean.po;

import lombok.Data;

/**
 * 返回的是图片的key  在web端通过ssd拉取即可
 *
 * @Author: kouichi
 * @Date: 2019/10/7 23:25
 */
@Data
public class DetailsInformationBean {
    /**
     * 身份证正面
     */
    private String pic1;
    /**
     * 手持身份证
     */
    private String pic2;
    /**
     * 工作环境
     */
    private String pic3;
}
