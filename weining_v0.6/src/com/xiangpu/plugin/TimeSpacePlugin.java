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

import android.content.Intent;

import com.xiangpu.activity.AudioSearchActivity;
import com.xiangpu.utils.LogUtil;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaArgs;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.PluginResult;
import org.json.JSONException;
import org.json.JSONObject;

public class TimeSpacePlugin extends CordovaPlugin {

    public static String TAG = "TimeSpacePlugin";
    public CallbackContext mCallbackContext;

    public OnVoiceButtonClickListener listener;

    public OnVoiceButtonClickListener getListener() {
        return listener;
    }

    public void setListener(OnVoiceButtonClickListener listener) {
        this.listener = listener;
    }

    public interface OnVoiceButtonClickListener {
        public void voiceButtonClick();
    }

    public boolean execute(String action, CordovaArgs args, final CallbackContext callbackContext) throws JSONException {
        LogUtil.e(TAG, "action:" + action);
        mCallbackContext = callbackContext;

        if (action.equals("sendMessage")) {
//            String startUrl = args.getString(1);
//            JSONObject extraPrefs = args.getJSONObject(2);
//            this.startActivity(startUrl, extraPrefs);
//            startActivity("", null);
            if (listener != null) {
                listener.voiceButtonClick();
            }
            PluginResult dataResult = new PluginResult(PluginResult.Status.OK, "");
            dataResult.setKeepCallback(true);
            mCallbackContext.sendPluginResult(dataResult);

            return true;
        }else if(action.equals("closeAudio")){
            PluginResult dataResult = new PluginResult(PluginResult.Status.OK, "");
            dataResult.setKeepCallback(true);
            mCallbackContext.sendPluginResult(dataResult);
            return true;
        }
        return false;
    }

    public void AndroidCommand(String cmd) {  // 发送指挥命令 (原生发给js) json 格式
        if (mCallbackContext != null) {
            PluginResult dataResult = new PluginResult(PluginResult.Status.OK, cmd);
            dataResult.setKeepCallback(true);
            mCallbackContext.sendPluginResult(dataResult);
        }
    }

    public void startActivity(String url, JSONObject extraPrefs) throws JSONException {
        {
            Intent intent = new Intent(this.cordova.getActivity(), AudioSearchActivity.class);
            cordova.getActivity().startActivity(intent);
        }
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
