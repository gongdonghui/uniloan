package com.sup.cms.bean.vo;

import lombok.Data;

import java.util.List;

/**
 * gongshuai
 * <p>
 * 2020/2/22
 */
@Data
public class AddUserGroupMemParams {
    private Integer  groupId;
    private List<Integer> users;
}
