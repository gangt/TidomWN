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
import android.util.Log;

import com.lssl.activity.SuneeeApplication;
import com.xiangpu.activity.DownloaderActivity;
import com.xiangpu.utils.LogUtil;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaArgs;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.PluginResult;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * description: 附件下载插件
 * autour: Andy
 * date: 2017/12/11 16:56
 * update: 2017/12/11
 * version: 1.0
 */
public class OpenUrlPlugin extends CordovaPlugin {

    public static String TAG = "OpenUrlPlugin";
    public CallbackContext mCallbackContext;

    // 处理下载页面启动多次的问题（只有当activity没有存在并且不在启动过程中时，执行打开页面操作）
    public static boolean isActivityOpened = false;

    public boolean execute(String action, CordovaArgs args, final CallbackContext callbackContext) throws JSONException {
        LogUtil.e(TAG, "action:" + action);
        mCallbackContext = callbackContext;

        if (action.equals("sendUrl")) { // 传菜单
            //String startUrl = args.getString(1);

            //todo 获取到下载数据对象之后进行解析，跳转到下载页面进行下载
            // todo {"name":"文件名", "size":"文件大小", "url":"下载地址"}
            // {"name":"smart_jni.txt","href":"","size":267,"content_type":"text/plain"}
            String fileInfo = args.getString(2);
            Log.e("fileInfo", fileInfo);
            JSONObject jsonObject = new JSONObject(fileInfo);
            String name = jsonObject.getString("name");
            String url = jsonObject.getString("href");
            String size = jsonObject.getString("size");
            String type = jsonObject.getString("content_type");
            startActivity(name, url, size, type);

            PluginResult dataResult = new PluginResult(PluginResult.Status.OK, "success");
            dataResult.setKeepCallback(true);//非常重要
            mCallbackContext.sendPluginResult(dataResult);
            return true;
        }
        return false;
    }

    private void startActivity(String name, String url, String size, String type) {
        if (isActivityOpened || SuneeeApplication.getInstance().isActivityExists(DownloaderActivity.class)) {
            return;
        }
        Intent intent = new Intent(cordova.getActivity(), DownloaderActivity.class);
        intent.putExtra("name", name);
        intent.putExtra("url", url);
        intent.putExtra("size", size);
        intent.putExtra("type", type);
        cordova.getActivity().startActivity(intent);

        isActivityOpened = true;

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
