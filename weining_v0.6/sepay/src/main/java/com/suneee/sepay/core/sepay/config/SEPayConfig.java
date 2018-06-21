package com.suneee.sepay.core.sepay.config;

/**
 * 配置一些全局参数
 * 在使用支付前配置
 * 可配置的包括setHttpConnectTimeout、debug
 * Created by yiw on 2016/6/22.
 */
public class SEPayConfig {

    /**
     * 暂存每次支付结束后的返回的订单唯一标识
     */
    public String billID;

    /**
     * SEPay Android SDK 版本
     */
    public static final String SEPAY_ANDROID_SDK_VERSION = "1.0.1";

    private static SEPayConfig instance;

    private SEPayConfig(){}
    public static SEPayConfig getInstance(){

        if(instance == null){
            synchronized (SEPayConfig.class){
                if(instance == null){
                    instance = new SEPayConfig();
                }
            }
        }
        return instance;
    }


    private long http_connect_timeout ;

    public void setHttpConnectTimeout(long connectTimeout){
        http_connect_timeout = connectTimeout;
    }

    public long getHttpConnectTimeout(){
        return http_connect_timeout;
    }


    private String debugTag = "debug";
    private boolean isDebug = true;

    public String getDebugTag() {
        return debugTag;
    }

    public boolean isDebug() {
        return isDebug;
    }

    public void setDebug(String debugTag, boolean isDebug){
        this.debugTag = debugTag;
        this.isDebug = isDebug;
    }

    private boolean isTest = false;
    public boolean isTest(){
        return this.isTest;
    }
    public void setTest(boolean isTest){
        this.isTest = isTest;
    }

}
