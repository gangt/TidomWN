package com.xiangpu.plugin;

import android.app.Activity;
import android.content.Intent;

import com.konecty.rocket.chat.AuthenticationActivity;
import com.konecty.rocket.chat.R;
import com.lssl.activity.SuneeeApplication;
import com.xiangpu.activity.UcpWebViewActivity;
import com.xiangpu.activity.usercenter.PersonCenterActivity;
import com.xiangpu.common.Constants;
import com.xiangpu.utils.LogUtil;
import com.xiangpu.utils.LoginUtils;
import com.xiangpu.utils.SharedPrefUtils;
import com.xiangpu.utils.ToastUtils;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaArgs;
import org.apache.cordova.CordovaPlugin;
import org.json.JSONException;

/**
 * Created by Administrator on 2017/12/6 0006.
 * Info：
 */

public class ChangePasswordPlugin extends CordovaPlugin {
    private CallbackContext callbackContext = null;
    private Activity activity = null;

    @Override
    public boolean execute(String action, CordovaArgs args, CallbackContext callbackContext) throws JSONException {
        this.callbackContext = callbackContext;
        this.activity = cordova.getActivity();
        LogUtil.d("action: " + action);
        if (action.equals("confirmChange")) {
            // 判断是sessionId失效还是调用退出
            String logoutType = args.getString(1);
            if (logoutType.equals("lose")) {
                // sessionId失效
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ToastUtils.showCenterToast(activity, activity.getString(R.string.user_had_logout));
                    }
                });
            }
            LoginUtils.logoutSessionIdIsInvalid(activity);
            return true;
        }
        return false;
    }

    @Override
    public Boolean shouldAllowRequest(String url) {
        return true;
    }

    @Override
    public Boolean shouldAllowBridgeAccess(String url) {
        return true;
    }

}
