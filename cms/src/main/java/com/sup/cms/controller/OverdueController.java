package com.sup.cms.controller;

import com.google.common.base.Strings;
import com.google.common.collect.Maps;
import com.sup.cms.bean.po.OverdueGetListBean;
import com.sup.cms.bean.vo.OverdueGetListParams;
import com.sup.cms.mapper.CrazyJoinMapper;
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
 * @Author: kouichi
 * @Date: 2019/10/13 19:47
 */
@Log4j
@RestController
@RequestMapping("/overdue")
public class OverdueController {

    @Autowired
    private CrazyJoinMapper crazyJoinMapper;


    /**
     * 催收列表（查询所有）
     * 催收列表查询（查询个人催收任务）
     * @param params
     * @return
     */
    @PostMapping("/pool/getList")
    public String getPool(@RequestBody @Valid OverdueGetListParams params) {
        StringBuilder sb = new StringBuilder();
        // TODO construct sql

        log.info("getPool conditions=" + sb.toString());
        Integer offset = (params.getPage() - 1) * params.getPageSize();
        Integer rows = params.getPageSize();
        List<OverdueGetListBean> l = crazyJoinMapper.getPoolList(sb.toString(), offset, rows);
        Map m = Maps.newHashMap();
        m.put("total",crazyJoinMapper.getPoolListCount(sb.toString()));
        m.put("list",l);
        return ResponseUtil.success(m);
    }


    /**
     * 催收分配（按催收员或者自动分配）
     */
    @PostMapping("/task/allocate")
    public String allocateTask(@RequestBody @Valid OverdueGetListParams params) {
        // TODO

        return ResponseUtil.success();
    }

    /**
     * 催收任务回收
     */
    @PostMapping("/task/recycle")
    public String recycleTask(@RequestBody @Valid OverdueGetListParams params) {
        // TODO

        return ResponseUtil.success();
    }


    /**
     * 催收报表
     */
    @PostMapping("/report/get")
    public String getReport(@RequestBody @Valid OverdueGetListParams params) {
        // TODO
        return ResponseUtil.success();
    }

}
