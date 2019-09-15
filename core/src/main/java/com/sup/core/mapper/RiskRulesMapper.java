package com.sup.core.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.sup.core.bean.RiskRulesBean;

import java.util.List;

public interface RiskRulesMapper extends BaseMapper<RiskRulesBean> {
    //TODO  add  impl
    public List<RiskRulesBean>   loadRulesByProduct(Integer product_id);
}
