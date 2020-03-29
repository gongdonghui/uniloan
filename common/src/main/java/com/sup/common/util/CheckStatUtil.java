package com.sup.common.util;

import com.sup.common.bean.OperationStatBean;
import com.sup.common.bean.OperationTaskJoinBean;
import com.sup.common.bean.OperatorInfoBean;
import com.sup.common.loan.ApplyStatusEnum;
import com.sup.common.loan.OperationTaskStatusEnum;
import com.sup.common.loan.OperationTaskTypeEnum;

import java.util.List;

/**
 * gongshuai
 * <p>
 * 2020/2/5
 */
public class CheckStatUtil {

    public static OperationStatBean processList(List<OperationTaskJoinBean> list, Integer taskType) {
        int deny = 0;
        int checked = 0;
        int allocated = 0;
        int loan = 0;
        int loan_amt = 0;
        int closed = 0;
        OperationStatBean operationStatBean = new OperationStatBean();

        for (OperationTaskJoinBean joinBean : list) {
            checked = allocated  =  list.size();
            if (joinBean != null) {
                if (taskType == OperationTaskTypeEnum.TASK_FIRST_AUDIT.getCode() && joinBean.getApplyStatus() == ApplyStatusEnum.APPLY_FIRST_DENY.getCode()) {
                    ++deny;

                }
                if (taskType == OperationTaskTypeEnum.TASK_FINAL_AUDIT.getCode() && joinBean.getApplyStatus() == ApplyStatusEnum.APPLY_FINAL_DENY.getCode()) {
                    ++deny;
                }
                if (joinBean.getApplyStatus() == ApplyStatusEnum.APPLY_LOAN_SUCC.getCode()) {
                    ++loan;
                    loan_amt += joinBean.getLoanAmt();
                }
                if (joinBean.getApplyStatus() == ApplyStatusEnum.APPLY_CANCEL.getCode()) {
                    ++closed;
                }
            }
        }
        operationStatBean.setAllocated(allocated);
        operationStatBean.setDenyed(deny);
        operationStatBean.setChecked(checked);
        operationStatBean.setLoan_num(loan);
        operationStatBean.setLoan_amt(loan_amt);
        operationStatBean.setPending(list.size() - allocated);
        int passed = checked - deny - closed >= 0 ? checked - deny - closed : 0;
        operationStatBean.setPassed(passed);
        double pass_rate = checked > 0 && passed > 0 ? (passed + 0.001f) / (checked + 0.001f) : 0.0f;
        operationStatBean.setPass_rate(pass_rate);
        double loan_rate = checked > 0 && loan > 0 ? (loan + 0.001f) / (checked + 0.001f) : 0.0f;
        operationStatBean.setLoan_rate(loan_rate);
        return operationStatBean;

    }


}
