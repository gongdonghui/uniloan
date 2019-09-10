package com.sup.core.facade.impl;

import com.sup.core.facade.CreditFacade;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.RestController;

/**
 * Project:uniloan
 * Class:  CreditFacadeImpl
 * <p>
 * Author: guanfeng
 * Create: 2019-09-09
 */

@RestController
public class CreditFacadeImpl implements CreditFacade {

    @Scheduled(cron = "0 */10 * * * ?")
    public void checkApplyInfo() {
        // 1. 获取所有新提交的进件

        // 2. 风险变量正则化

        // 3. 根据规则对用户评级

        // 4. 根据规则进行自动审查

        // 5. 更新进件状态
    }

    @Override
    public Object autoAudit(String version, String applyId) {
        return null;
    }

}
