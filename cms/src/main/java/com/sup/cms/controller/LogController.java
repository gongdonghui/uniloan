package com.sup.cms.controller;

import com.sup.cms.bean.vo.OperationLogParams;
import com.sup.cms.mapper.CrazyJoinMapper;
import com.sup.cms.mapper.OperationLogMapper;
import com.sup.common.util.ResponseUtil;
import com.sup.common.bean.TbOperationLogBean;
import lombok.extern.log4j.Log4j;
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
@Log4j
public class LogController {

    @Autowired
    private OperationLogMapper operationLogMapper;

    @Autowired
    private CrazyJoinMapper crazyJoinMapper;

    @PostMapping("/get")
    public String getList(@Valid @RequestBody OperationLogParams params) {
        Integer apply_id = params.getApplyId();
        log.info("get operation log for "+apply_id);
        List<TbOperationLogBean> list = new ArrayList<>();
        try {
            list = crazyJoinMapper.getOperationTaskHis(apply_id);
            log.info("get operation log for "+apply_id+",size:"+list.size());
        } catch (Exception e) {
            log.error(e.getMessage());
            e.printStackTrace();
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
