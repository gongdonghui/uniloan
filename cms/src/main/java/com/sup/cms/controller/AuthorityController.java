package com.sup.cms.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.gson.reflect.TypeToken;
import com.sup.cms.bean.dto.AuthRoleList;
import com.sup.cms.bean.dto.AuthResourceList;
import com.sup.cms.bean.dto.AuthUserList;
import com.sup.cms.bean.po.*;
import com.sup.cms.bean.vo.*;
import com.sup.cms.mapper.*;
import com.sup.cms.util.GsonUtil;
import com.sup.cms.util.ResponseUtil;
import org.apache.tomcat.util.security.MD5Encoder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * 权限管理页面
 *
 * @Author: kouichi
 * @Date: 2019/10/2 17:18
 */
@RequestMapping("/auth")
@RestController
public class AuthorityController {

    @Autowired
    private StringRedisTemplate redis;
    @Autowired
    private AuthUserBeanMapper userBeanMapper;
    @Autowired
    private AuthUserRoleBeanMapper userRoleBeanMapper;
    @Autowired
    private AuthRoleBeanMapper roleBeanMapper;
    @Autowired
    private AuthRoleResourceBeanMapper roleResourceBeanMapper;
    @Autowired
    private AuthResourceBeanMapper resourceBeanMapper;

    @GetMapping("/user/doLogin")
    public String doLogin(@RequestParam("userName") String userName, @RequestParam("password") String password) {
        QueryWrapper<AuthUserBean> qw = new QueryWrapper<>();
        qw.eq("user_name", userName);
        qw.eq("password", MD5Encoder.encode(password.getBytes()));
        AuthUserBean bean = userBeanMapper.selectOne(qw);
        if (bean == null) {
            return ResponseUtil.failed("用户不存在或密码错误");
        }
        String token = MD5Encoder.encode((userName + System.currentTimeMillis()).getBytes());
        Map m = Maps.newHashMap();
        m.put("userId", bean.getId());
        m.put("token", token);
        m.put("name", bean.getName());
        redis.opsForValue().set(token, bean.getId() + "", 30, TimeUnit.MINUTES);
        return ResponseUtil.success(m);
    }

    @GetMapping("/user/changePassword")
    public String changePassword(@RequestParam("userId") Integer userId, @RequestParam("password") String password) {
        AuthUserBean bean = new AuthUserBean();
        bean.setId(userId);
        bean.setPassword(MD5Encoder.encode(password.getBytes()));
        if (userBeanMapper.updateById(bean) <= 0) {
            return ResponseUtil.failed();
        }
        return ResponseUtil.success();
    }

    @GetMapping("/user/list")
    public String userList() {
        List<AuthUserBean> l = userBeanMapper.selectList(new QueryWrapper<>());
        List<AuthUserList> ll = GsonUtil.gson.fromJson(GsonUtil.toJson(l), new TypeToken<List<AuthUserList>>() {
        }.getType());
        return ResponseUtil.success(ll);
    }

    @PostMapping("/user/insert")
    public String userInsert(@RequestBody @Valid AuthAddUserParams params) {
        //先保存用户信息
        AuthUserBean bean = GsonUtil.beanCopy(params, AuthUserBean.class);
        bean.setPassword(MD5Encoder.encode(params.getPassword().getBytes()));
        bean.setCreateTime(new Date());
        if (userBeanMapper.insert(bean) < 0) {
            return ResponseUtil.failed();
        }
        //再保存角色信息
        params.getRoleList().forEach(x -> {
            AuthUserRoleBean userRoleBean = new AuthUserRoleBean();
            userRoleBean.setUserId(bean.getId());
            userRoleBean.setRoleId(x);
            userRoleBean.setCreateTime(new Date());
            userRoleBeanMapper.insert(userRoleBean);
        });
        return ResponseUtil.success();
    }

    @PostMapping("/user/modify")
    public String userModify(@RequestBody @Valid AuthModifyUserParams params) {
        AuthUserBean bean = GsonUtil.beanCopy(params, AuthUserBean.class);
        bean.setId(params.getUserId());
        bean.setUpdateTime(new Date());
        if (userBeanMapper.updateById(bean) <= 0) {
            return ResponseUtil.failed();
        }
        return ResponseUtil.success();
    }

    @GetMapping("/user/disable")
    public String userDisable(@RequestParam("userId") Integer userId) {
        AuthUserBean bean = new AuthUserBean();
        bean.setId(userId);
        bean.setUpdateTime(new Date());
        bean.setIsValid(0);
        if (userBeanMapper.updateById(bean) <= 0) {
            return ResponseUtil.failed();
        }
        return ResponseUtil.success();
    }

    @GetMapping("/user/active")
    public String userActive(@RequestParam("userId") Integer userId) {
        AuthUserBean bean = new AuthUserBean();
        bean.setId(userId);
        bean.setUpdateTime(new Date());
        bean.setIsValid(1);
        if (userBeanMapper.updateById(bean) <= 0) {
            return ResponseUtil.failed();
        }
        return ResponseUtil.success();
    }

