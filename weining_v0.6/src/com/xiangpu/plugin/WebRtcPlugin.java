package com.xiangpu.plugin;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.xiangpu.activity.ViedoGroupInviteActivity;
import com.xiangpu.activity.ViedoInviteActivity;
import com.xiangpu.bean.ChatUserInfo;
import com.xiangpu.utils.SystemManagerUtils;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaArgs;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.PluginResult;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import io.agora.openvcall.model.ConstantApp;
import io.agora.openvcall.ui.ChatActivity;
import io.agora.openvcall.ui.ChatActivity.ChatResultCallback;
import io.agora.openvcall.ui.ChatAudioActivity;
import io.agora.openvcall.ui.ChatAudioActivity.ChatAuidoResultCallback;
import io.agora.openvcall.ui.ChatAudioMulteActivity;

public class WebRtcPlugin extends CordovaPlugin implements ViedoInviteActivity.InviteResultCallback, //单人邀请
        ViedoGroupInviteActivity.GroupInviteResultCallback,//多人邀请
        ChatResultCallback,//单人视频聊天，多人视频聊天
        ChatAuidoResultCallback,//单人音频聊天
        ChatAudioMulteActivity.ChatAuidoMulteResultCallback//多人音频聊天
{

    static String TAG = "WebRtcPlugin";
    public CallbackContext mInviteCallbackContext = null;//邀请
    public CallbackContext mChatCallbackContext = null;//视频
    public static CallbackContext messagecallback;

    public ArrayList<ChatUserInfo> userInfoList = new ArrayList<ChatUserInfo>();

    public boolean execute(String action, CordovaArgs args, final CallbackContext callbackContext) throws JSONException {

        if (action.equals("start")) {

            //String className = args.isNull(0) ? MainDroidGapActivity.class.getCanonicalName() : args.getString(0);
            String startUrl = args.getString(1);
            JSONObject extraPrefs = args.getJSONObject(2);

            // Log.i("-----------------execute2", "startUrl ： " + " : " + startUrl + "action: " + action + "extraPrefs:" + extraPrefs);


            this.startActivity(callbackContext, startUrl, extraPrefs);

            return true;
        } else if (action.equals("messagecallback")) {
            messagecallback = callbackContext;

            if (messagecallback != null) {
                PluginResult result = new PluginResult(PluginResult.Status.OK, "messagecallback");
                result.setKeepCallback(true);
                messagecallback.sendPluginResult(result);
            }

            ViedoInviteActivity.setInviteResultCallback(this);
            ViedoGroupInviteActivity.setGroupInviteResultCallback(this);

//            Log.i("-----------------execute1", "messagecallback ： " + " : " + callbackContext + "action: " + action + "args:" + args);

            return true;
        }

        return false;
    }

    public void startActivity(CallbackContext callbackContext, String url, JSONObject extraPrefs) throws JSONException {

        if (url.contains("webRtc")) {

            String webrtcParam = url.substring(6, url.length());

            JSONObject result = new JSONObject(webrtcParam);

            String created_roomid = result.getString("created_roomid");
            String video_type = result.getString("av_type");
            String room_name = result.getString("room_name");


            userInfoList.clear();

            JSONArray jUserList = result.getJSONArray("userlist");
            for (int i = 0; i < jUserList.length(); i++) {
                JSONObject JsonUser = new JSONObject(jUserList.get(i).toString());
                String name = JsonUser.getString("name");
                String avatar = JsonUser.getString("avatar");
                String userId = JsonUser.getString("userId");

                ChatUserInfo user = new ChatUserInfo();
                user.name = name;
                user.avatar = avatar;
                user.userId = userId;
                userInfoList.add(user);
            }

            String selfuserId = "";

            if (jUserList.length() > 1) {

                int selfindex = result.getInt("selfindex");//指定成员列表，自己所在的位置。方便后续挂断用到自己的信息。
                ChatUserInfo user = userInfoList.get(selfindex);
                selfuserId = user.userId;

            } else {
                selfuserId = result.getString("userId");
            }
            /**
             * 类型判断
             */
            if (video_type.equals("3") || video_type.equals("4")) {//单人邀请页面
                mInviteCallbackContext = callbackContext;

                //调起邀请界面
                ViedoInviteActivity.setInviteResultCallback(this);
                Intent i = new Intent(cordova.getActivity(), ViedoInviteActivity.class);
                i.putExtra(ConstantApp.ACTION_KEY_CHANNEL_NAME, created_roomid);
                i.putExtra(ConstantApp.ACTION_KEY_ENCRYPTION_KEY, "");
                i.putExtra(ConstantApp.ACTION_KEY_ENCRYPTION_MODE, "AES-128-XTS");
                i.putExtra(ConstantApp.ACTION_KEY_VIDEO_TYPE, video_type);

                ChatUserInfo user = userInfoList.get(0);//默认第1个为发起者

                i.putExtra("avatar", user.avatar);
                i.putExtra("name", user.name);
                i.putExtra("userId", selfuserId);

                cordova.getActivity().startActivity(i);
            } else if (video_type.equals("5")) {//多人邀请页面
                mInviteCallbackContext = callbackContext;

                //调起邀请界面
                ViedoGroupInviteActivity.setGroupInviteResultCallback(this);

                Intent i = new Intent(cordova.getActivity(), ViedoGroupInviteActivity.class);
                i.putExtra(ConstantApp.ACTION_KEY_CHANNEL_NAME, created_roomid);
                i.putExtra(ConstantApp.ACTION_KEY_ENCRYPTION_KEY, "");
                i.putExtra(ConstantApp.ACTION_KEY_ENCRYPTION_MODE, "AES-128-XTS");
                i.putExtra(ConstantApp.ACTION_KEY_VIDEO_TYPE, video_type);

                Bundle bundle = new Bundle();
                bundle.putParcelableArrayList("chatList", userInfoList);
                i.putExtras(bundle);
                i.putExtra("userId", selfuserId);

                cordova.getActivity().startActivity(i);

            } else if (video_type.equals("6")) {//拒绝多人聊天
                Intent intent = new Intent();
                intent.setAction("updataUserList");
                Bundle bundle = new Bundle();
                bundle.putParcelableArrayList("chatList", userInfoList);
                intent.putExtras(bundle);

                cordova.getActivity().sendBroadcast(intent);
            } else if (video_type.equals("2")) {//挂断

                if ((mInviteCallbackContext != null || messagecallback != null) && mChatCallbackContext == null) {
                    Intent intent = new Intent();
                    intent.setAction("closeInvitationWnd");
                    cordova.getActivity().sendBroadcast(intent);
                    mInviteCallbackContext = null;
                    //messagecallback = null;
                    return;
                }

                if (mChatCallbackContext != null) {
                    Intent intent = new Intent();
                    intent.setAction("closeChatWnd");
                    cordova.getActivity().sendBroadcast(intent);
                }

            } else if (video_type.equals("1")) {//视频聊天
                if (mChatCallbackContext == null) {
                    mChatCallbackContext = callbackContext;
                }

                starSWVideoActivity(cordova.getActivity(), created_roomid, video_type, room_name, selfuserId);

            } else if (video_type.equals("0")) {//音频聊天

                SystemManagerUtils.wakeUpAndUnlock(cordova.getActivity());

                if (mChatCallbackContext == null) {
                    mChatCallbackContext = callbackContext;
                }

                if (userInfoList.size() == 1) {
                    ChatUserInfo user = userInfoList.get(0);

                    ChatAudioActivity.setChatResultCallback(this);
                    Intent i = new Intent(cordova.getActivity(), ChatAudioActivity.class);
                    i.putExtra(ConstantApp.ACTION_KEY_CHANNEL_NAME, created_roomid);
                    i.putExtra(ConstantApp.ACTION_KEY_ENCRYPTION_KEY, "");
                    //i.putExtra(ConstantApp.ACTION_KEY_ENCRYPTION_MODE, cordova.getActivity().getResources().getStringArray(R.array.encryption_mode_values)[vSettings().mEncryptionModeIndex]);
                    i.putExtra(ConstantApp.ACTION_KEY_ENCRYPTION_MODE, "AES-128-XTS");
                    i.putExtra(ConstantApp.ACTION_KEY_VIDEO_TYPE, video_type);
                    i.putExtra(ConstantApp.ACTION_KEY_VIDEO_NAME, user.name);
                    i.putExtra(ConstantApp.ACTION_KEY_VIDEO_HEAD, user.avatar);
                    i.putExtra(ConstantApp.ACTION_KEY_VIDEO_USERID, selfuserId);

                    cordova.getActivity().startActivity(i);
                    onChatSuccess();
                } else {

                    ChatAudioMulteActivity.setChatMulteResultCallback(this);
                    Intent i = new Intent(cordova.getActivity(), ChatAudioMulteActivity.class);

                    i.putExtra(ConstantApp.ACTION_KEY_CHANNEL_NAME, created_roomid);
                    i.putExtra(ConstantApp.ACTION_KEY_ENCRYPTION_KEY, "");
                    i.putExtra(ConstantApp.ACTION_KEY_ENCRYPTION_MODE, "AES-128-XTS");
                    i.putExtra(ConstantApp.ACTION_KEY_VIDEO_TYPE, video_type);

                    Bundle bundle = new Bundle();
                    bundle.putParcelableArrayList("chatList", userInfoList);
                    i.putExtra(ConstantApp.ACTION_KEY_VIDEO_USERID, selfuserId);
                    i.putExtras(bundle);

                    cordova.getActivity().startActivity(i);
                    onChatSuccess();
                }

//
                Intent intent = new Intent();
                intent.setAction("closeInvitationWnd");
                cordova.getActivity().sendBroadcast(intent);

            }
        }
    }

    private void starSWVideoActivity(Context context, String created_roomid, String video_type, String name, String selfuserId) {
        //ToastUtil.show(cordova.getActivity(),created_roomid+ ""+String.valueOf(video_type));

        Log.i("starSWVideoActivity", "created_roomid ： " + " : " + created_roomid);

        forwardToRoom(created_roomid, video_type, name, selfuserId);

        Intent intent = new Intent();
        intent.setAction("closeInvitationWnd");
        cordova.getActivity().sendBroadcast(intent);

    }

    public void forwardToRoom(String channel, String video_type, String name, String selfuserId) {
//        EditText v_channel = (EditText) findViewById(R.id.channel_name);
//        String channel = v_channel.getText().toString();
//        vSettings().mChannelName = channel;
//
//        EditText v_encryption_key = (EditText) findViewById(R.id.encryption_key);
//        String encryption = v_encryption_key.getText().toString();
//        vSettings().mEncryptionKey = encryption;

        ChatActivity.setChatResultCallback(this);
        Intent i = new Intent(cordova.getActivity(), ChatActivity.class);

        i.putExtra(ConstantApp.ACTION_KEY_CHANNEL_NAME, channel);
        i.putExtra(ConstantApp.ACTION_KEY_ENCRYPTION_KEY, "");
        //i.putExtra(ConstantApp.ACTION_KEY_ENCRYPTION_MODE, cordova.getActivity().getResources().getStringArray(R.array.encryption_mode_values)[vSettings().mEncryptionModeIndex]);
        i.putExtra(ConstantApp.ACTION_KEY_ENCRYPTION_MODE, "AES-128-XTS");
        i.putExtra(ConstantApp.ACTION_KEY_VIDEO_TYPE, video_type);
        i.putExtra(ConstantApp.ACTION_KEY_VIDEO_NAME, name);

        i.putExtra(ConstantApp.ACTION_KEY_VIDEO_USERID, selfuserId);

        cordova.getActivity().startActivity(i);
        onChatSuccess();
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
    public void onInviteSuccess(String message) {

        if (message == null) {//为null,正常的视频流程，非null推送的视频流程

            if (mInviteCallbackContext != null) {

                if (message == null) {
                    mInviteCallbackContext.success();
                } else {
                    PluginResult result = new PluginResult(PluginResult.Status.OK, message);
                    result.setKeepCallback(true);
                    mInviteCallbackContext.sendPluginResult(result);
                }
                mInviteCallbackContext = null;
            }
        } else {
//            Log.i("messagecallback", "messagecallback ： " + " : " + messagecallback);
            if (messagecallback != null) {

                if (message == null) {
                    messagecallback.success();
                } else {
                    PluginResult result = new PluginResult(PluginResult.Status.OK, message);
                    result.setKeepCallback(true);
                    messagecallback.sendPluginResult(result);
                }
                //messagecallback = null;
            }
        }
    }

    @Override
    public void onInviteFaile(String errorMsg) {

        if (mInviteCallbackContext != null) {
            mInviteCallbackContext.error(errorMsg);
            mInviteCallbackContext = null;
        }

        if (messagecallback != null) {
            messagecallback.error(errorMsg);
            //messagecallback = null;
        }
    }

    @Override
    public void onChatSuccess() {
        PluginResult result = new PluginResult(PluginResult.Status.OK, "chat ok");
        result.setKeepCallback(true);
        mChatCallbackContext.sendPluginResult(result);
    }

    @Override
    public void onChatFaile(String errorMsg) {

        if (mChatCallbackContext != null) {
            PluginResult result = new PluginResult(PluginResult.Status.ERROR, errorMsg);
            result.setKeepCallback(true);
            mChatCallbackContext.sendPluginResult(result);
            mChatCallbackContext = null;
        }
    }

//    @Override
//    public void onGroupChatSuccess(String message) {
//
//        if(mInviteCallbackContext != null){
//
//            if(message == null){
//                mInviteCallbackContext.success();
//            }else{
//                PluginResult result = new PluginResult(PluginResult.Status.OK, message);
//                result.setKeepCallback(true);
//                mInviteCallbackContext.sendPluginResult(result);
//            }
//        }
//
//
//        if(messagecallback != null ){
//
//            if(message == null){
//                messagecallback.success();
//            }else{
//                PluginResult result = new PluginResult(PluginResult.Status.OK, message);
//                result.setKeepCallback(true);
//                messagecallback.sendPluginResult(result);
//            }
//            messagecallback = null;
//        }
//
//    }
//
//    @Override
//    public void onGroupChatFaile(String errorMsg) {
//
//        if (mChatCallbackContext != null) {
//            PluginResult result = new PluginResult(PluginResult.Status.ERROR, errorMsg);
//            result.setKeepCallback(true);
//            mChatCallbackContext.sendPluginResult(result);
//            mChatCallbackContext = null;
//        }
//
//        if (mInviteCallbackContext != null) {
//            PluginResult result = new PluginResult(PluginResult.Status.ERROR, errorMsg);
//            result.setKeepCallback(true);
//            mInviteCallbackContext.sendPluginResult(result);
//            mInviteCallbackContext = null;
//        }
//
//        if(messagecallback != null){
//            messagecallback.error(errorMsg);
//            messagecallback = null;
//        }
//    }
}
