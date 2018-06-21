package com.suneee.sepay.core.http;

/**
 *返回结果回调
 */
public interface ApiCallback<ApiResultBean> {

    void onSuccess(ApiResultBean resultBean);

    void onFaile(Throwable e);

}
