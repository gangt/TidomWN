package com.suneee.sepay.core.sepay.config;

import com.suneee.sepay.core.sepay.bean.SEPayResult;
import com.suneee.sepay.core.sepay.callback.PayCallback;

/**
 * 配置微信支付
 * Created by suneee on 2016/6/28.
 */
public class WXConfig {

    public static String APP_ID = "test";
    private static WXConfig instance;
    private PayCallback callback;

    public PayCallback getCallback() {
        if(callback == null){
            callback = defaultCallback;
        }
        return callback;
    }

    public void setCallback(PayCallback callback) {
        this.callback = callback;
    }

    private WXConfig(){}

    public static WXConfig getInstance(){
        if(instance == null){
            synchronized (WXConfig.class){
                if(instance == null){
                    instance = new WXConfig();
                }
            }
        }
        return instance;
    }


    private static PayCallback defaultCallback = new PayCallback() {
        @Override
        public void onResponse(SEPayResult result) {

        }
    };

}
