package com.sup.core.util;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.sup.common.bean.TbRepayPlanBean;
import com.sup.common.util.DateUtil;
import com.sup.core.bean.OverdueInfoBean;
import com.sup.core.mapper.RepayPlanMapper;

import java.util.Date;
import java.util.List;

/**
 * gongshuai
 * <p>
 * 2019/9/21
 */
public class OverdueUtils {


    public static OverdueInfoBean getMaxOverdueDays(String userId, RepayPlanMapper   repayPlanMapper) {


        List<TbRepayPlanBean> plans = repayPlanMapper.selectList(new QueryWrapper<TbRepayPlanBean>().eq("user_id", Integer.parseInt(userId)).orderByAsc("repay_start_date"));
        if (plans.isEmpty()) {
            return null;
        }
        int times = 0;
        int max_days = 0;
        int latest_days = -1;
        for (TbRepayPlanBean repayPlanBean : plans) {
            Date repay_date = repayPlanBean.getRepay_time();
            Date repay_end_date = repayPlanBean.getRepay_end_date();
            if (repay_date != null) {
                int days = DateUtil.daysbetween(repay_end_date, repay_date);
                if (days > 0) {
                    times++;
                    max_days = days > max_days ? days : max_days;
                    if (latest_days < 0)
                        latest_days = days;    //latest overdue days
                }
            }
        }
        OverdueInfoBean ret = new OverdueInfoBean();
        ret.setTimes(times);
        ret.setMax_days(max_days);
        ret.setLatest_days(latest_days);
        return ret;

    }
}
