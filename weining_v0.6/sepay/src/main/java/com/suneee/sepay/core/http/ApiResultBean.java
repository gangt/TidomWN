package com.suneee.sepay.core.http;

/**
 *接口返回结果的标准对象
 */
public class ApiResultBean<T> {
    public int code=-1;
    public String message;
    public T data;
}
