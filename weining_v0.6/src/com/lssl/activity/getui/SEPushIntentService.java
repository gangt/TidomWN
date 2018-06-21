package com.lssl.activity.getui;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.text.Html;
import android.util.Log;

import com.adobe.phonegap.push.PushConstants;
import com.adobe.phonegap.push.PushHandlerActivity;
import com.adobe.phonegap.push.PushPlugin;
import com.igexin.sdk.GTIntentService;
import com.igexin.sdk.PushConsts;
import com.igexin.sdk.PushManager;
import com.igexin.sdk.message.FeedbackCmdMessage;
import com.igexin.sdk.message.GTCmdMessage;
import com.igexin.sdk.message.GTTransmitMessage;
import com.igexin.sdk.message.SetTagCmdMessage;
import com.konecty.rocket.chat.R;
import com.lssl.activity.SuneeeApplication;
import com.xiangpu.activity.usercenter.MessageActivity;
import com.xiangpu.bean.PersonalMessage;
import com.xiangpu.common.Constants;
import com.xiangpu.utils.LogUtil;
import com.xiangpu.utils.SharedPrefUtils;
import com.xiangpu.utils.Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

import me.leolin.shortcutbadger.ShortcutBadger;

/**
 * Created by suneee on 2017/1/13.
 */

public class SEPushIntentService extends GTIntentService implements PushConstants {
    private static final String LOG_TAG = "SEPushIntentService";
    /**
     * 为了观察透传数据变化.
     */
    private static int cnt;
    public static int badgeCount = 0;

    @Override
    public void onReceiveServicePid(Context context, int pid) {
        Log.d(TAG, "onReceiveServicePid -> " + pid);
    }

    @Override
    public void onReceiveMessageData(Context context, GTTransmitMessage msg) {
        boolean isOpenMessagePush = SharedPrefUtils.getBooleanData(this, Constants.IS_OPEN_MESSAGE_PUSH, true);
        //如果消息推送开关关闭，不接受消息
        if (!isOpenMessagePush) {
            return;
        }
        String appid = msg.getAppid();
        String taskid = msg.getTaskId();
        String messageid = msg.getMessageId();
        byte[] payload = msg.getPayload();
        String pkg = msg.getPkgName();
        String cid = msg.getClientId();

        // 第三方回执调用接口，actionid范围为90000-90999，可根据业务场景执行
        boolean result = PushManager.getInstance().sendFeedbackMessage(context, taskid, messageid, 90001);
        Log.d(TAG, "call sendFeedbackMessage = " + (result ? "success" : "failed"));

        Log.d(TAG, "onReceiveMessageData -> " + "appid = " + appid + "\ntaskid = " + taskid + "\nmessageid = " + messageid + "\npkg = " + pkg
                + "\ncid = " + cid);

        if (payload != null) {
            SuneeeApplication.user.isHavMessage = true;
            String data = new String(payload);
            LogUtil.i(TAG, "推送内容:" + data);
            badgeCount++;
            LogUtil.i(TAG, "badgeCount:" + badgeCount);
            boolean success = ShortcutBadger.applyCount(this, badgeCount);
//            Toast.makeText(getApplicationContext(), "Set count=" + badgeCount + ", success=" + success, Toast.LENGTH_SHORT).show();
            long receiveTime = System.currentTimeMillis();

            saveDataToDatabase(data, receiveTime);
            refreshMessageList();

            Bundle extras = createBundle(payload.toString());

            //原gcm的处理逻辑
            SharedPreferences prefs = getApplicationContext().getSharedPreferences(PushPlugin.COM_ADOBE_PHONEGAP_PUSH, Context.MODE_PRIVATE);
            boolean forceShow = prefs.getBoolean(FORCE_SHOW, false);
            // if we are in the foreground and forceShow is `false` only send data
            if (!forceShow && PushPlugin.isInForeground()) {
                Log.d(LOG_TAG, "foreground");
                extras.putBoolean(FOREGROUND, true);
                PushPlugin.sendExtras(extras);
            }
            // if we are in the foreground and forceShow is `true`, force show the notification if the data has at least a message or title
            else if (forceShow && PushPlugin.isInForeground()) {
                Log.d(LOG_TAG, "foreground force");
                extras.putBoolean(FOREGROUND, true);

                showNotificationIfPossible(getApplicationContext(), extras);
            }
            // if we are not in the foreground always send notification if the data has at least a message or title
            else {
                Log.d(LOG_TAG, "background");
                extras.putBoolean(FOREGROUND, false);

                showNotificationIfPossible(getApplicationContext(), extras);
            }
        }


        Log.d(TAG, "----------------------------------------------------------------------------------------------");
    }

