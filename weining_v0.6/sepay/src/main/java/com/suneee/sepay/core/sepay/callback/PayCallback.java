package com.suneee.sepay.core.sepay.callback;

import com.suneee.sepay.core.sepay.bean.SEPayResult;

/**
 * 支付完后的结果回调
 * Created by suneee on 2016/6/24.
 */
public interface PayCallback {
    public void onResponse(SEPayResult result);
}
