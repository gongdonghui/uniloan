package com.sup.cms.facade;

import com.sup.cms.bean.po.AuthUserGroupBean;
import com.sup.cms.bean.vo.AddUserGroupMemParams;
import com.sup.cms.bean.vo.UserGroupParams;
import com.sup.common.bean.AuthUserBean;
import com.sup.common.util.Result;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

/**
 * gongshuai
 * <p>
 * 2020/2/22
 */

@RequestMapping(value = "/user_group")
public interface UserGroupConfigFacade {
    /**
     * 创建用户分组
     *
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "create", produces = "application/json;charset=UTF-8")
    Result createGroup(@RequestBody AuthUserGroupBean authUserGroupBean);

    /**
     * 删除用户组
     *
     * @param group_id
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "delete", produces = "application/json;charset=UTF-8")
    Result deleteGroup(@RequestBody Integer group_id);


    /**
     * 查询用户组
     *
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "list", produces = "application/json;charset=UTF-8")
    Result<List<AuthUserGroupBean>> listGroup(@RequestBody UserGroupParams userGroupParams);


    /**
     * 添加成员
     *
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "addMem", produces = "application/json;charset=UTF-8")
    Result addMemeber(@RequestBody AddUserGroupMemParams addUserGroupMemParams);


    /**
     * 删除成员
     *
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "delMem", produces = "application/json;charset=UTF-8")
    Result delMemeber(@RequestBody AddUserGroupMemParams addUserGroupMemParams);

    /**
     * 查询用户组成员
     *
     * @param group_id
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "Mem", produces = "application/json;charset=UTF-8")
    Result<List<AuthUserBean>> listMem(@RequestBody Integer group_id);
}
