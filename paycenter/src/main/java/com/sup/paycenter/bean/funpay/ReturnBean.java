package com.sup.paycenter.bean.funpay;

import lombok.Data;

/**
 * @Author: kouichi
 * @Date: 2019/9/11 22:10
 */
@Data
public class ReturnBean<T> {
    private int code;
    private String msg;
    private T result;
}
