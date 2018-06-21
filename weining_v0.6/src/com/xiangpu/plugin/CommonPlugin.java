/*
       Licensed to the Apache Software Foundation (ASF) under one
       or more contributor license agreements.  See the NOTICE file
       distributed with this work for additional information
       regarding copyright ownership.  The ASF licenses this file
       to you under the Apache License, Version 2.0 (the
       "License"); you may not use this file except in compliance
       with the License.  You may obtain a copy of the License at

         http://www.apache.org/licenses/LICENSE-2.0

       Unless required by applicable law or agreed to in writing,
       software distributed under the License is distributed on an
       "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
       KIND, either express or implied.  See the License for the
       specific language governing permissions and limitations
       under the License.
*/
package com.xiangpu.plugin;

import android.content.Context;

import com.lssl.activity.MainActivity;
import com.lssl.activity.SuneeeApplication;
import com.lssl.activity.WLActivity;
import com.xiangpu.activity.BaseActivity;
import com.xiangpu.activity.UcpWebViewActivity;
import com.xiangpu.common.Constants;
import com.xiangpu.utils.LogUtil;
import com.xiangpu.utils.SharedPrefUtils;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaArgs;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.PluginResult;
import org.json.JSONException;

/**
 * 处理注册，返回等普通逻辑的插件
 */
public class CommonPlugin extends CordovaPlugin implements MainActivity.CommonResultCallback {

    public static String TAG = "CommonPlugin";
    public CallbackContext mCallbackContext;
    public static CallbackContext mEnterpriseCodecallback;

    private final String ACTION_GO_TO_LOGIN_PAGE = "goToLoginPage";
    private final String ACTION_GET_CLIENT_ID = "getClientId";
    private final String ACTION_CLOSE_SERVICE_WINDOW = "closeServiceWindow";
    private final String ACTION_GET_EnterpriseCode = "getEnterpriseCode";


    public boolean execute(String action, CordovaArgs args, final CallbackContext callbackContext) throws JSONException {
        LogUtil.e(TAG, "action:" + action);

        mCallbackContext = callbackContext;
        Context context = cordova.getActivity();
        if (ACTION_GO_TO_LOGIN_PAGE.equals(action)) {
            cordova.getActivity().finish();
            mCallbackContext.success();
            return true;
        } else if (ACTION_GET_CLIENT_ID.equals(action)) {
            String clientId = SharedPrefUtils.getStringData(context, Constants.CLIENT_ID, "");
            LogUtil.i(TAG, "----发送的----clientId-----:" + clientId);
            PluginResult dataResult = new PluginResult(PluginResult.Status.OK, clientId);
            dataResult.setKeepCallback(true);//非常重要
            mCallbackContext.sendPluginResult(dataResult);
            return true;
        } else if (ACTION_CLOSE_SERVICE_WINDOW.equals(action)) {
            cordova.getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (cordova.getActivity() instanceof WLActivity) {
                        ((WLActivity) cordova.getActivity()).closeServiceWindow();
                    } else if (cordova.getActivity() instanceof BaseActivity) {
                        ((BaseActivity) cordova.getActivity()).closeServiceWindow();
                    } else if (cordova.getActivity() instanceof UcpWebViewActivity) {
//                        ((UcpWebViewActivity) cordova.getActivity()).closeServiceWindow();
                    }
//                    ServicePopupWindowUtil.getInstance(cordova.getActivity()).closeServicePopupWindow();
                    mCallbackContext.success();
                }
            });
            return true;
        } else if (ACTION_GET_EnterpriseCode.equals(action)) {
            mEnterpriseCodecallback = callbackContext;
            String compCode = SuneeeApplication.getUser().getSelectCompanyId();

            if (mEnterpriseCodecallback != null) {
                PluginResult dataResult = new PluginResult(PluginResult.Status.OK, compCode);
                dataResult.setKeepCallback(true);//非常重要
                mEnterpriseCodecallback.sendPluginResult(dataResult);
            }

            MainActivity.setCommonResultCallback(this);

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


    @Override
    public void onCommonCmd(String cmd) {

        if (mEnterpriseCodecallback != null) {
            PluginResult result = new PluginResult(PluginResult.Status.OK, cmd);
            result.setKeepCallback(true);
            mEnterpriseCodecallback.sendPluginResult(result);
        }
    }
}
