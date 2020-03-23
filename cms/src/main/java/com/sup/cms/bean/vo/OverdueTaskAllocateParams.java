package com.sup.cms.bean.vo;

import lombok.Data;

import java.util.List;

/**
 * @Author: kouichi
 * @Date: 2019/10/13 19:49
 */
@Data
public class OverdueTaskAllocateParams {
    private Integer         distributor_id; // 分配人id
    private Integer         operatorId;     // 催收员id
    private List<Integer>   applyIdList;    // apply ids

    // 以下为自动分配时使用的参数
    private List<Integer>   operatorsForM1;
    private List<Integer>   operatorsForM2;


    private Integer  asset_level;
}
