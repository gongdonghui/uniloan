package com.sup.cms.controller;

import com.google.common.base.Strings;
import com.google.common.collect.Maps;
import com.sup.cms.bean.po.AfterLoanOverdueGetListBean;
import com.sup.cms.bean.vo.AfterLoanOverdueGetListParams;
import com.sup.cms.mapper.CrazyJoinMapper;
import com.sup.cms.util.ResponseUtil;
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
 * @Author: kouichi
 * @Date: 2019/10/13 19:47
 */
@Log4j
@RestController
@RequestMapping("/afterLoan")
public class AfterLoanController {

    @Autowired
    private CrazyJoinMapper crazyJoinMapper;

    @PostMapping("/overdue/getList")
    public String overdueGetList(@RequestBody @Valid AfterLoanOverdueGetListParams params) {
        StringBuilder sb = new StringBuilder();
        //逾期未还的 repay表就是状态是0  逾期已还的是repay表的状态就是1或者2
        if (params.getType() == 0) {
            sb.append(" and f.repay_status=0");
        } else {
            sb.append(" and (f.repay_status=1 or f.repay_status=2)");
        }
        if (params.getApplyId() != null) {
            sb.append(" and a.id=" + params.getApplyId());
        }
        if (params.getProductId() != null) {
            sb.append(" and a.product_id=" + params.getProductId());
        }
        if (!Strings.isNullOrEmpty(params.getCidNo())) {
            sb.append(" and c.cid_no='" + params.getCidNo() + "'");
        }
        if (!Strings.isNullOrEmpty(params.getMobile())) {
            sb.append(" and d.mobile='" + params.getMobile() + "'");
        }
        if (!Strings.isNullOrEmpty(params.getAppName())) {
            sb.append(" and g.app_name='" + params.getAppName() + "'");
        }
        log.info("overdueGetList conditions=" + sb.toString());
        Integer offset = (params.getPage() - 1) * params.getPageSize();
        Integer rows = params.getPageSize();
        List<AfterLoanOverdueGetListBean> l = crazyJoinMapper.afterLoanOverdueGetList(sb.toString(), offset, rows);
        Map m = Maps.newHashMap();
        m.put("total",crazyJoinMapper.afterLoanOverdueGetListForPaging(sb.toString()));
        m.put("list",l);
        return ResponseUtil.success(m);
    }
}
