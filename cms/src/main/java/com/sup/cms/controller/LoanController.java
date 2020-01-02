package com.sup.cms.controller;

import com.google.common.base.Strings;
import com.google.common.collect.Maps;
import com.sup.cms.bean.po.LoanRepayInfoGetListBean;
import com.sup.cms.bean.po.LoanUnRepayInfoGetListBean;
import com.sup.cms.bean.vo.LoanRepayInfoGetListParams;
import com.sup.cms.bean.vo.LoanUnRepayInfoGetListParams;
import com.sup.cms.mapper.CrazyJoinMapper;
import com.sup.cms.util.ResponseUtil;
import com.sup.common.loan.ApplyStatusEnum;
import com.sup.common.param.ReductionParam;
import com.sup.common.util.Result;
import lombok.extern.log4j.Log4j;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

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
@RequestMapping("/loan")
public class LoanController {
    @Autowired
    private CrazyJoinMapper crazyJoinMapper;

    /**
     * 获取还款列表，包含已还款、还款待确认的申请
     * @param params
     * @return
     */
    @PostMapping("/repayInfo/getList")
    public String repayInfoGetList(@RequestBody @Valid LoanRepayInfoGetListParams params) {
        StringBuilder sb = new StringBuilder();
        if (params.getRepayNeedConfirm() != null && params.getRepayNeedConfirm().equals(1)) {
            sb.append(" and g.status=0");
        }
        if (params.getShouldRepayDateStart() != null) {
            sb.append(" and f.repay_end_date>='" + params.getShouldRepayDateStart() + "'");
        }
        if (params.getShouldRepayDateEnd() != null) {
            sb.append(" and f.repay_start_date<='" + params.getShouldRepayDateEnd() + "'");
        }
        if (params.getActualRepayDateStart() != null) {
            sb.append(" and f.repay_time>='" + params.getActualRepayDateStart() + "'");
        }
        if (params.getActualRepayDateEnd() != null) {
            sb.append(" and f.repay_time<='" + params.getActualRepayDateEnd() + "'");
        }

        if (params.getProductId() != null) {
            sb.append(" and a.product_id=" + params.getProductId());
        }
        if (!Strings.isNullOrEmpty(params.getCidNo())) {
            sb.append(" and c.cid_no=" + params.getCidNo());
        }
        if (params.getApplyId() != null) {
            sb.append(" and a.id=" + params.getApplyId());
        }
        if (!Strings.isNullOrEmpty(params.getMobile())) {
            sb.append(" and d.mobile=" + params.getMobile());
        }
        if (params.getStatus() != null) {
            if (params.getStatus() == 0) {  // 未核销
                sb.append(" and a.status!=" + ApplyStatusEnum.APPLY_WRITE_OFF.getCode());
            } else {
                sb.append(" and a.status=" + ApplyStatusEnum.APPLY_WRITE_OFF.getCode());
            }
        }
        log.info("repayInfoGetList conditions=" + sb.toString());
        Integer offset = (params.getPage() - 1) * params.getPageSize();
        Integer rows = params.getPageSize();
        List<LoanRepayInfoGetListBean> l = crazyJoinMapper.loanRepayInfoGetList(sb.toString(), offset, rows);
        Map m = Maps.newHashMap();
        m.put("list", l);
        m.put("total",  crazyJoinMapper.loanRepayInfoGetListForPaging(sb.toString()));
        return ResponseUtil.success(m);
    }

    /**
     * 获取未还款列表
     * @param params
     * @return
     */
    @PostMapping("/unRepayInfo/getList")
    public String unRepayInfoGetList(@RequestBody @Valid LoanUnRepayInfoGetListParams params) {
        StringBuilder sb = new StringBuilder();
        if (params.getShouldRepayDateStart() != null) {
            sb.append(" and f.repay_end_date>='" + params.getShouldRepayDateStart() + "'");
        }
        if (params.getShouldRepayDateEnd() != null) {
            sb.append(" and f.repay_start_date<='" + params.getShouldRepayDateEnd() + "'");
        }

        if (params.getProductId() != null) {
            sb.append(" and a.product_id=" + params.getProductId());
        }
        if (!Strings.isNullOrEmpty(params.getCidNo())) {
            sb.append(" and c.cid_no=" + params.getCidNo());
        }
        if (params.getApplyId() != null) {
            sb.append(" and a.id=" + params.getApplyId());
        }
        if (!Strings.isNullOrEmpty(params.getMobile())) {
            sb.append(" and d.mobile=" + params.getMobile());
        }
        log.info("unRepayInfoGetList conditions=" + sb.toString());

        Integer offset = (params.getPage() - 1) * params.getPageSize();
        Integer rows = params.getPageSize();
        List<LoanUnRepayInfoGetListBean> l = crazyJoinMapper.loanUnRepayInfoGetList(sb.toString(), offset, rows);
        Map m = Maps.newHashMap();
        m.put("list", l);
        m.put("total", crazyJoinMapper.loanUnRepayInfoGetListForPaging(sb.toString()));
        return ResponseUtil.success(m);
    }

}
