package com.sup.cms.facade.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.sup.cms.facade.TaskLabelConfigFacade;
import com.sup.cms.mapper.TbTaskLabelMapper;
import com.sup.common.bean.CommentLabelBean;
import com.sup.common.param.GetLabelParam;
import com.sup.common.param.TaskLabelParam;
import com.sup.common.util.ResponseUtil;
import com.sup.common.util.Result;
import lombok.extern.log4j.Log4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;

/**
 * gongshuai
 * <p>
 * 2019/10/14
 */

@RestController
@Log4j
public class TaskLabelConfigImplFacade implements TaskLabelConfigFacade {

    @Autowired
    TbTaskLabelMapper tbTaskLabelMapper;

    @Override
    public Result addLabel(TaskLabelParam param) {
        CommentLabelBean taskLabel = new CommentLabelBean();
        taskLabel.setLabel_name(param.getLabel_name());
        taskLabel.setCreator((param.getCreator()));
        taskLabel.setContent(param.getContent());
        taskLabel.setScene(param.getScene());
        taskLabel.setCreat_time(new Date());
        this.tbTaskLabelMapper.insert(taskLabel);
        return Result.succ();

    }

    @Override
    public String getLabel(GetLabelParam param) {
        if (param != null) {

            Integer offset = (param.getPage() - 1) * param.getPageSize();
            Integer rows = param.getPageSize();
            int total = this.tbTaskLabelMapper.selectCount(new QueryWrapper<>());
            List<CommentLabelBean> ret = this.tbTaskLabelMapper.getTaskLabelBypage(offset, rows);
            Map m = new HashMap();
            m.put("total", total);
            m.put("list", ret);
            return ResponseUtil.success(m);
        } else {
            return ResponseUtil.failed("input param invalid");
        }

    }

    @Override
    public Result delLabel(Integer label_id) {
        if (label_id != null) {
            this.tbTaskLabelMapper.deleteById(label_id);
            return Result.succ();
        } else {
            return Result.fail("input param invalid");
        }
    }
}
