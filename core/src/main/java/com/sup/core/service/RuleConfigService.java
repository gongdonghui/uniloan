package com.sup.core.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.sup.common.bean.CreditLevelRuleBean;
import com.sup.common.bean.TbApplyInfoBean;
import com.sup.common.bean.TbUserRegistInfoBean;
import com.sup.common.loan.ApplyStatusEnum;
import com.sup.common.util.GsonUtil;
import com.sup.core.bean.OverdueInfoBean;
import com.sup.core.mapper.ApplyInfoMapper;
import com.sup.core.mapper.CreditLevelRulesMapper;
import com.sup.core.mapper.RepayPlanMapper;
import com.sup.core.mapper.UserRegisterInfoMapper;
import com.sup.core.util.OverdueUtils;
import lombok.extern.log4j.Log4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * gongshuai
 * <p>
 * 2020/1/5
 */
@Log4j
@Service
public class RuleConfigService {

    @Autowired
    private UserRegisterInfoMapper userRegisterInfoMapper;

    @Autowired
    private CreditLevelRulesMapper creditLevelRulesMapper;
    @Autowired
    private ApplyInfoMapper applyInfoMapper;

    @Autowired
    private RepayPlanMapper repayPlanMapper;

    //根据用户ID更新用户信用等级
    public void updateCreditLevelForUser(Integer user_id) {


        List<CreditLevelRuleBean> creditLevelRuleBeans = this.creditLevelRulesMapper.selectList(new QueryWrapper<CreditLevelRuleBean>().orderByDesc("level"));
        List<TbApplyInfoBean> applyInfoBeanList = this.applyInfoMapper.selectList(new QueryWrapper<TbApplyInfoBean>().eq("status", ApplyStatusEnum.APPLY_REPAY_ALL.getCode()).eq("user_id", user_id));  //结清状态的申请单
        int reloan_times = applyInfoBeanList != null ? applyInfoBeanList.size() : 0;
        this.updateCreditLevelForUser(user_id, reloan_times, creditLevelRuleBeans);
    }

    //与定时任务共用部分
    public void updateCreditLevelForUser(Integer user_id, Integer reloan_times, List<CreditLevelRuleBean> creditLevelRuleBeans) {

        OverdueInfoBean overdueInfoBean = OverdueUtils.getMaxOverdueDays(user_id, this.repayPlanMapper);
        log.info("updateCreditLevelForUser user_id=" + user_id + ", reloan_times=" + reloan_times + ", bean=" + GsonUtil.toJson(overdueInfoBean));
        for (CreditLevelRuleBean creditLevelRuleBean : creditLevelRuleBeans) {
            if (reloan_times >= creditLevelRuleBean.getReloan_times() && overdueInfoBean.getMax_days() <= creditLevelRuleBean.getMax_overdue_days()) {
                TbUserRegistInfoBean userRegistInfoBean = this.userRegisterInfoMapper.selectById(user_id);
                if (userRegistInfoBean != null) {
                    userRegistInfoBean.setCredit_level(creditLevelRuleBean.getLevel());
                    this.userRegisterInfoMapper.updateById(userRegistInfoBean);
                } else {
                    log.error("Failed to update Credit level user_id:" + user_id);
                }
                break;
            }

        }

    }
}
