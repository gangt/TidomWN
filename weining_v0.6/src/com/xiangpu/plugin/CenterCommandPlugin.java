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

import com.hcnetsdk.activity.HKCameraActivity;
import com.lssl.activity.SuneeeApplication;
import com.xiangpu.utils.LogUtil;
import com.xiangpu.utils.SharedPrefUtils;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaArgs;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.PluginResult;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * 指挥大厅与H5交互的插件
 * 1,一进去指挥大厅就传compId和userId给H5
 * 2，在指挥大厅切换指挥时，传compId，userId，command给H5
 */
public class CenterCommandPlugin extends CordovaPlugin {

    public static String TAG = "CenterCommandPlugin";
    public CallbackContext mCallbackContext;

    private final String ACTION_GET_COMMAND_ID = "getCommandId";

    public boolean execute(String action, CordovaArgs args, final CallbackContext callbackContext) throws JSONException {
        LogUtil.e(TAG, "action:" + action);

        mCallbackContext = callbackContext;
        if (action.equals("start")) {
            String compId = SuneeeApplication.user.compId;
            String userId = SharedPrefUtils.getUserId(cordova.getActivity());

            JSONObject json = new JSONObject();
            json.put("compId", compId);
            json.put("userId", userId);
//            json.put("commandId","1");

            PluginResult dataResult = new PluginResult(PluginResult.Status.OK, json.toString());
            dataResult.setKeepCallback(true);//非常重要
            mCallbackContext.sendPluginResult(dataResult);

            return true;
        } else if (action.equals("center_video")) {
            String startUrl = args.getString(1);
            this.startActivity(startUrl);
            //Intent intent = new Intent(cordova.getActivity(),HKCameraActivity.class);
            //cordova.getActivity().startActivity(intent);
            //mCallbackContext.success();
        }
//        else if(action.equals("center_control")){//指挥中心
//            Intent i = new Intent(cordova.getActivity(),ControlRoomActivity.class);
//            cordova.getActivity().startActivity(i);
//
//            mCallbackContext.success();
//
//        }
        else if (action.equals("AndroidChangeCommand")) {
            LogUtil.i(TAG, "JS叫我传compId和userId给他了！");
            String CompId = SuneeeApplication.user.compId;
            String UserId = SharedPrefUtils.getUserId(cordova.getActivity());

            JSONObject json = new JSONObject();
            json.put("compId", CompId);
            json.put("userId", UserId);

            PluginResult dataResult = new PluginResult(PluginResult.Status.OK, json);
            dataResult.setKeepCallback(true);//非常重要
            mCallbackContext.sendPluginResult(dataResult);

            return true; //非常重要
        } else if (action.equals("getCommandInfoInControlRoom")) {
            String CompId = SuneeeApplication.user.compId;
            String UserId = SharedPrefUtils.getUserId(cordova.getActivity());
            String commandId = SuneeeApplication.user.curCommand.commandid;

            JSONObject json = new JSONObject();
            json.put("compId", CompId);
            json.put("userId", UserId);
            json.put("commandId", commandId);

            PluginResult dataResult = new PluginResult(PluginResult.Status.OK, json.toString());
            dataResult.setKeepCallback(true);//非常重要
            mCallbackContext.sendPluginResult(dataResult);
            return true;
        } else if (ACTION_GET_COMMAND_ID.equals(action)) {
//            String tagPage = args.getString(0);
//            if ("CommandHistory".equals(tagPage)) {
//                String selectCommandId = ((CommandHistoryActivity) cordova.getActivity()).selectCommandId;
//                CommandHistoryActivity mActivity = (CommandHistoryActivity) cordova.getActivity();
//                mActivity.setSendDataToJSCallBack(new CommandHistoryActivity.SendDataToJSCallBack() {
//                    @Override
//                    public void sendDataToJS(String commandId) {
//                        LogUtil.i(TAG,"历史页面回调回来的指挥ID："+commandId);
//                        try {
//                            JSONObject json = new JSONObject();
//                            json.put("compId", SuneeeApplication.user.compId);
//                            json.put("userId", SuneeeApplication.user.userId);
//                            json.put("commandId", selectCommandId);
//                            PluginResult dataResult = new PluginResult(PluginResult.Status.OK, json.toString());
//                            dataResult.setKeepCallback(true);//非常重要
//                            mCallbackContext.sendPluginResult(dataResult);
//                        } catch (JSONException e) {
//                            e.printStackTrace();
//                            mCallbackContext.sendPluginResult(new PluginResult(PluginResult.Status.ERROR));
//                        }
//                    }
//                });
//            }
//            return true;
        } else if (action.equals("AndroidChangeHistory")) {

            String CompId = "1";//SuneeeApplication.user.compId;
            String UserId = "43855";//SuneeeApplication.user.userId;
            String commandId = "07CBCB9B-F942-417D-8BA9-60B609B162F0";//SuneeeApplication.user.hisCommand.commandid;

            JSONObject json = new JSONObject();
            json.put("compId", CompId);
            json.put("userId", UserId);
            json.put("commandId", commandId);

            PluginResult dataResult = new PluginResult(PluginResult.Status.OK, json.toString());
            dataResult.setKeepCallback(true);//非常重要
            mCallbackContext.sendPluginResult(dataResult);
            return true;
        }

        return false;
    }


    //http://blog.csdn.net/crazyman2010/article/details/46694925 (js 调原生)

    public void AndroidChangeCommand(String cmd) {  //发送指挥命令 (原生发给js) json 格式
        if (mCallbackContext != null) {
            LogUtil.i(TAG, "切换指挥:" + cmd);
            PluginResult dataResult = new PluginResult(PluginResult.Status.OK, cmd);
            dataResult.setKeepCallback(true);
            mCallbackContext.sendPluginResult(dataResult);
        } else {
            LogUtil.i(TAG, "切换指挥-mCallbackContext为null");
        }
    }

    public void startActivity(String url) throws JSONException {

        JSONObject result = new JSONObject(url);

        LogUtil.i(TAG, "-----startActivity:" + result);

        String strIP = result.getString("IP");
        String strPort = result.getString("ipPort");
        String strUserName = result.getString("username");
        String strPwd = result.getString("pwd");
        String strDevicePort = result.getString("devicePort");
        String name = result.getString("name");

        Intent intent = new Intent(cordova.getActivity(), HKCameraActivity.class);
        intent.putExtra("IP", strIP);
        intent.putExtra("ipPort", strPort);
        intent.putExtra("username", strUserName);
        intent.putExtra("pwd", strPwd);
        intent.putExtra("devicePort", strDevicePort);
        intent.putExtra("name", name);

        cordova.getActivity().startActivity(intent);

        mCallbackContext.success();
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
