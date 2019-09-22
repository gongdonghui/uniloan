package com.sup.cms.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.sup.cms.bean.po.ApplyOperationTaskBean;
import com.sup.cms.bean.po.ApprovalGetListBean;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * @Author: kouichi
 * @Date: 2019/9/22 11:15
 */
public interface ApplyOperationTaskMapper extends BaseMapper<ApplyOperationTaskBean> {
    @Select("select " +
            "a.id,a.apply_id,a.create_time," +
            "b.credit_class,b.create_time as apply_create_time,b.expire_time as apply_expire_time," +
            "c.name as product_name" +
            "d.cid_no,d.name," +
            "e.mobile" +
            " from tb_operation_task a" +
            " join tb_apply_info b on a.apply_id=b.id" +
            " join tb_produt_info c on b.product_id=c.id" +
            " join tb_user_citizen_identity_card_info d on b.user_id=d.user_id" +
            " join tb_user_regist_info e on b.user_id=e.id" +
            " where 1=1" +
            "${conditions}" +
            " limit #{offset},#{rows}")
    List<ApprovalGetListBean> search(String conditions, Integer offset, Integer rows);

}
