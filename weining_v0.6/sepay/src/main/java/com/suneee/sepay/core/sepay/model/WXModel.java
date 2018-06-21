package com.suneee.sepay.core.sepay.model;

import android.content.Context;

import com.suneee.sepay.core.log.L;
import com.suneee.sepay.core.sepay.bean.SEPayResult;
import com.suneee.sepay.core.sepay.bean.request.WXPayReq;
import com.suneee.sepay.core.sepay.callback.PayCallback;
import com.suneee.sepay.core.sepay.config.WXConfig;
import com.tencent.mm.sdk.constants.Build;
import com.tencent.mm.sdk.modelpay.PayReq;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;

/**
 * 处理微信sdk的接口
 * Created by suneee on 2016/6/24.
 */
public class WXModel {

    private static final String TAG = WXModel.class.getSimpleName();
    private static WXModel instance = new WXModel();
    private IWXAPI wxAPI;
    /**是否初始化过微信，既注册*/
    private boolean hasRegist;


    private WXModel(){}

    public static WXModel getInstance(){
        return instance;
    }
    /**
     * 微信支付只有经过初始化才能成功调起，其他支付渠道无此要求。
     *
     * @param context      需要在某Activity里初始化微信支付，此参数需要传递该Activity.this，不能为null
     * @return             返回出错信息，如果成功则为null
     */
    public String initWechatPay(Context context, String wechatAppID) {
        String errMsg = null;

        if (context == null) {
            errMsg = "Error: initWechatPay里，context参数不能为null.";
            L.e(TAG, errMsg);
            return errMsg;
        }

        if (wechatAppID == null || wechatAppID.length() == 0) {
            errMsg = "Error: initWechatPay里，wx_appid必须为合法的微信AppID.";
            L.e(TAG, errMsg);
            return errMsg;
        }

        // 通过WXAPIFactory工厂，获取IWXAPI的实例
        wxAPI = WXAPIFactory.createWXAPI(context, null);
        try {
            if (isWXPaySupported()) {
                // 将该app注册到微信
                wxAPI.registerApp(wechatAppID);
            } else {
                errMsg = "Error: 安装的微信版本不支持支付.";
                L.e(TAG, errMsg);
            }
        } catch (Exception ignored) {
            errMsg = "Error: 无法注册微信 " + wechatAppID + ". Exception: " + ignored.getMessage();
            L.e(TAG, errMsg);
        }

        if(errMsg == null){
            hasRegist = true;
        }
        return errMsg;
    }

    /**
     * 判断是否注册
     * @return
     */
    public boolean hasRegist(){
        return hasRegist;
    }

    /**
     * 判断微信是否支持支付
     * @return true表示支持
     */
    public  boolean isWXPaySupported() {
        boolean isPaySupported = false;
        if (wxAPI != null) {
            isPaySupported = wxAPI.getWXAppSupportAPI() >= Build.PAY_SUPPORTED_SDK_INT;
        }
        return isPaySupported;
    }

    /**
     * 判断微信客户端是否安装并被支持
     * @return true表示支持
     */
    public boolean isWXAppInstalledAndSupported() {
        boolean isWXAppInstalledAndSupported = false;
        if (wxAPI != null) {
            isWXAppInstalledAndSupported = wxAPI.isWXAppInstalled() && wxAPI.isWXAppSupportAPI();
        }
        return isWXAppInstalledAndSupported;
    }

    public void wxPay(WXPayReq wxPayParam, PayCallback callback){
        //获取到服务器的订单参数后，以下主要代码即可调起微信支付。
        PayReq request = new PayReq();
        request.appId = wxPayParam.appid;
        request.partnerId = wxPayParam.partnerid;
        request.prepayId = wxPayParam.prepayid;
        request.packageValue = wxPayParam.package_value;
        request.nonceStr = wxPayParam.noncestr;
        request.timeStamp = wxPayParam.timestamp;
        request.sign = wxPayParam.sign;

        L.e(TAG, request.toString());
        WXConfig.getInstance().setCallback(callback);

        if (wxAPI != null) {
            boolean result = wxAPI.sendReq(request);
            L.e(TAG, "sendReq result = " + result);
        } else {

            WXConfig.getInstance().getCallback().onResponse(new SEPayResult(SEPayResult.RESULT_FAIL,
                    SEPayResult.APP_INTERNAL_EXCEPTION_ERR_CODE,
                    SEPayResult.FAIL_EXCEPTION,
                    "Error: 微信API为空, 请确认已经在需要调起微信支付的Activity中[成功]调用了BCPay.initWechatPay"));
        }
    }
}
