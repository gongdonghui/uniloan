package com.sup.cms.facade.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.google.common.base.Strings;
import com.sup.cms.bean.vo.AddUserGroupMemParams;
import com.sup.cms.bean.vo.GroupIdParam;
import com.sup.cms.bean.vo.UserGroupParams;
import com.sup.cms.facade.UserGroupConfigFacade;
import com.sup.cms.mapper.AuthUserBeanMapper;
import com.sup.cms.mapper.AuthUserGroupBeanMapper;
import com.sup.common.bean.AuthUserBean;
import com.sup.common.bean.AuthUserGroupBean;
import com.sup.common.util.Result;
import lombok.extern.log4j.Log4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.List;

/**
 * gongshuai
 * <p>
 * 2020/2/22
 */
@Log4j
@RestController
public class UserGroupConfigImplFacade implements UserGroupConfigFacade {
    @Autowired
    private AuthUserGroupBeanMapper authUserGroupBeanMapper;

    @Autowired
    private AuthUserBeanMapper authUserBeanMapper;

    /**
     * 创建用户分组
     *
     * @param authUserGroupBean
     * @return
     */
    @Override
    public Result createGroup(AuthUserGroupBean authUserGroupBean) {
        if (authUserGroupBean != null) {
            Date   now  =  new Date();
            authUserGroupBean.setCreate_time(now);
            this.authUserGroupBeanMapper.insert(authUserGroupBean);
            return Result.succ();
        } else {
            return Result.fail("input error");
        }

    }

    /**
     * 删除用户组
     *
     * @param group_id
     * @return
     */
    @Override
    public Result deleteGroup(GroupIdParam  param) {
        if (param!= null &&   param.getGroup_id()!=null) {
            this.authUserGroupBeanMapper.deleteById(param.getGroup_id());
            return Result.succ();

        } else {
            return Result.fail("input error");
        }
    }

    /**
     * 查询用户组
     *
     * @param userGroupParams
     * @return
     */
    @Override
    public Result<List<AuthUserGroupBean>> listGroup(UserGroupParams userGroupParams) {

        if (userGroupParams != null) {
            String type = userGroupParams.getType();
            QueryWrapper<AuthUserGroupBean> queryWrapper = new QueryWrapper<>();
            if (Strings.isNullOrEmpty(type)) {
                queryWrapper.eq("type", type);
            }

            List<AuthUserGroupBean> ret = this.authUserGroupBeanMapper.selectList(queryWrapper);
            return Result.succ(ret);

        } else {
            return Result.fail("input error");
        }

    }

    /**
     * 添加成员
     *
     * @param addUserGroupMemParams
     * @return
     */
    @Override
    public Result addMemeber(AddUserGroupMemParams addUserGroupMemParams) {

        if (addUserGroupMemParams != null) {


            Integer groupId = addUserGroupMemParams.getGroupId();
            if (!addUserGroupMemParams.getUsers().isEmpty()) {

                List<AuthUserBean> users = this.authUserBeanMapper.selectBatchIds(addUserGroupMemParams.getUsers());
                for (AuthUserBean userBean : users) {
                    userBean.setGroupId(groupId);
                    this.authUserBeanMapper.updateById(userBean);
                }
            }
            return Result.succ();
        } else {
            return Result.fail("input error");
        }
    }

    /**
     * 删除成员
     *
     * @param addUserGroupMemParams
     * @return
     */
    @Override
    public Result delMemeber(AddUserGroupMemParams addUserGroupMemParams) {
        if (addUserGroupMemParams != null) {
            Integer groupId = addUserGroupMemParams.getGroupId();
            if (!addUserGroupMemParams.getUsers().isEmpty()) {

                List<AuthUserBean> users = this.authUserBeanMapper.selectBatchIds(addUserGroupMemParams.getUsers());
                for (AuthUserBean userBean : users) {
                    userBean.setGroupId(-1);
                    this.authUserBeanMapper.updateById(userBean);
                }
            }
            return Result.succ();
        } else {
            return Result.fail("input error");
        }
    }

    /**
     * 查询用户组成员
     *
     * @param group_id
     * @return
     */
    @Override
    public Result<List<AuthUserBean>> listMem(GroupIdParam param) {
        if (param!= null && param.getGroup_id()!= null) {
            QueryWrapper<AuthUserBean> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("group_id", param.getGroup_id());
            List<AuthUserBean> userBeanList = this.authUserBeanMapper.selectList(queryWrapper);
            return Result.succ(userBeanList);
        } else {
            return Result.fail("input error");
        }

    }
}
