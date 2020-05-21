package com.sup.common.bean;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.util.Date;

/**
 * Created by xidongzhou1 on 2019/9/5.
 */
@Data
@NoArgsConstructor
@Accessors(chain = true)
@TableName("tb_virtual_card_info_history")
public class TbVirtualCardInfoHistory {
    @TableId(type = IdType.AUTO)
    private Integer id;
    private Integer user_id;
    private String  bank_name;
    private Integer apply_id;
    private String  vc_no;
    private Integer status;
    private Integer service_fee;

    private Date create_time;
    private Date update_time;
    private Date expire_time;
}
