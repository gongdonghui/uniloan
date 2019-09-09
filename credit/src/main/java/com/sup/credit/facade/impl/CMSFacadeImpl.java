package com.sup.credit.facade.impl;

import com.sup.credit.bean.ApplyInfoBean;
import com.sup.credit.bean.cms.CMSDepartmentBean;
import com.sup.credit.bean.cms.CMSLogInfoBean;
import com.sup.credit.bean.cms.CMSRoleBean;
import com.sup.credit.bean.cms.CMSUserBean;
import com.sup.credit.facade.CMSFacade;
import org.springframework.web.bind.annotation.RestController;

/**
 * Project:uniloan
 * Class:  CMSFacadeImpl
 * <p>
 * Author: guanfeng
 * Create: 2019-09-09
 */

@RestController
public class CMSFacadeImpl implements CMSFacade {
    @Override
    public Object autoAuditAssign(ApplyInfoBean bean) {
        return null;
    }

    @Override
    public Object auditAssign(String assignTo, String operator, String applyId) {
        return null;
    }

    @Override
    public Object cancelAuditAssign(String assignTo, String operator, String applyId) {
        return null;
    }

    @Override
    public Object getOverdueInfo(Integer page, Integer pageSize) {
        return null;
    }

    @Override
    public Object overdueAssign(String assignTo, String operator, String applyId) {
        return null;
    }

    @Override
    public Object cancelOverdueAssign(String assignTo, String operator, String applyId) {
        return null;
    }

    @Override
    public Object addDepartment(CMSDepartmentBean bean) {
        return null;
    }

    @Override
    public Object updateDepartment(CMSDepartmentBean bean) {
        return null;
    }

    @Override
    public Object getDepartmentList(String cmsDepartmentId) {
        return null;
    }

    @Override
    public Object addUser(CMSUserBean bean) {
        return null;
    }

    @Override
    public Object updateUser(CMSUserBean bean) {
        return null;
    }

    @Override
    public Object getUserList(String cmsUserId) {
        return null;
    }

    @Override
    public Object addRole(CMSRoleBean bean) {
        return null;
    }

    @Override
    public Object updateRole(CMSRoleBean bean) {
        return null;
    }

    @Override
    public Object getRole(String cmsUserId) {
        return null;
    }

    /**
     * @param date_from 报表开始日期，格式：YYYY-mm-DD
     * @param date_to   报表结束日期，格式：YYYY-mm-DD
     * @return
     */
    @Override
    public Object getDailyReport(String date_from, String date_to) {
        return null;
    }

    @Override
    public Object addLog(CMSLogInfoBean bean) {
        return null;
    }

    /**
     * @param date_from 日志开始日期，格式：YYYY-mm-DD
     * @param date_to   日志结束日期，格式：YYYY-mm-DD
     * @return
     */
    @Override
    public Object getLog(String date_from, String date_to) {
        return null;
    }
}
