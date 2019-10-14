package com.sup.cms.controller;

import com.google.common.collect.Maps;
import com.sup.cms.bean.po.LoanRepayInfoGetListBean;
import com.sup.cms.bean.po.LoanUnRepayInfoGetListBean;
import com.sup.cms.bean.vo.LoanRepayInfoGetListParams;
import com.sup.cms.bean.vo.LoanUnRepayInfoGetListParams;
import com.sup.cms.mapper.CrazyJoinMapper;
import com.sup.cms.util.ResponseUtil;
import lombok.extern.slf4j.Slf4j;
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
@Slf4j
@RestController
@RequestMapping("/loan")
public class LoanController {
    @Autowired
    private CrazyJoinMapper crazyJoinMapper;

    @PostMapping("/repayInfo/getList")
    public String repayInfoGetList(@RequestBody @Valid LoanRepayInfoGetListParams params) {
        StringBuilder sb = new StringBuilder();
        Integer offset = (params.getPage() - 1) * params.getPageSize();
        Integer rows = params.getPageSize();
        List<LoanRepayInfoGetListBean> l = crazyJoinMapper.loanRepayInfoGetList(sb.toString(), offset, rows);
        Map m = Maps.newHashMap();
        m.put("list", l);
        m.put("total",  crazyJoinMapper.loanRepayInfoGetListForPaging(sb.toString()));
        return ResponseUtil.success(m);
    }

    @PostMapping("/unRepayInfo/getList")
    public String unRepayInfoGetList(@RequestBody @Valid LoanUnRepayInfoGetListParams params) {
        StringBuilder sb = new StringBuilder();
        Integer offset = (params.getPage() - 1) * params.getPageSize();
        Integer rows = params.getPageSize();
        List<LoanUnRepayInfoGetListBean> l = crazyJoinMapper.loanUnRepayInfoGetList(sb.toString(), offset, rows);
        Map m = Maps.newHashMap();
        m.put("list", l);
        m.put("total", crazyJoinMapper.loanUnRepayInfoGetListForPaging(sb.toString()));
        return ResponseUtil.success(m);
    }




}
