/**
 *
 * Created by xuanzhui on 2015/7/27.
 * Copyright (c) 2015 BeeCloud. All rights reserved.
 */
package com.suneee.sepay.core;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.suneee.sepay.core.log.L;
import com.suneee.sepay.core.sepay.bean.SEPayResult;
import com.suneee.sepay.core.sepay.callback.PayCallback;
import com.suneee.sepay.core.sepay.config.SEPayConfig;
import com.suneee.sepay.core.sepay.config.WXConfig;
import com.tencent.mm.sdk.modelbase.BaseReq;
import com.tencent.mm.sdk.modelbase.BaseResp;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.sdk.openapi.WXAPIFactory;

/*
 * 微信支付结果接收类
 */
public class SEWechatPaymentActivity extends Activity implements IWXAPIEventHandler {
    private static final String TAG = "SEWechatPaymentActivity";

    private IWXAPI wxAPI;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        L.e(TAG, "into weixin return activity");
        PayCallback callback = WXConfig.getInstance().getCallback();
        try {
            String wxAppId = WXConfig.APP_ID;
            if (wxAppId != null && wxAppId.length() > 0) {
                wxAPI = WXAPIFactory.createWXAPI(this, wxAppId);
                wxAPI.handleIntent(getIntent(), this);
            } else {
                L.e(TAG, "Error: wxAppId 不合法 SEWechatPaymentActivity: " + wxAppId);

                if (callback != null) {
                    callback.onResponse(new SEPayResult(SEPayResult.RESULT_FAIL,
                            SEPayResult.APP_INTERNAL_PARAMS_ERR_CODE,
                            SEPayResult.FAIL_INVALID_PARAMS,
                            "wxAppId 不合法"));
                }

                this.finish();

            }
        } catch (Exception ex) {
            L.e(TAG, ex.getMessage()==null ? "未知错误，初始化回调入口失败" : ex.getMessage());

            if (callback != null) {
                callback.onResponse(
                        new SEPayResult(SEPayResult.RESULT_FAIL,
                            SEPayResult.APP_INTERNAL_EXCEPTION_ERR_CODE,
                            SEPayResult.FAIL_EXCEPTION,
                            "微信回调入口初始化失败"));
            }

            ex.printStackTrace();

            this.finish();
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        try {
            setIntent(intent);
            if (wxAPI != null) {
                wxAPI.handleIntent(intent, this);
            }
        } catch (Exception ex) {
            PayCallback callback = WXConfig.getInstance().getCallback();
            if (callback != null) {
                callback.onResponse(
                        new SEPayResult(SEPayResult.RESULT_FAIL,
                                SEPayResult.APP_INTERNAL_EXCEPTION_ERR_CODE,
                                SEPayResult.FAIL_EXCEPTION,
                                "微信回调入口初始化失败"));
            }

            ex.printStackTrace();

            this.finish();
        }
    }

    /**
     * 微信发送请求到第三方应用时，会回调到该方法
     */
    @Override
    public void onReq(BaseReq baseReq) {

    }

    /**
     * 第三方应用发送到微信的请求处理后的响应结果，会回调到该方法
     */
    @Override
    public void onResp(BaseResp baseResp) {

        L.e(TAG, "onPayFinish, result code = " + baseResp.errCode);

        String result = SEPayResult.RESULT_FAIL;
        int errCode = SEPayResult.APP_INTERNAL_THIRD_CHANNEL_ERR_CODE;
        String errMsg = SEPayResult.FAIL_ERR_FROM_CHANNEL;

        String detailInfo = "";
        switch (baseResp.errCode) {
            case BaseResp.ErrCode.ERR_OK:
                result = SEPayResult.RESULT_SUCCESS;
                errCode = SEPayResult.APP_PAY_SUCC_CODE;
                errMsg = SEPayResult.RESULT_SUCCESS;
                detailInfo += baseResp.errCode ;
                break;
            case BaseResp.ErrCode.ERR_USER_CANCEL:
                result = SEPayResult.RESULT_CANCEL;
                errCode = SEPayResult.APP_PAY_CANCEL_CODE;
                errMsg = SEPayResult.RESULT_CANCEL;
                detailInfo += "用户取消";
                break;
            case BaseResp.ErrCode.ERR_AUTH_DENIED:
                detailInfo += "发送被拒绝";
                break;
            case BaseResp.ErrCode.ERR_COMM:
                detailInfo += "签名错误、未注册APPID、项目设置APPID不正确、注册的APPID与设置的不匹配、其他异常等";
                break;
            case BaseResp.ErrCode.ERR_UNSUPPORT:
                detailInfo += "不支持错误";
                break;
            case BaseResp.ErrCode.ERR_SENT_FAILED:
                detailInfo += "发送失败";
                break;
            default:
                detailInfo += "支付失败";
        }

        PayCallback callback = WXConfig.getInstance().getCallback();
        if (callback != null) {
            callback.onResponse(new SEPayResult(result,
                    errCode,
                    errMsg,
                    detailInfo,
                    SEPayConfig.getInstance().billID));
        }
        this.finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (wxAPI != null)
            wxAPI.detach();
    }
}
