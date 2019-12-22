package com.sup.cms.bean.vo;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.Map;

@Data
public class ApplyImageParams {
    @NotNull
    private Integer userId;

    @NotNull
    private Integer applyId;

    @NotNull
    private Integer operatorId;

    private String infoId;

    /**
     * image type(see DocumentaryImageObjectEnum) => image ssdb key
     * type: 0| 身份证正面 1| 身份证反面  2|手持身份证 3|工作环境 4|驾驶证 5|医保证 6|还款记录 7|其他
     */
    @NotNull
    private Map<Integer, String> imageKeys;
}