    @GetMapping("/user/roleList")
    public String userRoleList(@RequestParam("userId") Integer userId) {
        QueryWrapper<AuthUserRoleBean> qw = new QueryWrapper<>();
        qw.eq("user_id", userId);
        List<AuthUserRoleBean> userRoleBeans = userRoleBeanMapper.selectList(qw);
        List<AuthRoleList> l = Lists.newArrayList();
        userRoleBeans.forEach(x -> {
            AuthRoleList bean = new AuthRoleList();
            bean.setId(x.getRoleId());
            AuthRoleBean roleBean = roleBeanMapper.selectById(x.getRoleId());
            bean.setName(roleBean.getName());
            bean.setComment(roleBean.getComment());
            l.add(bean);
        });
        return ResponseUtil.success(l);
    }

    @PostMapping("/user/changeRoleList")
    public String userChangeRoleList(@RequestBody @Valid AuthModifyUserRoleParams params) {
        //删除全部原有角色
        QueryWrapper<AuthUserRoleBean> qw = new QueryWrapper<>();
        qw.eq("user_id", params.getUserId());
        if (userRoleBeanMapper.delete(qw) <= 0) {
            return ResponseUtil.failed();
        }
        //重新写入角色
        List<Integer> roleList = params.getRoleList();
        roleList.forEach(x -> {
            AuthUserRoleBean userRoleBean = new AuthUserRoleBean();
            userRoleBean.setUserId(params.getUserId());
            userRoleBean.setRoleId(x);
            userRoleBean.setCreateTime(new Date());
            userRoleBeanMapper.insert(userRoleBean);
        });
        return ResponseUtil.success();
    }

    @GetMapping("/role/list")
    public String roleList() {
        List<AuthRoleBean> l = roleBeanMapper.selectList(new QueryWrapper<>());
        List<AuthRoleList> ll = GsonUtil.gson.fromJson(GsonUtil.toJson(l), new TypeToken<List<AuthRoleList>>() {
        }.getType());
        return ResponseUtil.success(ll);
    }

    @PostMapping("/role/insert")
    public String roleInsert(@RequestBody @Valid AuthAddRoleParams params) {
        AuthRoleBean bean = new AuthRoleBean();
        bean.setName(params.getName());
        bean.setComment(params.getComment());
        bean.setCreateTime(new Date());
        if (roleBeanMapper.insert(bean) <= 0) {
            return ResponseUtil.failed();
        }
        params.getResourceList().forEach(x -> {
            AuthRoleResourceBean b = new AuthRoleResourceBean();
            b.setRoleId(bean.getId());
            b.setResourceId(x);
            b.setCreateTime(new Date());
            roleResourceBeanMapper.insert(b);
        });
        return ResponseUtil.success();
    }

    @PostMapping("/role/modify")
    public String roleModify(@RequestBody @Valid AuthModifyRoleParams params) {
        AuthRoleBean roleBean = new AuthRoleBean();
        roleBean.setId(params.getRoleId());
        roleBean.setComment(params.getComment());
        if (roleBeanMapper.updateById(roleBean) <= 0) {
            return ResponseUtil.failed();
        }
        if (roleResourceBeanMapper.delete(
                new QueryWrapper<AuthRoleResourceBean>()
                        .eq("role_id", params.getRoleId())) <= 0) {
            return ResponseUtil.failed();
        }
        params.getResourceList().forEach(x -> {
            AuthRoleResourceBean b = new AuthRoleResourceBean();
            b.setRoleId(params.getRoleId());
            b.setResourceId(x);
            b.setCreateTime(new Date());
            roleResourceBeanMapper.insert(b);
        });
        return ResponseUtil.success();
    }

    @GetMapping("/role/disable")
    public String roleDisable(@RequestParam("roleId") Integer roleId) {
        AuthRoleBean roleBean = new AuthRoleBean();
        roleBean.setId(roleId);
        roleBean.setIsValid(0);
        if (roleBeanMapper.updateById(roleBean) <= 0) {
            return ResponseUtil.failed();
        }
        return ResponseUtil.success();
    }

    @GetMapping("/role/active")
    public String roleActive(@RequestParam("roleId") Integer roleId) {
        AuthRoleBean roleBean = new AuthRoleBean();
        roleBean.setId(roleId);
        roleBean.setIsValid(1);
        if (roleBeanMapper.updateById(roleBean) <= 0) {
            return ResponseUtil.failed();
        }
        return ResponseUtil.success();
    }

    @GetMapping("/role/resourceList")
    public String roleResourceList(@RequestParam("roleId") Integer roleId) {
        QueryWrapper<AuthRoleResourceBean> qw = new QueryWrapper<>();
        qw.eq("role_id", roleId);
        List<AuthRoleResourceBean> l = roleResourceBeanMapper.selectList(qw);
        List<AuthResourceList> ll = Lists.newArrayList();
        l.forEach(x -> {
            AuthResourceList bean = new AuthResourceList();
            bean.setId(x.getResourceId());
            AuthResourceBean b = resourceBeanMapper.selectById(x.getResourceId());
            bean.setLevel1(b.getLevel1());
            bean.setLevel2(b.getLevel2());
            bean.setLevel3(b.getLevel3());
            bean.setName(b.getName());
            bean.setComment(b.getComment());
            ll.add(bean);
        });
        return ResponseUtil.success(ll);
    }

    @GetMapping("/resource/list")
    public String ResourceList() {
        List<AuthResourceBean> l = resourceBeanMapper.selectList(new QueryWrapper<>());
        List<AuthResourceList> ll = GsonUtil.gson.fromJson(GsonUtil.toJson(l), new TypeToken<List<AuthResourceList>>() {
        }.getType());
        return ResponseUtil.success(ll);
    }

}
