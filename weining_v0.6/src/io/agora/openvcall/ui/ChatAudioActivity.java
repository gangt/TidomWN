package io.agora.openvcall.ui;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.PorterDuff;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewStub;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.konecty.rocket.chat.R;
import com.xiangpu.bean.IRecordTimerInterface;
import com.xiangpu.bean.RecordTime;
import com.xiangpu.utils.BitmapUtils;
import com.xiangpu.utils.Utils;
import com.xiangpu.dialog.CommonDialog;

import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;

import io.agora.openvcall.model.AGEventHandler;
import io.agora.openvcall.model.ConstantApp;
import io.agora.propeller.UserStatusData;
import io.agora.propeller.preprocessing.VideoPreProcessing;
import io.agora.rtc.IRtcEngineEventHandler;
import io.agora.rtc.RtcEngine;
import io.agora.rtc.video.VideoCanvas;
import com.lssl.activity.SuneeeApplication;
import com.xiangpu.common.Constants;
import java.util.List;
import org.apache.http.NameValuePair;
import com.xiangpu.utils.WebServiceUtil;
import com.xiangpu.receivers.MyMessageReceiver;

import com.lssl.activity.SuneeeApplication;

public class ChatAudioActivity extends BaseActivity implements AGEventHandler, View.OnClickListener,
        IRecordTimerInterface,WebServiceUtil.OnDataListener{

    private final static Logger log = LoggerFactory.getLogger(ChatAudioActivity.class);

    private RelativeLayout mSmallVideoViewDock;

    // should only be modified under UI thread
    private final HashMap<Integer, SurfaceView> mUidsList = new HashMap<>(); // uid = 0 || uid == EngineConfig.mUid

    private volatile boolean mAudioMuted = false;

    private volatile int mAudioRouting = -1; // Default
    private int video_type;
    private TextView text_Record_id;
    private ImageView call_avatar;

    private String selfUserId;//自己userid
    private String created_roomid = null;//房间号。

    private RecordTime TimeThread = null;

    public void StartRecordTimer() {
        (TimeThread = new RecordTime(0, this)).start();
    }

    public void StopRecordTimer() {
        if (TimeThread != null) {
            TimeThread.stopRecordTime();
        }
    }

    private static ChatAuidoResultCallback chatResultCallback;

    public static void setChatResultCallback(ChatAuidoResultCallback callback) {
        chatResultCallback = callback;
    }

    public interface ChatAuidoResultCallback {
        void onChatSuccess();

        void onChatFaile(String errorMsg);
    }

    private CommonDialog commonDialog = null;
    private ImageView iv_hand_free = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final Window win = getWindow();//自动解锁
        win.addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
                | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD
                | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);

        setContentView(R.layout.activity_chataudio);
        commonDialog = new CommonDialog(this);
        commonDialog.setOnResultListener(new CommonDialog.OnResultListener() {
            @Override
            public void onConfirm() {
                if (chatResultCallback != null) {
                    chatResultCallback.onChatFaile("chat disconnect");
                }
                finish();
            }

            @Override
            public void onCancel() {

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return false;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return false;
    }

    @Override
    protected void initUIandEvent() throws Exception {
        event().addEventHandler(this);

        Intent i = getIntent();
        String channelName = i.getStringExtra(ConstantApp.ACTION_KEY_CHANNEL_NAME);
        final String encryptionKey = getIntent().getStringExtra(ConstantApp.ACTION_KEY_ENCRYPTION_KEY);
        final String encryptionMode = getIntent().getStringExtra(ConstantApp.ACTION_KEY_ENCRYPTION_MODE);
        video_type = getIntent().getIntExtra(ConstantApp.ACTION_KEY_VIDEO_TYPE, 0);
        final String name = getIntent().getStringExtra(ConstantApp.ACTION_KEY_VIDEO_NAME);
        final String avatar = getIntent().getStringExtra(ConstantApp.ACTION_KEY_VIDEO_HEAD);

        selfUserId =  getIntent().getStringExtra(ConstantApp.ACTION_KEY_VIDEO_USERID);
        created_roomid = channelName;

        doConfigEngine(encryptionKey, encryptionMode);

        worker().joinChannel(channelName, config().mUid);

        TextView textChannelName = (TextView) findViewById(R.id.channel_name);
        textChannelName.setText(name);

        call_avatar = (ImageView) findViewById(R.id.call_avatar);

        call_avatar(avatar);

        optional();


        this.findViewById(R.id.bottom_action_end_call).setOnClickListener(ChatAudioActivity.this);
        text_Record_id = (TextView) this.findViewById(R.id.text_Record_id);
        iv_hand_free = (ImageView) findViewById(R.id.iv_hand_free);
        iv_hand_free.setOnClickListener(this);

        registerBroadCast();

        StartRecordTimer();

        SuneeeApplication.getInstance().showNotificationAudioVideo(0);
    }

    public void onClickHideIME(View view) {
        log.debug("onClickHideIME " + view);

    }

    private void call_avatar(final String avatarUrl) {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                try {
                    Bitmap bitmap = Utils.getUrlToImage(avatarUrl);
                    bitmap = BitmapUtils.getRoundImage(bitmap, 360);
                    android.os.Message msg = new android.os.Message();
                    msg.what = 1;
                    msg.obj = bitmap;
                    mHandler.sendMessage(msg);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        new Thread(runnable).start();
    }

    private int mDataStreamId;

    private void sendChannelMsg(String msgStr) {
        RtcEngine rtcEngine = rtcEngine();
        if (mDataStreamId <= 0) {
            mDataStreamId = rtcEngine.createDataStream(true, true); // boolean reliable, boolean ordered
        }

        if (mDataStreamId < 0) {
            String errorMsg = "Create data stream error happened " + mDataStreamId;
            log.warn(errorMsg);
            showLongToast(errorMsg);
            return;
        }

        byte[] encodedMsg;
        try {
            encodedMsg = msgStr.getBytes("UTF-8");
        } catch (UnsupportedEncodingException e) {
            encodedMsg = msgStr.getBytes();
        }

        rtcEngine.sendStreamMessage(mDataStreamId, encodedMsg);
    }

    private void optional() {
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);

        setVolumeControlStream(AudioManager.STREAM_VOICE_CALL);
    }

    private void optionalDestroy() {
    }

    private int getVideoProfileIndex() {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
        int profileIndex = pref.getInt(ConstantApp.PrefManager.PREF_PROPERTY_PROFILE_IDX, ConstantApp.DEFAULT_PROFILE_IDX);
        if (profileIndex > ConstantApp.VIDEO_PROFILES.length - 1) {
            profileIndex = ConstantApp.DEFAULT_PROFILE_IDX;

            // save the new value
            SharedPreferences.Editor editor = pref.edit();
            editor.putInt(ConstantApp.PrefManager.PREF_PROPERTY_PROFILE_IDX, profileIndex);
            editor.apply();
        }
        return profileIndex;
    }

    private void doConfigEngine(String encryptionKey, String encryptionMode) throws Exception {
        int vProfile = ConstantApp.VIDEO_PROFILES[getVideoProfileIndex()];

        worker().configEngine(vProfile, encryptionKey, encryptionMode);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        SuneeeApplication.getInstance().closeNotificationVideo();
        SuneeeApplication.getInstance().closeNotificationVideo(MyMessageReceiver._ALIYUN_NOTIFICATION_ID_);
    }

    @Override
    protected void deInitUIandEvent() {
        optionalDestroy();

        doLeaveChannel();
        event().removeEventHandler(this);

        if (mUidsList != null && !mUidsList.isEmpty()) {
            mUidsList.clear();
        }

        endCallActivity();

    }

    private void doLeaveChannel() {
        worker().leaveChannel(config().mChannel);
        //worker().preview(false, null, 0);
    }


    private VideoPreProcessing mVideoPreProcessing;

    public void onBtnNClicked(View view) {
        if (mVideoPreProcessing == null) {
            mVideoPreProcessing = new VideoPreProcessing();
        }

        ImageView iv = (ImageView) view;
        Object showing = view.getTag();
        if (showing != null && (Boolean) showing) {
            mVideoPreProcessing.enablePreProcessing(false);
            iv.setTag(null);
            iv.clearColorFilter();
        } else {
            mVideoPreProcessing.enablePreProcessing(true);
            iv.setTag(true);
            iv.setColorFilter(getResources().getColor(R.color.agora_blue), PorterDuff.Mode.MULTIPLY);
        }
    }

    private SurfaceView getLocalView() {
        for (HashMap.Entry<Integer, SurfaceView> entry : mUidsList.entrySet()) {
            if (entry.getKey() == 0 || entry.getKey() == config().mUid) {
                return entry.getValue();
            }
        }

        return null;
    }

    private void hideLocalView(boolean hide) {
        int uid = config().mUid;
        doHideTargetView(uid, hide);
    }

    private void doHideTargetView(int targetUid, boolean hide) {
        HashMap<Integer, Integer> status = new HashMap<>();
        status.put(targetUid, hide ? UserStatusData.VIDEO_MUTED : UserStatusData.DEFAULT_STATUS);
    }

    private void resetToVideoEnabledUI() {
        ImageView iv = (ImageView) findViewById(R.id.customized_function_id);
        iv.setImageResource(R.drawable.btn_switch_camera);
        iv.clearColorFilter();

        //   notifyHeadsetPlugged(mAudioRouting);
    }

    private void resetToVideoDisabledUI() {
        ImageView iv = (ImageView) findViewById(R.id.customized_function_id);
        iv.setImageResource(R.drawable.btn_speaker);
        iv.clearColorFilter();

        // notifyHeadsetPlugged(mAudioRouting);
    }

    @Override
    public void onFirstRemoteVideoDecoded(int uid, int width, int height, int elapsed) {
        // doRenderRemoteUi(uid);
    }

    private void doRenderRemoteUi(final int uid) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (isFinishing()) {
                    return;
                }

                if (mUidsList.containsKey(uid)) {
                    return;
                }

                SurfaceView surfaceV = RtcEngine.CreateRendererView(getApplicationContext());
                mUidsList.put(uid, surfaceV);


                boolean useDefaultLayout = mLayoutType == LAYOUT_TYPE_DEFAULT && mUidsList.size() != 2;

                surfaceV.setZOrderOnTop(!useDefaultLayout);
                surfaceV.setZOrderMediaOverlay(!useDefaultLayout);

                rtcEngine().setupRemoteVideo(new VideoCanvas(surfaceV, VideoCanvas.RENDER_MODE_HIDDEN, uid));

                if (useDefaultLayout) {
                    log.debug("doRenderRemoteUi LAYOUT_TYPE_DEFAULT " + (uid & 0xFFFFFFFFL));
                    switchToDefaultVideoView();
                } else {
                    int bigBgUid = mSmallVideoViewAdapter == null ? uid : mSmallVideoViewAdapter.getExceptedUid();
                    log.debug("doRenderRemoteUi LAYOUT_TYPE_SMALL " + (uid & 0xFFFFFFFFL) + " " + (bigBgUid & 0xFFFFFFFFL));

                    switchToSmallVideoView(bigBgUid);
                }
            }
        });
    }

    @Override
    public void onJoinChannelSuccess(String channel, final int uid, int elapsed) {
        log.debug("onJoinChannelSuccess " + channel + " " + (uid & 0xFFFFFFFFL) + " " + elapsed);

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (isFinishing()) {
                    return;
                }

                SurfaceView local = mUidsList.remove(0);

                if (local == null) {
                    return;
                }

                mUidsList.put(uid, local);
            }
        });
    }

    @Override
    public void onUserOffline(int uid, int reason) {
        doRemoveRemoteUi(uid);
    }

    @Override
    public void onExtraCallback(final int type, final Object... data) {

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (isFinishing()) {
                    return;
                }

                doHandleExtraCallback(type, data);
            }
        });
    }

    private void doHandleExtraCallback(int type, Object... data) {
        int peerUid;
        boolean muted;

        switch (type) {
            case AGEventHandler.EVENT_TYPE_ON_USER_AUDIO_MUTED:
                peerUid = (Integer) data[0];
                muted = (boolean) data[1];

                if (mLayoutType == LAYOUT_TYPE_DEFAULT) {
                    HashMap<Integer, Integer> status = new HashMap<>();
                    status.put(peerUid, muted ? UserStatusData.AUDIO_MUTED : UserStatusData.DEFAULT_STATUS);
                }

                break;

            case AGEventHandler.EVENT_TYPE_ON_USER_VIDEO_MUTED:
                peerUid = (Integer) data[0];
                muted = (boolean) data[1];

                doHideTargetView(peerUid, muted);

                break;

            case AGEventHandler.EVENT_TYPE_ON_USER_VIDEO_STATS:
                IRtcEngineEventHandler.RemoteVideoStats stats = (IRtcEngineEventHandler.RemoteVideoStats) data[0];

                if (ConstantApp.SHOW_VIDEO_INFO) {
                    if (mLayoutType == LAYOUT_TYPE_DEFAULT) {
                        int uid = config().mUid;
                        int profileIndex = getVideoProfileIndex();
                        String resolution = getResources().getStringArray(R.array.string_array_resolutions)[profileIndex];
                        String fps = getResources().getStringArray(R.array.string_array_frame_rate)[profileIndex];
                        String bitrate = getResources().getStringArray(R.array.string_array_bit_rate)[profileIndex];

                        String[] rwh = resolution.split("x");
                        int width = Integer.valueOf(rwh[0]);
                        int height = Integer.valueOf(rwh[1]);

                    }
                }

                break;

            case AGEventHandler.EVENT_TYPE_ON_SPEAKER_STATS:
                IRtcEngineEventHandler.AudioVolumeInfo[] infos = (IRtcEngineEventHandler.AudioVolumeInfo[]) data[0];

                if (infos.length == 1 && infos[0].uid == 0) { // local guy, ignore it
                    break;
                }

                if (mLayoutType == LAYOUT_TYPE_DEFAULT) {
                    HashMap<Integer, Integer> volume = new HashMap<>();

                    for (IRtcEngineEventHandler.AudioVolumeInfo each : infos) {
                        peerUid = each.uid;
                        int peerVolume = each.volume;

                        if (peerUid == 0) {
                            continue;
                        }
                        volume.put(peerUid, peerVolume);
                    }

                }

                break;

            case AGEventHandler.EVENT_TYPE_ON_APP_ERROR:
                int subType = (int) data[0];

                if (subType == ConstantApp.AppError.NO_NETWORK_CONNECTION) {
                    showLongToast(getString(R.string.msg_no_network_connection));
                    endCallActivity();
                }

                break;

            case AGEventHandler.EVENT_TYPE_ON_DATA_CHANNEL_MSG:

                peerUid = (Integer) data[0];
                final byte[] content = (byte[]) data[1];
                // notifyMessageChanged(new Message(new User(peerUid, String.valueOf(peerUid)), new String(content)));

                break;

            case AGEventHandler.EVENT_TYPE_ON_AGORA_MEDIA_ERROR: {
                int error = (int) data[0];
                String description = (String) data[1];

                //notifyMessageChanged(new Message(new User(0, null), error + " " + description));

                break;
            }

            case AGEventHandler.EVENT_TYPE_ON_AUDIO_ROUTE_CHANGED:
                //notifyHeadsetPlugged((int) data[0]);

                break;

        }
    }

    private void requestRemoteStreamType(final int currentHostCount) {
        log.debug("requestRemoteStreamType " + currentHostCount);
    }

    private void doRemoveRemoteUi(final int uid) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (isFinishing()) {
                    SuneeeApplication.getInstance().closeNotificationVideo();
                    return;
                }

                Object target = mUidsList.remove(uid);
                if (target == null) {
                    deInitUIandEvent();
                    return;
                }

                if (mUidsList.isEmpty() || mUidsList.size() == 1) {
                    deInitUIandEvent();
                    return;
                }

                int bigBgUid = -1;
                if (mSmallVideoViewAdapter != null) {
                    bigBgUid = mSmallVideoViewAdapter.getExceptedUid();
                }

                log.debug("doRemoveRemoteUi " + (uid & 0xFFFFFFFFL) + " " + (bigBgUid & 0xFFFFFFFFL) + " " + mLayoutType);

                if (mLayoutType == LAYOUT_TYPE_DEFAULT || uid == bigBgUid) {
                    switchToDefaultVideoView();
                } else {
                    switchToSmallVideoView(bigBgUid);
                }
            }
        });
    }

    private SmallVideoViewAdapter mSmallVideoViewAdapter;

    private void switchToDefaultVideoView() {
        if (mSmallVideoViewDock != null) {
            mSmallVideoViewDock.setVisibility(View.GONE);
        }

        mLayoutType = LAYOUT_TYPE_DEFAULT;


    }

    private void switchToSmallVideoView(int bigBgUid) {
        HashMap<Integer, SurfaceView> slice = new HashMap<>(1);
        slice.put(bigBgUid, mUidsList.get(bigBgUid));

        bindToSmallVideoView(bigBgUid);

        mLayoutType = LAYOUT_TYPE_SMALL;

        requestRemoteStreamType(mUidsList.size());
    }

    public int mLayoutType = LAYOUT_TYPE_DEFAULT;

    public static final int LAYOUT_TYPE_DEFAULT = 0;

    public static final int LAYOUT_TYPE_SMALL = 1;

    private void bindToSmallVideoView(final int exceptUid) {
        if (mSmallVideoViewDock == null) {
            ViewStub stub = (ViewStub) findViewById(R.id.small_video_view_dock);
            mSmallVideoViewDock = (RelativeLayout) stub.inflate();
        }

        boolean twoWayVideoCall = mUidsList.size() == 2;

        boolean create = false;

        if (mSmallVideoViewAdapter == null) {
            create = true;
            mUidsList.remove(config().mUid);
            mSmallVideoViewAdapter = new SmallVideoViewAdapter(this, config().mUid, exceptUid, mUidsList, new VideoViewEventListener() {
                @Override
                public void onItemDoubleClick(View v, Object item) {
                    //switchToDefaultVideoView();

                    mUidsList.remove(exceptUid);
                    SurfaceView surfaceV = RtcEngine.CreateRendererView(getApplicationContext());
                    mUidsList.put(config().mUid, surfaceV);
                    boolean useDefaultLayout = mLayoutType == LAYOUT_TYPE_DEFAULT && mUidsList.size() != 2;
                    surfaceV.setZOrderOnTop(!useDefaultLayout);
                    surfaceV.setZOrderMediaOverlay(!useDefaultLayout);


                    mSmallVideoViewAdapter.notifyDataSetChanged();
                }
            });
            mSmallVideoViewAdapter.setHasStableIds(true);
        }

        log.debug("bindToSmallVideoView " + twoWayVideoCall + " " + (exceptUid & 0xFFFFFFFFL));

    }

    private boolean speek = true;
    private boolean voiceShowBg = true;

    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        switch (v.getId()) {
            case R.id.bottom_action_end_call:
                // 挂断
                endCallActivity();
                break;
            case R.id.iv_hand_free:
                // 免提
                RtcEngine rtcEngine = rtcEngine();
                rtcEngine.setEnableSpeakerphone(speek);

                if (speek) {
                    iv_hand_free.setBackgroundResource(R.drawable.skin_icon_video_select);
                } else {
                    iv_hand_free.setBackgroundResource(R.drawable.skin_icon_video_hf_jump);
                }
                speek = !speek;
                break;
        }
    }

    public void endCallActivity(){
        if (chatResultCallback != null) {
            chatResultCallback.onChatFaile("chat disconnect");
        }
        hangUp();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // 屏蔽返回键
        if (keyCode == event.KEYCODE_BACK) {
            // 捕获按下返回键事件
            commonDialog.show();
            commonDialog.setTvContent("是否结束此次音频？");
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        unregisterReceiver(chatBroadCast);
        StopRecordTimer();
        SuneeeApplication.getInstance().closeNotificationVideo();
        SuneeeApplication.getInstance().closeNotificationVideo(MyMessageReceiver._ALIYUN_NOTIFICATION_ID_);
    }

    @Override
    public void onRecordTime(int h, int m, int s) {

    }

    @Override
    public void onRecordTime(String value) {
        android.os.Message msg = new android.os.Message();
        msg.what = 0;
        msg.obj = value.toString();
        mHandler.sendMessage(msg);
    }

    final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(android.os.Message msg) {
            super.handleMessage(msg);

            if (msg.what == 1) {
                Bitmap bitmap = (Bitmap) msg.obj;
                if (bitmap != null) {
                    //call_avatar.setBackground(Utils.BmpToDraw(bitmap));
                    call_avatar.setImageBitmap(bitmap);
                } else {
                    call_avatar.setBackgroundResource(R.drawable.lssl_person);
                }
            } else if (msg.what == 0) {
                if (msg.obj != null) {
                    text_Record_id.setText(msg.obj.toString());
                }
            }
        }
    };

    private MyChatBroadCast chatBroadCast;

    private void registerBroadCast() {

        chatBroadCast = new MyChatBroadCast();
        IntentFilter filter = new IntentFilter();
        filter.addAction("closeChatWnd");
        filter.addAction("disNetworkConnected");
        registerReceiver(chatBroadCast, filter);
    }

    private class MyChatBroadCast extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            //String action = intent.getAction();

            String action = intent.getAction();

            if ("closeChatWnd".equals(action)) {
                if (chatResultCallback != null) {
                    chatResultCallback.onChatFaile("chat disconnect");
                }
                ChatAudioActivity.this.finish();
            }else if("disNetworkConnected".equals(action)){
                ChatAudioActivity.this.finish();
            }
        }
    }

    private void hangUp(){//挂断会议
        WebServiceUtil.request(Constants.VIDEO_AUDIO_HANG_UP, "json", this);
    }

    @Override
    public List<NameValuePair> onGetParamData(String mode) {
        return null;
    }

    @Override
    public String onGetParamDataString(String mode) {
        JSONObject json = new JSONObject();

        if (mode == null) {
            return json.toString();
        }

        if(mode != null && mode.equals(Constants.VIDEO_AUDIO_HANG_UP)){//挂断会议
            try {

                json.put("userId", selfUserId);
                json.put("meetNumber", created_roomid);
                json.put("numbers", 0);

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        return json.toString().replace("\\/", "/").replace("\\n", "");
    }

    @Override
    public void onReceivedData(String mode, JSONObject result) {

        if (result == null) {
            return;
        }else
        {
            if(mode != null && mode.equals(Constants.VIDEO_AUDIO_HANG_UP)){
                this.finish();
            }
        }
    }
}
