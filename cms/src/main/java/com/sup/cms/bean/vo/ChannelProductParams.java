package com.sup.cms.bean.vo;

import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * @Author: kouichi
 * @Date: 2019/9/18 16:40
 */
@Data
public class ChannelProductParams {
    @NotNull
    private Integer channelId;
    @NotNull
    private List<Integer> productIdList;
}
