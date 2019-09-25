package com.sup.core.facade;

import com.sup.common.bean.TbRepayPlanBean;
import com.sup.common.bean.paycenter.RepayInfo;
import com.sup.common.bean.paycenter.vo.RepayVO;
import com.sup.common.param.FunpayCallBackParam;
import com.sup.common.param.LoanCalculatorParam;
import com.sup.common.param.ManualLoanParam;
import com.sup.common.param.ManualRepayParam;
import com.sup.common.util.Result;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Project:uniloan
 * Class:  LoanFacade
 * <p>
 * Author: guanfeng
 * Create: 2019-09-05
 */

@RequestMapping(value = "/loan")
public interface LoanFacade {

    //////////////////////////////
    // 放款接口
    //////////////////////////////

    /**
     * 放款计算器，预估到手金额以及还款总额度
     * @param param
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "calc", produces = "application/json;charset=UTF-8")
    Result calculator(@RequestBody LoanCalculatorParam param);

//    // auto loan
//    @ResponseBody
//    @RequestMapping(value = "autoExec", produces = "application/json;charset=UTF-8")
//    Result autoLoan(String userId, String applyId);
//
//    // add/update/get loan plan
//    @ResponseBody
//    @RequestMapping(value = "plan/add", produces = "application/json;charset=UTF-8")
//    Result addRepayPlan(String userId, String applyId);

    @ResponseBody
    @RequestMapping(value = "plan/update", produces = "application/json;charset=UTF-8")
    Result updateRepayPlan(@RequestBody TbRepayPlanBean bean);

    /**
     *
     * @param applyId
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "plan/get", produces = "application/json;charset=UTF-8")
    Result getRepayPlan(String applyId);

    /**
     * 手动放款
     * @param param
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "manualLoan", produces = "application/json;charset=UTF-8")
    Result manualLoan(@RequestBody ManualLoanParam param);

    /**
     * 手动还款
     * @param param
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "manualRepay", produces = "application/json;charset=UTF-8")
    Result manualRepay(@RequestBody ManualRepayParam param);



    /**
     * 获取支付通道还款所需信息，包括支付码和链接
     * @param repayInfo    还款参数
     * @return  还款所需信息，包括交易码、便利店地址、流水号、交易码过期时间
     */
    @ResponseBody
    @RequestMapping(value = "repayInfo/get", produces = "application/json;charset=UTF-8")
    Result getRepayInfo(@RequestBody RepayInfo repayInfo);

    /**
     * 支付通道放款回调接口
     * @param param
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "payCallBack", produces = "application/json;charset=UTF-8")
    Result payCallBack(@RequestBody FunpayCallBackParam param);


    /**
     * 支付通道还款回调接口
     * @param param
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "repayCallBack", produces = "application/json;charset=UTF-8")
    Result repayCallBack(@RequestBody FunpayCallBackParam param);

}
