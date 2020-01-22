package com.sup.cms.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.sup.cms.bean.po.CollectionRecordBean;
import com.sup.cms.bean.po.TbCollectionRecordBean;
import org.apache.ibatis.annotations.Select;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * @Author: kouichi
 * @Date: 2019/10/2 16:25
 */
public interface CollectionRecordBeanMapper extends BaseMapper<TbCollectionRecordBean> {
    @Select("select\n" +
            "  cr.mobile as mobile\n" +
            "  ,cr.status as status\n" +
            "  ,cr.alert_date as alertDate\n" +
            "  ,cr.comment as comment\n" +
            "  ,cr.apply_id as applyId\n" +
            "  ,cr.periods as periods\n" +
            "  ,cr.operator_id as operatorId\n" +
            "  ,cau.name as operaName\n" +
            "  ,cr.create_time as createTime\n" +
            " from (select * from tb_cms_collection_record where apply_id=#{applyId}) cr\n" +
            " left join tb_cms_auth_user cau\n" +
            " on cr.operator_id=cau.id")
    List<CollectionRecordBean> getRecords(@RequestParam("applyId") String applyId);
}
