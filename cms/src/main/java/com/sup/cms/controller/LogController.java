package com.sup.cms.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.sup.cms.bean.vo.OperationLogParams;
import com.sup.cms.mapper.OperationLogMapper;
import com.sup.common.util.ResponseUtil;
import com.sup.common.bean.TbOperationLogBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 产品页面
 *
 * @Author: kouichi
 * @Date: 2019/9/18 16:33
 */
@RequestMapping("/log")
@RestController
public class LogController {

    @Autowired
    private OperationLogMapper operationLogMapper;

    @PostMapping("/get")
    public String getList(@Valid @RequestBody OperationLogParams params) {
        QueryWrapper<TbOperationLogBean> wrapper = new QueryWrapper<>();
        wrapper.eq("apply_id", params.getApplyId());
        if (params.getOperationType() != null) {
            wrapper.eq("operation_type", params.getOperationType());
        }
        List<TbOperationLogBean> list = operationLogMapper.selectList(wrapper);
        if (list == null) {
            list = new ArrayList<>();
        }
        return ResponseUtil.success(list);
    }

    @PostMapping("/insert")
    public String insert(@Valid @RequestBody TbOperationLogBean bean) {
        bean.setCreate_time(new Date());
        if (operationLogMapper.insert(bean) > 0) {
            return ResponseUtil.success();
        } else {
            return ResponseUtil.failed();
        }
    }

    @GetMapping("/delete")
    public String delete(@RequestParam("id") Integer id) {
        if (operationLogMapper.deleteById(id) > 0) {
            return ResponseUtil.success();
        } else {
            return ResponseUtil.failed();
        }
    }

}
