package com.sup.cms.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.sup.cms.bean.po.CollectionRecordBean;
import com.sup.cms.bean.po.TbCollectionRecordBean;
import com.sup.cms.bean.vo.CollectionRecords;
import org.apache.ibatis.annotations.Param;
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

    @Select("select\n" +
            "  cr.apply_id as applyId\n" +
            "  ,ai.status as applyStatus\n" +
            "  ,grant_quota as grantQuota\n" +
            "  ,inhand_quota as inhandQuota\n" +
            "  ,cr.mobile as mobile\n" +
            "  ,cr.status as status\n" +
            "  ,cr.alert_date as alertDate\n" +
            "  ,cr.comment as comment\n" +
            "  ,cr.periods as periods\n" +
            "  ,cr.operator_id as operatorId\n" +
            "  ,cau.name as operatorName\n" +
            "from (\n" +
            "   select * from tb_cms_collection_record where 1=1 \n" +
            "     ${conditions} \n" +
            ") cr\n" +
            "left join tb_cms_auth_user cau on cr.operator_id=cau.id\n" +
            "left join tb_apply_info ai on cr.apply_id=ai.id")
    List<CollectionRecords> exportRecords(@Param(value = "conditions") String conditions);
}
