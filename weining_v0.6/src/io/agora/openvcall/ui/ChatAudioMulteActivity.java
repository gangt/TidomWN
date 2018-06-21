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
import android.os.Message;
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
import com.xiangpu.bean.ChatUserInfo;
import com.xiangpu.bean.IRecordTimerInterface;
import com.xiangpu.bean.RecordTime;
import com.xiangpu.utils.BitmapUtils;
import com.xiangpu.utils.NpcCommon;
import com.xiangpu.utils.Utils;

import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;

import io.agora.openvcall.model.AGEventHandler;
import io.agora.openvcall.model.ConstantApp;
import io.agora.propeller.UserStatusData;
import io.agora.propeller.preprocessing.VideoPreProcessing;
import io.agora.rtc.IRtcEngineEventHandler;
import io.agora.rtc.RtcEngine;
import io.agora.rtc.video.VideoCanvas;
import com.xiangpu.receivers.MyMessageReceiver;
import com.lssl.activity.SuneeeApplication;
import com.xiangpu.common.Constants;
import java.util.List;
import org.apache.http.NameValuePair;
import com.xiangpu.utils.WebServiceUtil;
import com.xiangpu.receivers.MyMessageReceiver;


public class ChatAudioMulteActivity extends BaseActivity implements AGEventHandler,View.OnClickListener,
        IRecordTimerInterface,WebServiceUtil.OnDataListener {

    private final static Logger log = LoggerFactory.getLogger(ChatAudioMulteActivity.class);

    private RelativeLayout mSmallVideoViewDock;

    // should only be modified under UI thread
    private final HashMap<Integer, SurfaceView> mUidsList = new HashMap<>(); // uid = 0 || uid == EngineConfig.mUid

    private volatile boolean mAudioMuted = false;

    private volatile int mAudioRouting = -1; // Default
    private int video_type;

    private TextView text_Record_id;
    //private ImageView call_avatar;

    public ArrayList<ChatUserInfo> userInfoList = null;

    private TextView call_name1;
    private ImageView call_avatar1;

    private TextView call_name2;
    private ImageView call_avatar2;

    private TextView call_name3;
    private ImageView call_avatar3;

    private TextView call_name4;
    private ImageView call_avatar4;

    private TextView call_name5;
    private ImageView call_avatar5;

    private TextView call_name6;
    private ImageView call_avatar6;
    private ImageView iv_hand_free = null;

    private String selfUserId;//自己userid
    private String created_roomid = null;//房间号。

    private RecordTime TimeThread=null;
    public void StartRecordTimer(){
        (TimeThread = new RecordTime(0,this)).start();
    }
    public void StopRecordTimer(){
        if(TimeThread!=null){
            TimeThread.stopRecordTime();
        }
    }

    private static ChatAuidoMulteResultCallback chatResultCallback;
	public static void setChatMulteResultCallback(ChatAuidoMulteResultCallback callback){
		chatResultCallback = callback;
	}
	public interface ChatAuidoMulteResultCallback {
	    //void onChatSuccess(String message);
	    void onChatFaile(String errorMsg);
	}
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final Window win = getWindow();//自动解锁
        win.addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
                | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD
                | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);

        setContentView(R.layout.activity_chataudiomulte);
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
        video_type = getIntent().getIntExtra(ConstantApp.ACTION_KEY_VIDEO_TYPE,0);

        selfUserId =  getIntent().getStringExtra(ConstantApp.ACTION_KEY_VIDEO_USERID);
        created_roomid = channelName;

        userInfoList = this.getIntent().getParcelableArrayListExtra("chatList");

        doConfigEngine(encryptionKey, encryptionMode);

        worker().joinChannel(channelName, config().mUid);

        optional();


        this.findViewById(R.id.bottom_action_end_call).setOnClickListener(ChatAudioMulteActivity.this);
        text_Record_id = (TextView)this.findViewById(R.id.text_Record_id);
        iv_hand_free = (ImageView) findViewById(R.id.iv_hand_free);
        iv_hand_free.setOnClickListener(this);

        initview();
        registerBroadCast();
        StartRecordTimer();
    }

    public void onClickHideIME(View view) {
        log.debug("onClickHideIME " + view);
    }

    private void call_avatar(final String avatarUrl,final int pos){
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                try {
                    Bitmap bitmap = Utils.getUrlToImage(avatarUrl);
                    bitmap = BitmapUtils.getRoundImage(bitmap, 360);
                    Message msg = new Message();
                    msg.what = 1;
                    msg.arg1 = pos;
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
    protected void deInitUIandEvent() {
        optionalDestroy();

        doLeaveChannel();
        event().removeEventHandler(this);

        if(mUidsList!=null && !mUidsList.isEmpty()){
        	mUidsList.clear();
        }
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

        if(userInfoList.size() <= 2){
            if(chatResultCallback!=null){
                chatResultCallback.onChatFaile("chat disconnect");
            }
            this.finish();
        }
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
                    return;
                }

                Object target = mUidsList.remove(uid);
                if (target == null) {
                    return;
                }

                if(mUidsList.isEmpty()){
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

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch(v.getId()){
		    case R.id.bottom_action_end_call:
                // 挂断
                if(chatResultCallback!=null){
                    chatResultCallback.onChatFaile("chat disconnect");
                }
                hangUp();

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

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        return true;
    }

    @Override
    public void onDestroy(){
        super.onDestroy();

        unregisterReceiver(chatBroadCast);
        StopRecordTimer();
        SuneeeApplication.getInstance().closeNotificationVideo(MyMessageReceiver._ALIYUN_NOTIFICATION_ID_);
    }

    @Override
    public void onRecordTime(int h,int m,int s){

    }
    @Override
    public void onRecordTime(String value){
        Message  msg = new Message();
        msg.what = 0;
        msg.obj = value.toString();
        mHandler.sendMessage(msg);
    }

    final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 1) {
                Bitmap bitmap = (Bitmap) msg.obj;

                if(msg.arg1 == 0){
                    if(bitmap != null){
                        call_avatar1.setImageBitmap(bitmap);
                    }else{
                        call_avatar1.setBackgroundResource(R.drawable.lssl_person);
                    }

                }else if(msg.arg1 == 1){
                    if(bitmap != null){
                        call_avatar2.setImageBitmap(bitmap);
                    }else{
                        call_avatar2.setBackgroundResource(R.drawable.lssl_person);
                    }

                }else if(msg.arg1 == 2){
                    if(bitmap != null){
                        call_avatar3.setImageBitmap(bitmap);
                    }else{
                        call_avatar3.setBackgroundResource(R.drawable.lssl_person);
                    }

                }else if(msg.arg1 == 3){
                    if(bitmap != null){
                        call_avatar4.setImageBitmap(bitmap);
                    }else{
                        call_avatar4.setBackgroundResource(R.drawable.lssl_person);
                    }

                }else if(msg.arg1 == 4){
                    if(bitmap != null){
                        call_avatar5.setImageBitmap(bitmap);
                    }else{
                        call_avatar5.setBackgroundResource(R.drawable.lssl_person);
                    }

                }else if(msg.arg1 == 5){
                    if(bitmap != null){
                        call_avatar6.setImageBitmap(bitmap);
                    }else{
                        call_avatar6.setBackgroundResource(R.drawable.lssl_person);
                    }

                }

            }else if(msg.what == 2){

                //Intent intent = new Intent();
                //intent.setAction("closeInvitationWndTime");
                //ViedoGroupInviteActivity.this.sendBroadcast(intent);
            }else  if(msg.what == 0){
                if(msg.obj != null){
                    text_Record_id.setText(msg.obj.toString());
                }
            }
        }
    };

    private MyChatBroadCast chatBroadCast;
    private void registerBroadCast(){

        chatBroadCast = new MyChatBroadCast();
        IntentFilter filter = new IntentFilter();
        filter.addAction("closeChatWnd");
        filter.addAction("updataUserList");
        filter.addAction("disNetworkConnected");
        registerReceiver(chatBroadCast, filter);
    }

    private class MyChatBroadCast extends BroadcastReceiver
    {

        @Override
        public void onReceive(Context context, Intent intent) {
            //String action = intent.getAction();

            String action = intent.getAction();

            if("closeChatWnd".equals(action)){
                if(chatResultCallback!=null){
                    chatResultCallback.onChatFaile("chat disconnect");
                }
                ChatAudioMulteActivity.this.finish();

            }else if("updataUserList".equals(action))
            {
                userInfoList = intent.getParcelableArrayListExtra("chatList");
                showHeader();
            }else if("disNetworkConnected".equals(action)){
                ChatAudioMulteActivity.this.finish();
            }
        }
    }

    private void initview() {


        call_name1  = (TextView) this.findViewById(R.id.call_name1);
        call_avatar1 = (ImageView) this.findViewById(R.id.call_avatar1);

        call_name2 = (TextView) this.findViewById(R.id.call_name2);
        call_avatar2 = (ImageView) this.findViewById(R.id.call_avatar2);

        call_name3 = (TextView) this.findViewById(R.id.call_name3);
        call_avatar3 = (ImageView) this.findViewById(R.id.call_avatar3);

        call_name4 = (TextView) this.findViewById(R.id.call_name4);
        call_avatar4 = (ImageView) this.findViewById(R.id.call_avatar4);

        call_name5 = (TextView) this.findViewById(R.id.call_name5);
        call_avatar5 = (ImageView) this.findViewById(R.id.call_avatar5);

        call_name6 = (TextView) this.findViewById(R.id.call_name6);
        call_avatar6 = (ImageView) this.findViewById(R.id.call_avatar6);

        TextView txt_network_id = (TextView) this.findViewById(R.id.txt_network_id);

        if(NpcCommon.verifyNetwork(this)){
            if(NpcCommon.mNetWorkType == NpcCommon.NETWORK_TYPE.NETWORK_WIFI){
                txt_network_id.setText("您正在使用WIFI网络，通话完全免费！");
            }else{
                txt_network_id.setText("您正在使用手机网络，通话会产生流量费用！");
            }
        }

        showHeader();
    }

    private void showHeader(){

        if(!userInfoList.isEmpty()) {
            switch (userInfoList.size()) {
                case 6:
                    call_avatar1.setVisibility(View.VISIBLE);
                    call_avatar2.setVisibility(View.VISIBLE);
                    call_avatar3.setVisibility(View.VISIBLE);
                    call_avatar4.setVisibility(View.VISIBLE);
                    call_avatar5.setVisibility(View.VISIBLE);
                    call_avatar6.setVisibility(View.VISIBLE);

                    call_name1.setVisibility(View.VISIBLE);
                    call_name2.setVisibility(View.VISIBLE);
                    call_name3.setVisibility(View.VISIBLE);
                    call_name4.setVisibility(View.VISIBLE);
                    call_name5.setVisibility(View.VISIBLE);
                    call_name6.setVisibility(View.VISIBLE);

                    call_name1.setText(userInfoList.get(0).name);
                    call_avatar(userInfoList.get(0).avatar, 0);

                    call_name2.setText(userInfoList.get(1).name);
                    call_avatar(userInfoList.get(1).avatar, 1);

                    call_name3.setText(userInfoList.get(2).name);
                    call_avatar(userInfoList.get(2).avatar, 2);

                    call_name4.setText(userInfoList.get(3).name);
                    call_avatar(userInfoList.get(3).avatar, 3);

                    call_name5.setText(userInfoList.get(4).name);
                    call_avatar(userInfoList.get(4).avatar, 4);

                    call_name6.setText(userInfoList.get(5).name);
                    call_avatar(userInfoList.get(5).avatar, 5);
                    break;
                case 5:
                    call_avatar1.setVisibility(View.VISIBLE);
                    call_avatar2.setVisibility(View.VISIBLE);
                    call_avatar3.setVisibility(View.VISIBLE);
                    call_avatar4.setVisibility(View.VISIBLE);
                    call_avatar5.setVisibility(View.VISIBLE);
                    call_avatar6.setVisibility(View.GONE);

                    call_name1.setVisibility(View.VISIBLE);
                    call_name2.setVisibility(View.VISIBLE);
                    call_name3.setVisibility(View.VISIBLE);
                    call_name4.setVisibility(View.VISIBLE);
                    call_name5.setVisibility(View.VISIBLE);
                    call_name6.setVisibility(View.GONE);


                    call_name1.setText(userInfoList.get(0).name);
                    call_avatar(userInfoList.get(0).avatar, 0);

                    call_name2.setText(userInfoList.get(1).name);
                    call_avatar(userInfoList.get(1).avatar, 1);

                    call_name3.setText(userInfoList.get(2).name);
                    call_avatar(userInfoList.get(2).avatar, 2);

                    call_name4.setText(userInfoList.get(3).name);
                    call_avatar(userInfoList.get(3).avatar, 3);

                    call_name5.setText(userInfoList.get(4).name);
                    call_avatar(userInfoList.get(4).avatar, 4);
                    break;
                case 4:
                    call_avatar1.setVisibility(View.VISIBLE);
                    call_avatar2.setVisibility(View.VISIBLE);
                    call_avatar3.setVisibility(View.VISIBLE);
                    call_avatar4.setVisibility(View.VISIBLE);
                    call_avatar5.setVisibility(View.GONE);
                    call_avatar6.setVisibility(View.GONE);

                    call_name1.setVisibility(View.VISIBLE);
                    call_name2.setVisibility(View.VISIBLE);
                    call_name3.setVisibility(View.VISIBLE);
                    call_name4.setVisibility(View.VISIBLE);
                    call_name5.setVisibility(View.GONE);
                    call_name6.setVisibility(View.GONE);

                    call_name1.setText(userInfoList.get(0).name);
                    call_avatar(userInfoList.get(0).avatar, 0);

                    call_name2.setText(userInfoList.get(1).name);
                    call_avatar(userInfoList.get(1).avatar, 1);

                    call_name3.setText(userInfoList.get(2).name);
                    call_avatar(userInfoList.get(2).avatar, 2);

                    call_name4.setText(userInfoList.get(3).name);
                    call_avatar(userInfoList.get(3).avatar, 3);

                    break;
                case 3: {
                    call_avatar1.setVisibility(View.VISIBLE);
                    call_avatar2.setVisibility(View.VISIBLE);
                    call_avatar3.setVisibility(View.VISIBLE);
                    call_avatar4.setVisibility(View.GONE);
                    call_avatar5.setVisibility(View.GONE);
                    call_avatar6.setVisibility(View.GONE);

                    call_name1.setVisibility(View.VISIBLE);
                    call_name2.setVisibility(View.VISIBLE);
                    call_name3.setVisibility(View.VISIBLE);
                    call_name4.setVisibility(View.GONE);
                    call_name5.setVisibility(View.GONE);
                    call_name6.setVisibility(View.GONE);

                    call_name1.setText(userInfoList.get(0).name);
                    call_avatar(userInfoList.get(0).avatar, 0);

                    call_name2.setText(userInfoList.get(1).name);
                    call_avatar(userInfoList.get(1).avatar, 1);

                    call_name3.setText(userInfoList.get(2).name);
                    call_avatar(userInfoList.get(2).avatar, 2);
                }
                break;
                case 2: {
                    call_avatar1.setVisibility(View.VISIBLE);
                    call_avatar2.setVisibility(View.VISIBLE);
                    call_avatar3.setVisibility(View.GONE);
                    call_avatar4.setVisibility(View.GONE);
                    call_avatar5.setVisibility(View.GONE);
                    call_avatar6.setVisibility(View.GONE);

                    call_name1.setVisibility(View.VISIBLE);
                    call_name2.setVisibility(View.VISIBLE);
                    call_name3.setVisibility(View.GONE);
                    call_name4.setVisibility(View.GONE);
                    call_name5.setVisibility(View.GONE);
                    call_name6.setVisibility(View.GONE);

                    call_name1.setText(userInfoList.get(0).name);
                    call_avatar(userInfoList.get(0).avatar, 0);

                    call_name2.setText(userInfoList.get(1).name);
                    call_avatar(userInfoList.get(1).avatar, 1);
                }
                break;
                case 1: {
                    call_avatar1.setVisibility(View.VISIBLE);
                    call_avatar2.setVisibility(View.GONE);
                    call_avatar3.setVisibility(View.GONE);
                    call_avatar4.setVisibility(View.GONE);
                    call_avatar5.setVisibility(View.GONE);
                    call_avatar6.setVisibility(View.GONE);

                    call_name1.setVisibility(View.VISIBLE);
                    call_name2.setVisibility(View.GONE);
                    call_name3.setVisibility(View.GONE);
                    call_name4.setVisibility(View.GONE);
                    call_name5.setVisibility(View.GONE);
                    call_name6.setVisibility(View.GONE);

                    call_name1.setText(userInfoList.get(0).name);
                    call_avatar(userInfoList.get(0).avatar, 0);
                }
                break;
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
                json.put("numbers", (userInfoList.size() > 0 ? userInfoList.size()-1:0) );

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
