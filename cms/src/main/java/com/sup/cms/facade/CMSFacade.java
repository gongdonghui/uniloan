package com.sup.cms.facade;

import com.sup.cms.bean.CMSDepartmentBean;
import com.sup.cms.bean.CMSLogInfoBean;
import com.sup.cms.bean.CMSRoleBean;
import com.sup.cms.bean.CMSUserBean;
import com.sup.core.bean.ApplyInfoBean;
import com.sup.core.bean.CreditClassBean;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Project:uniloan
 * Class:  CMSFacade
 * <p>
 * Author: guanfeng
 * Create: 2019-09-09
 */

@RequestMapping(value = "/cms")
public interface CMSFacade {

    ////////////////////////////////////////////////////////////
    // 进件管理接口
    ////////////////////////////////////////////////////////////

    // apply info auto assigned
    @ResponseBody
    @RequestMapping(value = "auditTask/autoAssign", produces = "application/json;charset=UTF-8")
    Object autoAuditAssign(@RequestBody ApplyInfoBean bean);

    // apply info manual assigned
    @ResponseBody
    @RequestMapping(value = "auditTask/assign", produces = "application/json;charset=UTF-8")
    Object auditAssign(String assignTo, String operator, String applyId);

    // apply info cancel assignment
    @ResponseBody
    @RequestMapping(value = "auditTask/cancelAssign", produces = "application/json;charset=UTF-8")
    Object cancelAuditAssign(String assignTo, String operator, String applyId);

    // audit apply
    @ResponseBody
    @RequestMapping(value = "auditTask/exec", produces = "application/json;charset=UTF-8")
    Object updateApplyInfo(String operator, @RequestBody ApplyInfoBean bean);


    ////////////////////////////////////////////////////////////
    // 逾期管理接口
    ////////////////////////////////////////////////////////////

    // get overdue loan info
    @ResponseBody
    @RequestMapping(value = "overdueInfo/get", produces = "application/json;charset=UTF-8")
    Object getOverdueInfo(Integer page, Integer pageSize);

    // overdue loan info manual assigned
    @ResponseBody
    @RequestMapping(value = "overdueTask/assign", produces = "application/json;charset=UTF-8")
    Object overdueAssign(String assignTo, String operator, String applyId);

    // overdue loan info cancel assignment
    @ResponseBody
    @RequestMapping(value = "overdueTask/cancelAssign", produces = "application/json;charset=UTF-8")
    Object cancelOverdueAssign(String assignTo, String operator, String applyId);



    ////////////////////////////////////////////////////////////
    // 部门管理接口
    ////////////////////////////////////////////////////////////
    @ResponseBody
    @RequestMapping(value = "dep/add", produces = "application/json;charset=UTF-8")
    Object addDepartment(@RequestBody CMSDepartmentBean bean);

    @ResponseBody
    @RequestMapping(value = "dep/update", produces = "application/json;charset=UTF-8")
    Object updateDepartment(@RequestBody CMSDepartmentBean bean);

    // 参数为空返回所有
    @ResponseBody
    @RequestMapping(value = "dep/get", produces = "application/json;charset=UTF-8")
    Object getDepartmentList(String cmsDepartmentId);

    

    ////////////////////////////////////////////////////////////
    // 人员管理接口
    ////////////////////////////////////////////////////////////
    @ResponseBody
    @RequestMapping(value = "user/add", produces = "application/json;charset=UTF-8")
    Object addUser(@RequestBody CMSUserBean bean);

    @ResponseBody
    @RequestMapping(value = "user/update", produces = "application/json;charset=UTF-8")
    Object updateUser(@RequestBody CMSUserBean bean);

    // 参数为空返回所有
    @ResponseBody
    @RequestMapping(value = "user/get", produces = "application/json;charset=UTF-8")
    Object getUserList(String cmsUserId);



    ////////////////////////////////////////////////////////////
    // 权限管理接口
    ////////////////////////////////////////////////////////////
    @ResponseBody
    @RequestMapping(value = "role/add", produces = "application/json;charset=UTF-8")
    Object addRole(@RequestBody CMSRoleBean bean);

    @ResponseBody
    @RequestMapping(value = "role/update", produces = "application/json;charset=UTF-8")
    Object updateRole(@RequestBody CMSRoleBean bean);

    @ResponseBody
    @RequestMapping(value = "role/get", produces = "application/json;charset=UTF-8")
    Object getRole(String cmsUserId);



    ////////////////////////////////////////////////////////////
    // 报表接口
    ////////////////////////////////////////////////////////////

    /**
     *
     * @param date_from 报表开始日期，格式：YYYY-mm-DD
     * @param date_to   报表结束日期，格式：YYYY-mm-DD
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "report/get", produces = "application/json;charset=UTF-8")
    Object getDailyReport(String date_from, String date_to);



    ////////////////////////////////////////////////////////////
    // 操作日志接口
    ////////////////////////////////////////////////////////////
    @ResponseBody
    @RequestMapping(value = "log/add", produces = "application/json;charset=UTF-8")
    Object addLog(@RequestBody CMSLogInfoBean bean);

    /**
     *
     * @param date_from 日志开始日期，格式：YYYY-mm-DD
     * @param date_to   日志结束日期，格式：YYYY-mm-DD
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "log/get", produces = "application/json;charset=UTF-8")
    Object getLog(String date_from, String date_to);

    ////////////////////////////////////////////////////////////
    // 用户信用等级接口
    ////////////////////////////////////////////////////////////
    // update/get user core class
    @ResponseBody
    @RequestMapping(value = "creditClass/add", produces = "application/json;charset=UTF-8")
    Object addCreditClass(@RequestBody CreditClassBean bean);

    @ResponseBody
    @RequestMapping(value = "creditClass/update", produces = "application/json;charset=UTF-8")
    Object updateCreditClass(@RequestBody CreditClassBean bean);

    // TODO: 产品配置

}