    private void refreshMessageList() {
        LogUtil.i(TAG, "Refresh Message List");
        Intent mIntent = new Intent(Constants.REFRESH_MESSAGE_LIST);
        //发送广播
        sendBroadcast(mIntent);
    }

    /**
     * 将消息保存到数据库
     *
     * @param data        {module=ucp, title=@子悦, context= 测试数据}
     * @param receiveTime
     */
    private void saveDataToDatabase(String data, long receiveTime) {
        try {

            LogUtil.i(TAG, "message data:" + data);
            JSONObject messageData = new JSONObject(Utils.remove(data, ' '));
            String module = messageData.getString("module");
            String title = messageData.getString("title");
            String context = messageData.getString("context");
//            String router = messageData.getString("router");
            showNotification(context, receiveTime, module, "");
            PersonalMessage personalMessage = new PersonalMessage();
            personalMessage.setModule(module);
            personalMessage.setMessageTitle(title);
            personalMessage.setMessageContent(context);
            personalMessage.setReceiveTime(receiveTime);
            personalMessage.setUserId(SharedPrefUtils.getUserId(this));
            personalMessage.save();

        } catch (JSONException e) {
            e.printStackTrace();
        }


    }

    /**
     * 展示推送通知
     *
     * @param data
     */
    private void showNotification(String data, long receiveTime, String module, String router) {
        NotificationManager mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this);
        mBuilder.setContentTitle("象谱")// 设置通知栏标题
                .setContentText(data)// 设置通知栏显示内容
                .setContentIntent(getDefalutIntent(Notification.FLAG_AUTO_CANCEL, module, router)) // 设置通知栏点击意图
                // .setNumber(number) //设置通知集合的数量
                .setTicker("通知来了") // 通知首次出现在通知栏，带上升动画效果的
                .setWhen(receiveTime)// 通知产生的时间，会在通知信息里显示，一般是系统获取到的时间
                .setPriority(Notification.PRIORITY_DEFAULT) // 设置该通知优先级
                // .setAutoCancel(true)//设置这个标志当用户单击面板就可以让通知将自动取消
                .setOngoing(false)// ture，设置他为一个正在进行的通知。他们通常是用来表示一个后台任务,用户积极参与(如播放音乐)或以某种方式正在等待,因此占用设备(如一个文件下载,同步操作,主动网络连接)
//                .setDefaults(Notification.DEFAULT_SOUND)// 向通知添加声音、闪灯和振动效果的最简单、最一致的方式是使用当前的用户默认设置，使用defaults属性，可以组合
//                .setDefaults(Notification.DEFAULT_VIBRATE)
                // Notification.DEFAULT_ALL Notification.DEFAULT_SOUND 添加声音 //
                // requires VIBRATE permission
                .setSmallIcon(R.mipmap.icon);// 设置通知小ICON
        LogUtil.i(TAG, "pushId:" + pushId);

