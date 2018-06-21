package com.suneee.sepay.core.exception;

/**
 * 支付异常
 * Created by suneee on 2016/6/23.
 */
public class PayTypeException extends Exception{

    public PayTypeException(){
        super();
    }

    public PayTypeException(String errorMsg){
        super(errorMsg);
    }
}
