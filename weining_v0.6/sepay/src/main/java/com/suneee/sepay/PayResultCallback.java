package com.suneee.sepay;

/**
 * Created by suneee on 2016/9/8.
 */
public interface PayResultCallback {

    public final String ERROR_RETURN = "error_return";

    void onSuccess();
    void onFaile(String errorMsg);
}
