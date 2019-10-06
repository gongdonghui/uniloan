package com.sup.cms.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.sup.cms.bean.po.ApplyManagementGetListBean;
import com.sup.cms.bean.po.ApplyApprovalGetListBean;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 各种大连表合集
 *
 * @Author: kouichi
 * @Date: 2019/10/6 17:04
 */
public interface CrazyJoinMapper extends BaseMapper {
    @Select("select " +
            "a.id,a.apply_id,a.create_time,a.task_type,a.status,a.operator_id,a.update_time," +
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
    List<ApplyApprovalGetListBean> applyApprovalGetList(String conditions, Integer offset, Integer rows);

    @Select("select " +
            "a.id as applyId,a.status as status,a.apply_quota as amount,1 as jiekuanqixian,a.fee_type as huanKuanFangShi,'' as shangHuiMingCheng,a.create_time as dealDate,a.create_time as createTime,a.update_time as updateTime" +
            "b.name as productName," +
            "c.name as name," +
            "d.APP_NAME as appName" +
            " from tb_apply_info a" +
            " left join tb_product_info b on a.product_id=b.id" +
            " left join tb_user_citizen_identity_card_info c on a.user_id=c.user_id" +
            " left join tb_app_version d on a.app_id=d.id" +
            " where 1=1" +
            "${conditions}" +
            " limit #{offset},#{rows}")
    List<ApplyManagementGetListBean> applyManagementGetList(String conditions, Integer offset, Integer rows);

}