        LogUtil.i(TAG, "当前毫秒值:" + System.currentTimeMillis());
        Notification notification = mBuilder.build();
//        long[] vibrate = {0,1000,1000,1000};
//        notification.vibrate = vibrate;
        notification.flags = Notification.DEFAULT_LIGHTS | Notification.FLAG_AUTO_CANCEL;
        mNotificationManager.notify((int) System.currentTimeMillis(), notification);
    }

    public int pushId;

    public PendingIntent getDefalutIntent(int flags, String module, String router) {
//        Intent intent = null;
//        if (Constants.MODULE_UCP.equals(module)){
//            sendRouterToUcp(router);
//            intent = new Intent(this, UcpWebViewActivity.class);
//        }else if (Constants.MODULE_HNQ.equals(module)){
//            intent = new Intent(this, WebMainActivity.class);
//            intent.putExtra("title", "汇能器");
//            intent.putExtra("link", router);
//            intent.putExtra("TagPlugin", "");
//        }
//        PendingIntent pendingIntent = PendingIntent.getActivity(this, pushId, intent,
//                flags);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, pushId, new Intent(this, MessageActivity.class),
                flags);
        return pendingIntent;
    }

    private void sendRouterToUcp(String router) {
        LogUtil.i(TAG, "send Router To Ucp");
        Intent mIntent = new Intent(Constants.SEND_ROUTER_TO_UCP);
        mIntent.putExtra("router", router);
        //发送广播
        sendBroadcast(mIntent);
    }

    private Bundle createBundle(String payload) {
        Bundle bundle = new Bundle();


        return bundle;
    }

    @Override
    public void onReceiveClientId(Context context, String clientid) {
        Log.e(TAG, "onReceiveClientId -> " + "clientid = " + clientid);
        SharedPrefUtils.saveStringData(this, Constants.CLIENT_ID, clientid);
    }

    @Override
    public void onReceiveOnlineState(Context context, boolean online) {
        Log.d(TAG, "onReceiveOnlineState -> " + (online ? "online" : "offline"));
    }

    @Override
    public void onReceiveCommandResult(Context context, GTCmdMessage cmdMessage) {
        Log.d(TAG, "onReceiveCommandResult -> " + cmdMessage);

        int action = cmdMessage.getAction();

        if (action == PushConsts.SET_TAG_RESULT) {
            setTagResult((SetTagCmdMessage) cmdMessage);
        } else if ((action == PushConsts.THIRDPART_FEEDBACK)) {
            feedbackResult((FeedbackCmdMessage) cmdMessage);
        }
    }

    private void setTagResult(SetTagCmdMessage setTagCmdMsg) {
        String sn = setTagCmdMsg.getSn();
        String code = setTagCmdMsg.getCode();

        String text = "设置标签失败, 未知异常";
        switch (Integer.valueOf(code)) {
            case PushConsts.SETTAG_SUCCESS:
                text = "设置标签成功";
                break;

            case PushConsts.SETTAG_ERROR_COUNT:
                text = "设置标签失败, tag数量过大, 最大不能超过200个";
                break;

            case PushConsts.SETTAG_ERROR_FREQUENCY:
                text = "设置标签失败, 频率过快, 两次间隔应大于1s且一天只能成功调用一次";
                break;

            case PushConsts.SETTAG_ERROR_REPEAT:
                text = "设置标签失败, 标签重复";
                break;

            case PushConsts.SETTAG_ERROR_UNBIND:
                text = "设置标签失败, 服务未初始化成功";
                break;

            case PushConsts.SETTAG_ERROR_EXCEPTION:
                text = "设置标签失败, 未知异常";
                break;

            case PushConsts.SETTAG_ERROR_NULL:
                text = "设置标签失败, tag 为空";
                break;

            case PushConsts.SETTAG_NOTONLINE:
                text = "还未登陆成功";
                break;

            case PushConsts.SETTAG_IN_BLACKLIST:
                text = "该应用已经在黑名单中,请联系售后支持!";
                break;

            case PushConsts.SETTAG_NUM_EXCEED:
                text = "已存 tag 超过限制";
                break;

            default:
                break;
        }

        Log.d(TAG, "settag result sn = " + sn + ", code = " + code + ", text = " + text);
    }

    private void feedbackResult(FeedbackCmdMessage feedbackCmdMsg) {
        String appid = feedbackCmdMsg.getAppid();
        String taskid = feedbackCmdMsg.getTaskId();
        String actionid = feedbackCmdMsg.getActionId();
        String result = feedbackCmdMsg.getResult();
        long timestamp = feedbackCmdMsg.getTimeStamp();
        String cid = feedbackCmdMsg.getClientId();

        Log.d(TAG, "onReceiveCommandResult -> " + "appid = " + appid + "\ntaskid = " + taskid + "\nactionid = " + actionid + "\nresult = " + result
                + "\ncid = " + cid + "\ntimestamp = " + timestamp);
    }

    private static HashMap<Integer, ArrayList<String>> messageMap = new HashMap<Integer, ArrayList<String>>();

    public void setNotification(int notId, String message) {
        ArrayList<String> messageList = messageMap.get(notId);
        if (messageList == null) {
            messageList = new ArrayList<String>();
            messageMap.put(notId, messageList);
        }

        if (message.isEmpty()) {
            messageList.clear();
        } else {
            messageList.add(message);
        }
    }

    private void showNotificationIfPossible(Context context, Bundle extras) {

        // Send a notification if there is a message or title, otherwise just send data
        String message = extras.getString(MESSAGE);
        String title = extras.getString(TITLE);

        Log.d(LOG_TAG, "message =[" + message + "]");
        Log.d(LOG_TAG, "title =[" + title + "]");

        if ((message != null && message.length() != 0) ||
                (title != null && title.length() != 0)) {

            Log.d(LOG_TAG, "create notification");

            createNotification(context, extras);
        } else {
            Log.d(LOG_TAG, "send notification event");
            PushPlugin.sendExtras(extras);
        }
    }

    public void createNotification(Context context, Bundle extras) {
        NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        String appName = getAppName(this);
        String packageName = context.getPackageName();
        Resources resources = context.getResources();

        int notId = parseInt(NOT_ID, extras);
        Intent notificationIntent = new Intent(this, PushHandlerActivity.class);
        notificationIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        notificationIntent.putExtra(PUSH_BUNDLE, extras);
        notificationIntent.putExtra(NOT_ID, notId);

        int requestCode = new Random().nextInt();
        PendingIntent contentIntent = PendingIntent.getActivity(this, requestCode, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(context)
                        .setWhen(System.currentTimeMillis())
                        .setContentTitle(extras.getString(TITLE))
                        .setTicker(extras.getString(TITLE))
                        .setContentIntent(contentIntent)
                        .setAutoCancel(true);

        SharedPreferences prefs = context.getSharedPreferences(PushPlugin.COM_ADOBE_PHONEGAP_PUSH, Context.MODE_PRIVATE);
        String localIcon = prefs.getString(ICON, null);
        String localIconColor = prefs.getString(ICON_COLOR, null);
        boolean soundOption = prefs.getBoolean(SOUND, true);
        boolean vibrateOption = prefs.getBoolean(VIBRATE, true);
        Log.d(LOG_TAG, "stored icon=" + localIcon);
        Log.d(LOG_TAG, "stored iconColor=" + localIconColor);
        Log.d(LOG_TAG, "stored sound=" + soundOption);
        Log.d(LOG_TAG, "stored vibrate=" + vibrateOption);

        /*
         * Notification Vibration
         */

        setNotificationVibration(extras, vibrateOption, mBuilder);

        /*
         * Notification Icon Color
         *
         * Sets the small-icon background color of the notification.
         * To use, add the `iconColor` key to plugin android options
         *
         */
        setNotificationIconColor(extras.getString("color"), mBuilder, localIconColor);

        /*
         * Notification Icon
         *
         * Sets the small-icon of the notification.
         *
         * - checks the plugin options for `icon` key
         * - if none, uses the application icon
         *
         * The icon value must be a string that maps to a drawable resource.
         * If no resource is found, falls
         *
         */
        setNotificationSmallIcon(context, extras, packageName, resources, mBuilder, localIcon);

        /*
         * Notification Large-Icon
         *
         * Sets the large-icon of the notification
         *
         * - checks the gcm data for the `image` key
         * - checks to see if remote image, loads it.
         * - checks to see if assets image, Loads It.
         * - checks to see if resource image, LOADS IT!
         * - if none, we don't set the large icon
         *
         */
        setNotificationLargeIcon(extras, packageName, resources, mBuilder);

        /*
         * Notification Sound
         */
        if (soundOption) {
            setNotificationSound(context, extras, mBuilder);
        }

        /*
         *  LED Notification
         */
        setNotificationLedColor(extras, mBuilder);

        /*
         *  Priority Notification
         */
        setNotificationPriority(extras, mBuilder);

        /*
         * Notification message
         */
        setNotificationMessage(notId, extras, mBuilder);

        /*
         * Notification count
         */
        setNotificationCount(extras, mBuilder);

        /*
         * Notification add actions
         */
        createActions(extras, mBuilder, resources, packageName);

        mNotificationManager.notify(appName, notId, mBuilder.build());
    }

    private void createActions(Bundle extras, NotificationCompat.Builder mBuilder, Resources resources, String packageName) {
        Log.d(LOG_TAG, "create actions");
        String actions = extras.getString(ACTIONS);
        if (actions != null) {
            try {
                JSONArray actionsArray = new JSONArray(actions);
                for (int i = 0; i < actionsArray.length(); i++) {
                    Log.d(LOG_TAG, "adding action");
                    JSONObject action = actionsArray.getJSONObject(i);
                    Log.d(LOG_TAG, "adding callback = " + action.getString(CALLBACK));
                    Intent intent = new Intent(this, PushHandlerActivity.class);
                    intent.putExtra(CALLBACK, action.getString(CALLBACK));
                    intent.putExtra(PUSH_BUNDLE, extras);
                    PendingIntent pIntent = PendingIntent.getActivity(this, i, intent, PendingIntent.FLAG_UPDATE_CURRENT);

                    mBuilder.addAction(resources.getIdentifier(action.getString(ICON), DRAWABLE, packageName),
                            action.getString(TITLE), pIntent);
                }
            } catch (JSONException e) {
                // nope
            }
        }
    }

    private void setNotificationCount(Bundle extras, NotificationCompat.Builder mBuilder) {
        String msgcnt = extras.getString(MSGCNT);
        if (msgcnt == null) {
            msgcnt = extras.getString(BADGE);
        }
        if (msgcnt != null) {
            mBuilder.setNumber(Integer.parseInt(msgcnt));
        }
    }

    private void setNotificationVibration(Bundle extras, Boolean vibrateOption, NotificationCompat.Builder mBuilder) {
        String vibrationPattern = extras.getString(VIBRATION_PATTERN);
        if (vibrationPattern != null) {
            String[] items = vibrationPattern.replaceAll("\\[", "").replaceAll("\\]", "").split(",");
            long[] results = new long[items.length];
            for (int i = 0; i < items.length; i++) {
                try {
                    results[i] = Long.parseLong(items[i].trim());
                } catch (NumberFormatException nfe) {
                }
            }
            mBuilder.setVibrate(results);
        } else {
            if (vibrateOption) {
                mBuilder.setDefaults(Notification.DEFAULT_VIBRATE);
            }
        }
    }

    private void setNotificationMessage(int notId, Bundle extras, NotificationCompat.Builder mBuilder) {
        String message = extras.getString(MESSAGE);

        String style = extras.getString(STYLE, STYLE_TEXT);
        if (STYLE_INBOX.equals(style)) {
            setNotification(notId, message);

            mBuilder.setContentText(message);

            ArrayList<String> messageList = messageMap.get(notId);
            Integer sizeList = messageList.size();
            if (sizeList > 1) {
                String sizeListMessage = sizeList.toString();
                String stacking = sizeList + " more";
                if (extras.getString(SUMMARY_TEXT) != null) {
                    stacking = extras.getString(SUMMARY_TEXT);
                    stacking = stacking.replace("%n%", sizeListMessage);
                }
                NotificationCompat.InboxStyle notificationInbox = new NotificationCompat.InboxStyle()
                        .setBigContentTitle(extras.getString(TITLE))
                        .setSummaryText(stacking);

                for (int i = messageList.size() - 1; i >= 0; i--) {
                    notificationInbox.addLine(Html.fromHtml(messageList.get(i)));
                }

                mBuilder.setStyle(notificationInbox);
            } else {
                NotificationCompat.BigTextStyle bigText = new NotificationCompat.BigTextStyle();
                if (message != null) {
                    bigText.bigText(message);
                    bigText.setBigContentTitle(extras.getString(TITLE));
                    mBuilder.setStyle(bigText);
                }
            }
        } else if (STYLE_PICTURE.equals(style)) {
            setNotification(notId, "");

            NotificationCompat.BigPictureStyle bigPicture = new NotificationCompat.BigPictureStyle();
            bigPicture.bigPicture(getBitmapFromURL(extras.getString(PICTURE)));
            bigPicture.setBigContentTitle(extras.getString(TITLE));
            bigPicture.setSummaryText(extras.getString(SUMMARY_TEXT));

            mBuilder.setContentTitle(extras.getString(TITLE));
            mBuilder.setContentText(message);

            mBuilder.setStyle(bigPicture);
        } else {
            setNotification(notId, "");

            NotificationCompat.BigTextStyle bigText = new NotificationCompat.BigTextStyle();

            if (message != null) {
                mBuilder.setContentText(Html.fromHtml(message));

                bigText.bigText(message);
                bigText.setBigContentTitle(extras.getString(TITLE));

                String summaryText = extras.getString(SUMMARY_TEXT);
                if (summaryText != null) {
                    bigText.setSummaryText(summaryText);
                }

                mBuilder.setStyle(bigText);
            }
            /*
            else {
                mBuilder.setContentText("<missing message content>");
            }
            */
        }
    }

    private void setNotificationSound(Context context, Bundle extras, NotificationCompat.Builder mBuilder) {
        String soundname = extras.getString(SOUNDNAME);
        if (soundname == null) {
            soundname = extras.getString(SOUND);
        }
        if (soundname != null && !soundname.contentEquals(SOUND_DEFAULT)) {
            Uri sound = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE
                    + "://" + context.getPackageName() + "/raw/" + soundname);
            Log.d(LOG_TAG, sound.toString());
            mBuilder.setSound(sound);
        } else {
            mBuilder.setSound(android.provider.Settings.System.DEFAULT_NOTIFICATION_URI);
        }
    }

    private void setNotificationLedColor(Bundle extras, NotificationCompat.Builder mBuilder) {
        String ledColor = extras.getString(LED_COLOR);
        if (ledColor != null) {
            // Converts parse Int Array from ledColor
            String[] items = ledColor.replaceAll("\\[", "").replaceAll("\\]", "").split(",");
            int[] results = new int[items.length];
            for (int i = 0; i < items.length; i++) {
                try {
                    results[i] = Integer.parseInt(items[i].trim());
                } catch (NumberFormatException nfe) {
                }
            }
            if (results.length == 4) {
                mBuilder.setLights(Color.argb(results[0], results[1], results[2], results[3]), 500, 500);
            } else {
                Log.e(LOG_TAG, "ledColor parameter must be an array of length == 4 (ARGB)");
            }
        }
    }

    private void setNotificationPriority(Bundle extras, NotificationCompat.Builder mBuilder) {
        String priorityStr = extras.getString(PRIORITY);
        if (priorityStr != null) {
            try {
                Integer priority = Integer.parseInt(priorityStr);
                if (priority >= NotificationCompat.PRIORITY_MIN && priority <= NotificationCompat.PRIORITY_MAX) {
                    mBuilder.setPriority(priority);
                } else {
                    Log.e(LOG_TAG, "Priority parameter must be between -2 and 2");
                }
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
        }
    }

    private void setNotificationLargeIcon(Bundle extras, String packageName, Resources resources, NotificationCompat.Builder mBuilder) {
        String gcmLargeIcon = extras.getString(IMAGE); // from gcm
        if (gcmLargeIcon != null) {
            if (gcmLargeIcon.startsWith("http://") || gcmLargeIcon.startsWith("https://")) {
                mBuilder.setLargeIcon(getBitmapFromURL(gcmLargeIcon));
                Log.d(LOG_TAG, "using remote large-icon from gcm");
            } else {
                AssetManager assetManager = getAssets();
                InputStream istr;
                try {
                    istr = assetManager.open(gcmLargeIcon);
                    Bitmap bitmap = BitmapFactory.decodeStream(istr);
                    mBuilder.setLargeIcon(bitmap);
                    Log.d(LOG_TAG, "using assets large-icon from gcm");
                } catch (IOException e) {
                    int largeIconId = 0;
                    largeIconId = resources.getIdentifier(gcmLargeIcon, DRAWABLE, packageName);
                    if (largeIconId != 0) {
                        Bitmap largeIconBitmap = BitmapFactory.decodeResource(resources, largeIconId);
                        mBuilder.setLargeIcon(largeIconBitmap);
                        Log.d(LOG_TAG, "using resources large-icon from gcm");
                    } else {
                        Log.d(LOG_TAG, "Not setting large icon");
                    }
                }
            }
        }
    }

    private void setNotificationSmallIcon(Context context, Bundle extras, String packageName, Resources resources, NotificationCompat.Builder mBuilder, String localIcon) {
        int iconId = 0;
        String icon = extras.getString(ICON);
        if (icon != null) {
            iconId = resources.getIdentifier(icon, DRAWABLE, packageName);
            Log.d(LOG_TAG, "using icon from plugin options");
        } else if (localIcon != null) {
            iconId = resources.getIdentifier(localIcon, DRAWABLE, packageName);
            Log.d(LOG_TAG, "using icon from plugin options");
        }
        if (iconId == 0) {
            Log.d(LOG_TAG, "no icon resource found - using application icon");
            iconId = context.getApplicationInfo().icon;
        }
        mBuilder.setSmallIcon(iconId);
    }

    private void setNotificationIconColor(String color, NotificationCompat.Builder mBuilder, String localIconColor) {
        int iconColor = 0;
        if (color != null) {
            try {
                iconColor = Color.parseColor(color);
            } catch (IllegalArgumentException e) {
                Log.e(LOG_TAG, "couldn't parse color from android options");
            }
        } else if (localIconColor != null) {
            try {
                iconColor = Color.parseColor(localIconColor);
            } catch (IllegalArgumentException e) {
                Log.e(LOG_TAG, "couldn't parse color from android options");
            }
        }
        if (iconColor != 0) {
            mBuilder.setColor(iconColor);
        }
    }

    public Bitmap getBitmapFromURL(String strURL) {
        try {
            URL url = new URL(strURL);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            return BitmapFactory.decodeStream(input);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private static String getAppName(Context context) {
        CharSequence appName = context.getPackageManager().getApplicationLabel(context.getApplicationInfo());
        return (String) appName;
    }

    private int parseInt(String value, Bundle extras) {
        int retval = 0;

        try {
            retval = Integer.parseInt(extras.getString(value));
        } catch (NumberFormatException e) {
            Log.e(LOG_TAG, "Number format exception - Error parsing " + value + ": " + e.getMessage());
        } catch (Exception e) {
            Log.e(LOG_TAG, "Number format exception - Error parsing " + value + ": " + e.getMessage());
        }

        return retval;
    }
}
