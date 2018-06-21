package com.xiangpu.receivers;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;
import android.view.WindowManager;

import com.alibaba.sdk.android.push.MessageReceiver;
import com.alibaba.sdk.android.push.notification.CPushMessage;
import com.lssl.activity.SuneeeApplication;
import com.xiangpu.activity.UcpWebViewActivity;
import com.xiangpu.activity.person.LoginActivity2;
import com.xiangpu.common.Constants;
import com.xiangpu.dialog.CommonDialog;
import com.xiangpu.utils.LogUtil;
import com.xiangpu.utils.SharedPrefUtils;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;

import chat.rocket.android.RocketChatApplication;
import chat.rocket.android.RocketChatCache;
import chat.rocket.android.video.model.CloseBusEvent;
import me.leolin.shortcutbadger.ShortcutBadger;

/**
 * @author: 正纬
 * @since: 15/4/9
 * @version: 1.1
 * @feature: 阿里云用于接收推送的通知和消息
 */
public class MyMessageReceiver extends MessageReceiver {

    // 消息接收部分的LOG_TAG
    public static final String REC_TAG = "receiver";
    public static int _ALIYUN_NOTIFICATION_ID_ = 0;

    /**
     * 推送通知的回调方法，定子链发消息用的这种推送方式
     *
     * @param context
     * @param title
     * @param summary
     * @param extraMap
     */
    @Override
    public void onNotification(Context context, String title, String summary, Map<String, String> extraMap) {
        // TODO 处理推送通知
        if (null != extraMap) {
            for (Map.Entry<String, String> entry : extraMap.entrySet()) {
                Log.i(REC_TAG, "@Get diy param : Key=" + entry.getKey() + " , Value=" + entry.getValue());
            }

            String strid = extraMap.get("_ALIYUN_NOTIFICATION_ID_");
            _ALIYUN_NOTIFICATION_ID_ = Integer.parseInt(strid);

            LogUtil.i(TAG, "-------------ABC-----S--------");
            Log.i(REC_TAG, "----_ALIYUN_NOTIFICATION_ID_:" + _ALIYUN_NOTIFICATION_ID_);
            LogUtil.i(TAG, "-------------ABC------E-------");

            String badgeCount = extraMap.get("badgeCount");
            LogUtil.i(TAG, "message center send badgeCount:" + badgeCount);
            if (!TextUtils.isEmpty(badgeCount)) {
                ShortcutBadger.applyCount(context, Integer.parseInt(badgeCount));
            }
        } else {
            Log.i(REC_TAG, "@收到通知 && 自定义消息为空");

        }
        Log.i(REC_TAG, "收到一条推送通知 ： " + title + ", summary:" + summary + "，extraMap：" + extraMap);

        //play video
        try {
            String router = SharedPrefUtils.getStringData(context, "router", "");
            JSONObject jsonObject = new JSONObject(router);
            String nType = jsonObject.getString("nType");
            if ("video".equals(nType) || "audio".equals(nType)) {
                RocketChatApplication.playSounde(0);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        SuneeeApplication.wakeUpAndUnlock();
//        MainApplication.setConsoleText("收到一条推送通知 ： " + title + ", summary:" + summary);
    }

    /**
     * 应用处于前台时通知到达回调。注意:该方法仅对自定义样式通知有效,相关详情请参考https://help.aliyun.com/document_detail/30066.html?spm=5176.product30047.6.620.wjcC87#h3-3-4-basiccustompushnotification-api
     *
     * @param context
     * @param title
     * @param summary
     * @param extraMap
     * @param openType
     * @param openActivity
     * @param openUrl
     */
    @Override
    protected void onNotificationReceivedInApp(Context context, String title, String summary, Map<String, String> extraMap, int openType, String openActivity, String openUrl) {
        Log.i(REC_TAG, "onNotificationReceivedInApp ： " + " : " + title + " : " + summary + "  " + extraMap + " : " + openType + " : " + openActivity + " : " + openUrl);
//        MainApplication.setConsoleText("onNotificationReceivedInApp ： " + " : " + title + " : " + summary);
    }

    /**
     * 推送消息的回调方法，限制登录用的这种推送方式
     *
     * @param context
     * @param cPushMessage
     */
    @Override
    public void onMessage(final Context context, CPushMessage cPushMessage) {
        Log.i(REC_TAG, "收到一条推送消息 ： " + cPushMessage.getTitle() + ", content:" + cPushMessage.getContent());
        if (!TextUtils.isEmpty(cPushMessage.getTitle())) {
            //title为user的时候表示该账号已经被挤下线
            if ("user".equals(cPushMessage.getTitle())) {
                ShortcutBadger.removeCount(context);//被挤掉之后，桌面右上角未读消息清零
//                SharedPrefUtils.saveBooleanData(context, "remember_password", false);//取消记住密码
                SharedPrefUtils.saveStringData(context, "psdWord", null);//清除缓存密码
                SuneeeApplication.getInstance().removeActivity_(UcpWebViewActivity.class);
                String sessionId = SharedPrefUtils.getSessionId(context);
                RocketChatCache.INSTANCE.setDownLine(true);
                EventBus.getDefault().post(new CloseBusEvent());//关闭当前音视频对话框
                //如果sessionId为空，则表示该用户没有登录，就不弹出被下线的提示框
                if (TextUtils.isEmpty(sessionId)){
                    return;
                }
                SharedPrefUtils.saveSessionId(context,null);//清除本地的sessionId
                CommonDialog commonDialog = new CommonDialog(context);
//                commonDialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
                commonDialog.getWindow().setType(WindowManager.LayoutParams.TYPE_TOAST);
                commonDialog.show();
                commonDialog.setTvTitle("下线通知");
                commonDialog.setTvContent("你的账号已在另一台设备上登录");
//                commonDialog.setConfirmButtonText("重新登录", Color.parseColor("#444242"));
                commonDialog.setCancelButtonText("我知道了", true);
                commonDialog.setOnResultListener(new CommonDialog.OnResultListener() {
                    @Override
                    public void onConfirm() {
                    }

                    @Override
                    public void onCancel() {
                        Intent loginIntent = new Intent(context, LoginActivity2.class);
                        loginIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        context.startActivity(loginIntent);
                    }
                });
                commonDialog.setCanceledOnTouchOutside(false);
                commonDialog.setCancelable(false);
            }
        } else {
            LogUtil.e(REC_TAG, "推送消息 title is null");
        }
//        MainApplication.setConsoleText(cPushMessage.getTitle() + ", content:" + cPushMessage.getContent());
    }
//
//    private Context mContext = null;
//
//    @Override
//    public void onConfirm() {
//
//    }
//
//    @Override
//    public void onCancel() {
////        SharedPrefUtils.saveBooleanData(mContext, "remember_password", false);
//        Intent intent2 = new Intent(mContext, LoginActivity2.class);
//        intent2.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//        mContext.startActivity(intent2);
//    }

    /**
     * 从通知栏打开通知的扩展处理
     *
     * @param context
     * @param title
     * @param summary
     * @param extraMap
     */
    @Override
    public void onNotificationOpened(Context context, String title, String summary, String extraMap) {
        Log.i(REC_TAG, "onNotificationOpened ： " + " : " + title + " : " + summary + " : " + extraMap);
        try {

            JSONObject jsonObject = new JSONObject(extraMap);
            String module = jsonObject.getString("module");

//            if (Constants.MODULE_UCP.equals(module)) {
//                String voice = jsonObject.getString("voice");//默认default 为文字，voide 为视频
//
//                if (voice.equals("video")) {
//                    String router = jsonObject.getString("router");
//                    if (router != null) {
//
//                        String callback = (new JSONObject(router)).getString("callback");
//                        String exec = (new JSONObject(router)).getString("exec");
//
//                        Intent intent = new Intent();
//                        intent.setAction(module);//离线音视频
//                        intent.putExtra("router", exec);
//                        intent.putExtra("callback", callback);
//
//                        context.sendBroadcast(intent);
//                    }
//                } else {
                    String router = jsonObject.getString("router");
                    SharedPrefUtils.saveStringData(context, "router", router);
                    SuneeeApplication.clickMsg = true;
//                }
//            } else {
//
//            }

        } catch (JSONException e) {
            e.printStackTrace();
            LogUtil.e(TAG, "---parse router error---");
        }

//        MainApplication.setConsoleText("onNotificationOpened ： " + " : " + title + " : " + summary + " : " + extraMap);
    }

    /**
     * 通知删除回调
     *
     * @param context
     * @param messageId
     */
    @Override
    public void onNotificationRemoved(Context context, String messageId) {
        Log.i(REC_TAG, "onNotificationRemoved ： " + messageId);
        RocketChatApplication.stopSound();
//        MainApplication.setConsoleText("onNotificationRemoved ： " + messageId);
    }

    /**
     * 无动作通知点击回调。当在后台或阿里云控制台指定的通知动作为无逻辑跳转时,通知点击回调为onNotificationClickedWithNoAction而不是onNotificationOpened
     *
     * @param context
     * @param title
     * @param summary
     * @param extraMap
     */
    @Override
    protected void onNotificationClickedWithNoAction(Context context, String title, String summary, String extraMap) {
        Log.i(REC_TAG, "onNotificationClickedWithNoAction ： " + " : " + title + " : " + summary + " : " + extraMap);
//        MainApplication.setConsoleText("onNotificationClickedWithNoAction ： " + " : " + title + " : " + summary + " : " + extraMap);
    }
}