package com.sup.common.util;

import com.sup.common.bean.OperationStatBean;
import com.sup.common.bean.OperationTaskJoinBean;
import com.sup.common.loan.ApplyStatusEnum;
import com.sup.common.loan.OperationTaskStatusEnum;

import java.util.List;

/**
 * gongshuai
 * <p>
 * 2020/2/5
 */
public class CheckStatUtil {

    public static OperationStatBean processList(List<OperationTaskJoinBean> list) {
        int deny = 0;
        int checked = 0;
        int allocated = 0;
        int loan = 0;
        int loan_amt = 0;
        OperationStatBean operationStatBean = new OperationStatBean();

        for (OperationTaskJoinBean joinBean : list) {
            if (joinBean.getApplyStatus() == ApplyStatusEnum.APPLY_FINAL_DENY.getCode() || joinBean.getApplyStatus() == ApplyStatusEnum.APPLY_FIRST_DENY.getCode()) {

                ++deny;
            }
            if (joinBean.getTaskStatus() == OperationTaskStatusEnum.TASK_STATUS_DONE.getCode()) {
                ++checked;
            }
            if (joinBean.getHasOwner() == 1) {
                ++allocated;
            }
            if (joinBean.getApplyStatus() == ApplyStatusEnum.APPLY_LOAN_SUCC.getCode()) {
                ++loan;
                loan_amt += joinBean.getLoanAmt();
            }
        }
        operationStatBean.setAllocated(allocated);
        operationStatBean.setDenyed(deny);
        operationStatBean.setChecked(checked);
        operationStatBean.setLoan_num(loan);
        operationStatBean.setLoan_amt(loan_amt);
        operationStatBean.setPending(list.size() - allocated);
        operationStatBean.setPassed(checked - deny);
        double pass_rate = checked > 0 ? 0.0f : (checked - deny + 0.001f) / (checked + 0.001f);
        operationStatBean.setPass_rate(pass_rate);
        double  loan_rate = checked >0 ? 0.0 : (loan+0.001f)/(checked+0.001f);
        operationStatBean.setLoan_rate(loan_rate);
        return operationStatBean;

    }

}
