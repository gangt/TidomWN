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
import android.text.TextUtils;

import com.lssl.activity.SuneeeApplication;
import com.xiangpu.utils.LogUtil;
import com.xiangpu.utils.SharedPrefUtils;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaArgs;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.PluginResult;
import org.json.JSONException;
import org.json.JSONObject;

import me.leolin.shortcutbadger.ShortcutBadger;

public class UCPCommandPlugin extends CordovaPlugin {

    public static String TAG = "UCPCommandPlugin";
    public CallbackContext mCallbackContext;
    public JSONObject menuParamJson;
    private final String ACTION_SET_APPLICATION_ICON_BADGE_NUMBER = "setApplicationIconBadgeNumber";
    private final String ACTION_GET_ROUTER = "getRouter";

    public boolean execute(String action, final CordovaArgs args, final CallbackContext callbackContext) throws JSONException {
        LogUtil.e(TAG, "action:" + action);
        mCallbackContext = callbackContext;
        final Context context = cordova.getActivity();

        if (action.equals("sendMessage")) { // 传菜单
            String startUrl = args.getString(1);
            //JSONObject extraPrefs = args.getJSONObject(2);
            String strMenu = args.getString(2);
            LogUtil.i(TAG, "startUrl:" + startUrl + "，strMenu:" + strMenu);
//            this.loadUCPMenu(startUrl, strMenu);

            PluginResult dataResult = new PluginResult(PluginResult.Status.OK, strMenu);
            dataResult.setKeepCallback(true);//非常重要
            mCallbackContext.sendPluginResult(dataResult);
            return true;
        } else if (action.equals("getSessionId")) {
            String sessionId = SharedPrefUtils.getSessionId(cordova.getActivity());
            LogUtil.i(TAG, "SessionId send to ucp is:" + sessionId);
            AndroidUcpCommand(sessionId);
            return true;
        } else if (ACTION_SET_APPLICATION_ICON_BADGE_NUMBER.equals(action)) {
            cordova.getThreadPool().execute(new Runnable() {
                @Override
                public void run() {
                    try {
                        JSONObject jsonObject = args.getJSONObject(0);
                        int badgeCount = jsonObject.getInt("badge");
                        LogUtil.i(TAG, "ucp send badgeCount:" + badgeCount);
                        if (badgeCount == 0) {
                            ShortcutBadger.removeCount(context);    //初始化图标小红点为0
                        } else {
                            ShortcutBadger.applyCount(context, badgeCount);
                        }
                        mCallbackContext.success();
                    } catch (JSONException e) {
                        e.printStackTrace();
                        mCallbackContext.error(e.getMessage());
                    }
                }
            });
            return true;
        } else if (ACTION_GET_ROUTER.equals(action)) {

            String router = SharedPrefUtils.getStringData(context, "router", "");
            if (!TextUtils.isEmpty(router)) {
                AndroidUcpCommand(router);
                SharedPrefUtils.saveStringData(context, "router", null);
            }
            return true;
        }
//        else if (action.equals("getSessionId")) {
//            String sessionId = SuneeeApplication.sessionClient.getSessionId(SuneeeApplication.user.userId);//会话id
//            PluginResult dataResult = new PluginResult(PluginResult.Status.OK, sessionId);
//            dataResult.setKeepCallback(true);//非常重要
//            mCallbackContext.sendPluginResult(dataResult);
//            return true; //非常重要
//        }
        else if (action.equals("exitEnterpriseEmail")) {

        }
        return false;
    }

    //http://blog.csdn.net/crazyman2010/article/details/46694925 (js 调原生)
    public void AndroidUcpCommand(String cmd) {  // 发送指挥命令 (原生发给js) json 格式
        if (mCallbackContext != null) {
            PluginResult dataResult = new PluginResult(PluginResult.Status.OK, cmd);
            dataResult.setKeepCallback(true);
            mCallbackContext.sendPluginResult(dataResult);
        } else {
            LogUtil.e(TAG, "UCPCommandPlugin mCallbackContext is null!");
        }
    }

    public void loadUCPMenu(String url, String extraPrefs) throws JSONException {
        menuParamJson = new JSONObject(extraPrefs.toString());
    }

    public JSONObject getLoadMenu() {
        return menuParamJson;
    }

    //andi
    @Override
    public Boolean shouldAllowRequest(String url) {
        return true;
    }

    @Override
    public Boolean shouldAllowBridgeAccess(String url) {
        return true;
    }


}
