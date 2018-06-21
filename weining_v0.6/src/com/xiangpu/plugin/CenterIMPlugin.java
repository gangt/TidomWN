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
import com.xiangpu.utils.MD5Encoder;
import com.xiangpu.utils.SharedPrefUtils;
import com.xiangpu.utils.ToastUtils;
import com.xiangpu.utils.Utils;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaArgs;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.PluginResult;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URLEncoder;

public class CenterIMPlugin extends CordovaPlugin {

    static String TAG = "CenterIMPlugin";
    public CallbackContext mCallbackContext;

    public static String m_add_view = "/chat/inner-api/m-add-view";
    public static String m_mian_view = "/chat/inner-api/m-main-view";
    public static String m_history_details = "/chat/inner-api/m-history-details";

    //    public  static String []StrUserId = {"371","386"};
    public String[] StrUserId = null;

    private onGetTeamUserIdsListener onGetTeamUserIdsListener;

    public void setOnGetTeamUserIdsListener(CenterIMPlugin.onGetTeamUserIdsListener onGetTeamUserIdsListener) {
        this.onGetTeamUserIdsListener = onGetTeamUserIdsListener;
    }

    public interface onGetTeamUserIdsListener {
        void onGetTeamUserIds(String[] StrUserId);
    }

    public boolean execute(String action, CordovaArgs args, final CallbackContext callbackContext) throws JSONException {
        LogUtil.e(TAG, TAG + " action:" + action);
        mCallbackContext = callbackContext;

        if (action.equals("start")) {
            PluginResult dataResult = new PluginResult(PluginResult.Status.OK, "");
            dataResult.setKeepCallback(true);
            mCallbackContext.sendPluginResult(dataResult);

            return true;
        } else if (action.equals("sendMessage")) {

            String result = args.getString(1);
            LogUtil.i(TAG, "chat-member-result:" + result);
            JSONObject extraPrefs = new JSONObject(result);
            try {


                String status = extraPrefs.getString("status");
                String msg = extraPrefs.getString("msg");
                if (status.equals("1")) {

                    JSONArray json = extraPrefs.getJSONArray("userIds");

                    StrUserId = new String[json.length()];
                    for (int i = 0; i < json.length(); i++) {
                        int userid = json.getInt(i);
                        StrUserId[i] = String.valueOf(userid);
                    }
                    if (onGetTeamUserIdsListener != null) {
                        onGetTeamUserIdsListener.onGetTeamUserIds(StrUserId);
                    }

                    PluginResult dataResult = new PluginResult(PluginResult.Status.OK, extraPrefs.toString());
                    dataResult.setKeepCallback(true);//非常重要
                    mCallbackContext.sendPluginResult(dataResult);
                    return true;
                } else {
                    ToastUtils.showCenterToast(cordova.getActivity(), msg);
                    if (onGetTeamUserIdsListener != null) {
                        LogUtil.i(TAG, "StrUserId:" + StrUserId);
                        onGetTeamUserIdsListener.onGetTeamUserIds(StrUserId);
                    }
                    PluginResult dataResult = new PluginResult(PluginResult.Status.OK, extraPrefs.toString());
                    dataResult.setKeepCallback(true);//非常重要
                    mCallbackContext.sendPluginResult(dataResult);
                    return true;
                }


            } catch (Exception e) {

            }
        }
        return false;
    }


    //http://blog.csdn.net/crazyman2010/article/details/46694925 (js 调原生)

    public void AndroidIMCommand(String cmd) {  //发送命令 (原生发给js) json 格式
        if (mCallbackContext != null) {
            PluginResult dataResult = new PluginResult(PluginResult.Status.OK, cmd);
            dataResult.setKeepCallback(true);
            mCallbackContext.sendPluginResult(dataResult);
        } else {
            LogUtil.i(TAG, TAG + " mCallbackContext为null");
        }
    }

    public void startActivity(String url) throws JSONException {

        //String payParam = url.substring(5,url.length());

        JSONObject result = new JSONObject(url);

        String strIP = result.getString("IP");
        String strPort = result.getString("ipPort");
        String strUserName = result.getString("username");
        String strPwd = result.getString("pwd");
        String strDevicePort = result.getString("devicePort");

        ///Intent i = new Intent(cordova.getActivity(),ControlRoomActivity.class);
        ///cordova.getActivity().startActivity(i);

        Intent intent = new Intent(cordova.getActivity(), HKCameraActivity.class);


        intent.putExtra("IP", strIP);
        intent.putExtra("ipPort", strPort);
        intent.putExtra("username", strUserName);
        intent.putExtra("pwd", strPwd);
        intent.putExtra("devicePort", strDevicePort);

        cordova.getActivity().startActivity(intent);

        mCallbackContext.success();
    }

//    public static String getCommandCenterImUrl(String outerid, String outername, String action) {
//        try {
//            String sessionId = SharedPrefUtils.getSessionId(SuneeeApplication.getUser().getUserId());//会话id
//            String outerId = outerid;//指挥id
//            String outerName = outername;//"指挥";//指挥名称
//            String timestamp = String.valueOf(Utils.getDateSercond(Utils.getCurTime()));
//
//            String sign = "outerId=" + outerId +
//                    "&outerName=" + outerName +
//                    "&sessionId=" + sessionId +
//                    "&timestamp=" + timestamp +
//                    "&key=192006276b4c092j4rc02edce69f6a2d";
//
//            String signEncoder = MD5Encoder.encode(sign);
//
//            String strCenterIM = SuneeeApplication.getInstance().commandCenterIm + action
//
//                    + "?sessionId=" + sessionId
//                    + "&outerId=" + outerId
//                    + "&outerName=" + URLEncoder.encode(outerName)
//                    + "&timestamp=" + timestamp
//                    + "&sign=" + signEncoder;
//
//            return strCenterIM;
//
//        } catch (Exception e) {
//        }
//
//        return "";
//    }
//
//    public static String getCommandCenterImUrl(String comid, String outerid, String outername, String action) {
//        if (false) {
//            return "http://119.23.25.233:8083/Mxp/userList.html";
//        }
//        try {
//            String enterpriseId = comid;//企业id
//            String sessionId = SuneeeApplication.sessionClient.getSessionId(SuneeeApplication.user.userId);//会话id
//            String outerId = outerid;//指挥id
//            String outerName = outername;//"指挥";//指挥名称
////            String timestamp = String.valueOf(Utils.getDateSercond(Utils.getCurTime()));
//            String timestamp = System.currentTimeMillis() / 1000 + "";
//
//            String sign = "enterpriseId=" + enterpriseId +
//                    "&outerId=" + outerId +
//                    "&outerName=" + outerName +
//                    "&sessionId=" + sessionId +
//                    "&timestamp=" + timestamp +
//                    "&key=192006276b4c092j4rc02edce69f6a2d";
//
//            String signEncoder = MD5Encoder.encode(sign);
//            LogUtil.i(TAG, "勾选成员MD5加密后的签名：" + signEncoder);
//            String strCenterIM = SuneeeApplication.getInstance().commandCenterIm + action
//
//                    + "?sessionId=" + sessionId
//                    + "&enterpriseId=" + enterpriseId
//                    + "&outerId=" + outerId
//                    + "&outerName=" + URLEncoder.encode(outerName)
//                    + "&timestamp=" + timestamp
//                    + "&sign=" + signEncoder;
//
//            return strCenterIM;
//
//        } catch (Exception e) {
//        }
//
//        return "";
//    }

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
