package com.sup.cms.bean.vo;

import lombok.Data;

import javax.validation.constraints.Min;
import java.util.Date;
import java.util.List;

/**
 * @Author: kouichi
 * @Date: 2019/10/13 19:49
 */
@Data
public class OverdueTaskRecycleParams {
    private Integer         distributor_id; // 分配人id
    private Integer         operatorId;     // 催收员id
    private List<Integer>   applyIdList;    // apply ids
}
