package com.sup.cms.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.sup.common.bean.CommentLabelBean;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * gongshuai
 * <p>
 * 2020/2/9
 */
public interface TbTaskLabelMapper extends BaseMapper<CommentLabelBean> {


    @Select("select *   " +
            " from  tb_core_comment_label   order by creat_time  desc  limit ${offset}, ${rows} ")
    List<CommentLabelBean> getTaskLabelBypage(Integer offset, Integer rows);

}
