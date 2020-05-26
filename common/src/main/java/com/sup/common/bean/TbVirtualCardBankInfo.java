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
@TableName("tb_virtual_card_bank_info")
public class TbVirtualCardBankInfo {
    @TableId(type = IdType.AUTO)
    private Integer id;
    private String bank_account_name;
    private String bank_name;
    private String branch_name;
    private String link;
    private Integer service_fee;
    private Date create_time;
}
