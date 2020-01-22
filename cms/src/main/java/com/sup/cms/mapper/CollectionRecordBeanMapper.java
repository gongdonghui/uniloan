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
    @Select("select" +
            "  cr.mobile as mobile" +
            "  ,cr.status as status" +
            "  ,cr.alert_date as alertDate" +
            "  ,cr.comment as comment" +
            "  ,cr.apply_id as applyId" +
            "  ,cr.periods as periods" +
            "  ,cr.operator_id as operatorId" +
            "  ,cau.name as operatorName" +
            "  ,cr.create_time as createTime" +
            " from (select * from tb_cms_collection_record where apply_id=#{applyId}) cr" +
            " left join tb_cms_auth_user cau on cr.operator_id=cau.id"
    )
    List<CollectionRecordBean> getRecords(@RequestParam("applyId") String applyId);
}
