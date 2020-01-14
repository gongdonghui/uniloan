package com.sup.cms.controller;

import com.google.common.base.Strings;
import com.google.common.collect.Maps;
import com.sup.cms.bean.po.LoanRepayInfoGetListBean;
import com.sup.cms.bean.po.LoanUnRepayInfoGetListBean;
import com.sup.cms.bean.vo.LoanRepayInfoGetListParams;
import com.sup.cms.bean.vo.LoanUnRepayInfoGetListParams;
import com.sup.cms.bean.vo.RepayOfflineConfirmParams;
import com.sup.cms.mapper.CrazyJoinMapper;
import com.sup.common.loan.ApplyStatusEnum;
import com.sup.common.util.DateUtil;
import com.sup.common.util.ResponseUtil;
import lombok.extern.log4j.Log4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;
import java.util.Map;

/**
 * 贷款管理下面的子页面
 *
 * @Author: kouichi
 * @Date: 2019/10/13 17:41
 */
@Log4j
@RestController
@RequestMapping("/repay")
public class RepayController {
    @Autowired
    private CrazyJoinMapper crazyJoinMapper;

    /**
     * 确认线下还款
     * @param params
     * @return
     */
    @PostMapping("/offline/confirm")
    public String confirmOfflineRepay(@RequestBody @Valid RepayOfflineConfirmParams params) {
        StringBuilder sb = new StringBuilder();
        // TODO

        return ResponseUtil.success();
    }

}
