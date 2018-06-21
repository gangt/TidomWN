package com.xiangpu.activity;

import io.agora.openvcall.model.ConstantApp;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.konecty.rocket.chat.R;
import com.lssl.activity.SuneeeApplication;
import com.xiangpu.utils.BitmapUtils;
import com.xiangpu.utils.NetWorkUtils;
import com.xiangpu.utils.Utils;

import android.os.PowerManager;
import android.os.PowerManager.WakeLock;

import android.view.Window;
import android.view.WindowManager;

import org.json.JSONException;
import org.json.JSONObject;

import com.xiangpu.common.Constants;
import java.util.Map;
import java.util.List;
import org.apache.http.NameValuePair;
import com.xiangpu.utils.WebServiceUtil;
import com.xiangpu.utils.ToastUtils;
import android.net.ConnectivityManager;
import com.xiangpu.receivers.MyMessageReceiver;
import com.lssl.activity.SuneeeApplication;
/**
 * 邀請界面
 * @author chengang
 *
 */
public class ViedoInviteActivity extends Activity implements WebServiceUtil.OnDataListener{
	private ImageButton call_reject_btn;
	private ImageButton call_receive_btn;
	private ImageButton call_reject_btn_invite;
	
	private LinearLayout call_receive_layout;
	
	private TextView call_name;
	private ImageView call_avatar;

	private TextView chronometerDetail;
	
	private String channelName;
	private String encryptionKey;
	private String encryptionMode;
	private String video_type;
	
	private String avatar;
	private String name;
	private String userId;
	private String created_roomid = null;//房间号。
	private String callback = null;

	private WaitTime waitTimer = null;
	private MyInViTationBroadCast InvitationBroadCast;

	private WakeLock wakeLock;


	private static InviteResultCallback inviteResultCallback;
	public static void setInviteResultCallback(InviteResultCallback callback){
		inviteResultCallback = callback;
	}
	public interface InviteResultCallback {
	    void onInviteSuccess(String successMsg);
	    void onInviteFaile(String errorMsg);
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

		final Window win = getWindow();//自动解锁
		win.addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
				| WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD
				| WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
				| WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);

		setContentView(R.layout.im_view_ccp_call_interface_layout);
		initview();
		initonclick();

		initNetWork();
		
		registerBroadCast();

	}

	private void initNetWork() {
		int state = NetWorkUtils.getAPNType(this);
		switch (state) {
			case 0:
				chronometerDetail.setText("没有网络");
				break;

			case 1:
				chronometerDetail.setText("你正在使用WiFi网络");
				break;

			case 2:
				chronometerDetail.setText("你正在使用手机网络");
				break;

			case 3:
				chronometerDetail.setText("你正在使用手机网络");
				break;

			case 4:
				chronometerDetail.setText("你正在使用手机网络");
				break;

			default:
				break;
		}
	}

	private void initview() {

		call_name = (TextView) this.findViewById(R.id.call_name);
		call_avatar= (ImageView) this.findViewById(R.id.call_avatar);

		chronometerDetail = (TextView) findViewById(R.id.chronometer_detail);

		call_receive_layout = (LinearLayout) this.findViewById(R.id.call_receive_layout);
		
		call_reject_btn = (ImageButton) this.findViewById(R.id.call_reject_btn);
		call_receive_btn = (ImageButton) this
				.findViewById(R.id.call_receive_btn);
		
		call_reject_btn_invite = (ImageButton) this
				.findViewById(R.id.call_reject_btn_invite);

		channelName = getIntent().getStringExtra(
				ConstantApp.ACTION_KEY_CHANNEL_NAME);

		encryptionKey = getIntent().getStringExtra(
				ConstantApp.ACTION_KEY_ENCRYPTION_KEY);

		encryptionMode = getIntent().getStringExtra(
				ConstantApp.ACTION_KEY_ENCRYPTION_MODE);
		
		video_type  = getIntent().getStringExtra(
				ConstantApp.ACTION_KEY_VIDEO_TYPE);

		avatar  = getIntent().getStringExtra(
				"avatar");
		
		name    = getIntent().getStringExtra(
				"name");

		userId  = getIntent().getStringExtra(
				"userId");

		callback =  getIntent().getStringExtra(
				"callback");

		created_roomid  = channelName;
		
		call_name.setText(name);
		call_avatar(avatar);
		
		
		if(video_type.equals("3")){
			call_receive_layout.setVisibility(View.GONE);
		}else if(video_type.equals("4")){
			
		}

		waitTimer = new WaitTime();
		waitTimer.start();

		SuneeeApplication.getInstance().playSounde(0);

		//isMeeting();
	}

	final Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			if (msg.what == 1) {
				Bitmap bitmap = (Bitmap) msg.obj;
				if (bitmap != null) {
					//call_avatar.setBackground(Utils.BmpToDraw(bitmap));
					call_avatar.setImageBitmap(bitmap);
				} else {
					call_avatar.setBackgroundResource(R.drawable.lssl_person);
				}
			}else if(msg.what == 2){

				//Intent intent = new Intent();
				//intent.setAction("closeInvitationWndTime");
				//ViedoInviteActivity.this.sendBroadcast(intent);

				if(callback != null){
					onFaile(1);
				}else{
					if(inviteResultCallback!=null){
						inviteResultCallback.onInviteFaile("超时");
					}
				}
				hangUp();

				//ViedoInviteActivity.this.finish();
			}
		}
	};

	private void call_avatar(final String avatarUrl){
			Runnable runnable = new Runnable() {
				@Override
				public void run() {
					try {
						Bitmap bitmap = Utils.getUrlToImage(avatarUrl);
						bitmap = BitmapUtils.getRoundImage(bitmap, 360);
						Message msg = new Message();
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
	
	private void initonclick() {

		call_reject_btn.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {

				if(NetWorkUtils.isNetworkConnected(ViedoInviteActivity.this)) {//没网络。
					if(callback != null){
						onFaile(0);
					}else{
						if(inviteResultCallback!=null){
							inviteResultCallback.onInviteFaile("video reject");
						}
					}
					hangUp();
				}else{
					if(inviteResultCallback!=null){
						inviteResultCallback.onInviteFaile("video reject");
					}
					finish();
				}
			}
		});

		call_receive_btn.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				if(NetWorkUtils.isNetworkConnected(ViedoInviteActivity.this)){//没网络。
					if(inviteResultCallback != null){

						if(callback != null){
							try{
								JSONObject result = new JSONObject();
								result.put("av_type",video_type);
								result.put("data",callback);

								inviteResultCallback.onInviteSuccess(result.toString());

							}catch (JSONException e) {
								e.printStackTrace();
							}

						}else{
							inviteResultCallback.onInviteSuccess(null);
						}
					}
					finish();
				}else{
					ToastUtils.showCenterToast(ViedoInviteActivity.this,""+String.valueOf("当前无网络！"));
				}
			}
		});

		call_reject_btn_invite.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				if(callback != null){
					onFaile(0);
				}else{
					if(inviteResultCallback!=null){
						inviteResultCallback.onInviteFaile("video reject");
					}
				}

				hangUp();
			}
		});

	}

	public void onFaile(int error){
		try{

			String errorMessage = "";

			switch(error){
				case 0: {//正常挂断
					JSONObject result = new JSONObject();
					result.put("av_type", video_type);
					result.put("data", callback);
					result.put("errorcode","video reject");

					errorMessage = result.toString();
				}break;
				case 1:{//超时

					JSONObject result = new JSONObject();
					result.put("av_type", video_type);
					result.put("data", callback);
					result.put("errorcode","超时");

					errorMessage = result.toString();

				}break;
				default:break;
			}

			if(inviteResultCallback!=null){
				inviteResultCallback.onInviteFaile(errorMessage);
			}

		}catch (JSONException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onDestroy(){
		super.onDestroy();

		unregisterReceiver(InvitationBroadCast);

		if(waitTimer != null){
			waitTimer.stopRecordTime();
		}

		SuneeeApplication.getInstance().stopSound();
		SuneeeApplication.getInstance().closeNotificationVideo(MyMessageReceiver._ALIYUN_NOTIFICATION_ID_);
	}

	@Override
	public void onResume(){
		PowerManager pm = (PowerManager) getSystemService(POWER_SERVICE);
		wakeLock = pm.newWakeLock(PowerManager.SCREEN_BRIGHT_WAKE_LOCK
				| PowerManager.ON_AFTER_RELEASE, "DPA");

		wakeLock.acquire();

		super.onResume();
	}

	@Override
	protected void onPause() {
		if (wakeLock != null) {
			wakeLock.release();
		}
		super.onPause();
	}

	private void registerBroadCast(){
		InvitationBroadCast = new MyInViTationBroadCast();
		IntentFilter filter = new IntentFilter();
		filter.addAction("closeInvitationWnd");
		filter.addAction("closeInvitationWndTime");
		filter.addAction("disNetworkConnected");
		registerReceiver(InvitationBroadCast, filter);
	}

	private class MyInViTationBroadCast extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			//String action = intent.getAction();
			
			String action = intent.getAction();
			
			if("closeInvitationWnd".equals(action)){

				Log.i("--------------guaduan", "closeInvitationWnd ： " + " : " + action);

				ViedoInviteActivity.this.finish();
				hangUp();

			}else if("closeInvitationWndTime".equals(action)){


				if(callback != null ){
					onFaile(1);
				}else{
					if(inviteResultCallback != null ){
						inviteResultCallback.onInviteFaile("超时");
					}
				}

				hangUp();
				//ViedoInviteActivity.this.finish();
			}
			else if("disNetworkConnected".equals(action)){
				ViedoInviteActivity.this.finish();
			}
		}
	}

	private void isMeeting(){//查询用户是否在会议中
		WebServiceUtil.request(Constants.VIDEO_AUDIO_IS_MEETING, "json", this);
	}

	private void hangUp(){//挂断会议
		WebServiceUtil.request(Constants.VIDEO_AUDIO_HANG_UP, "json", this);
	}

	private void updateMeeting() {//更新会议
		WebServiceUtil.request(Constants.VIDEO_AUDIO_UPDATE_MEETING, "json", this);
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

		if (mode != null && mode.equals(Constants.VIDEO_AUDIO_IS_MEETING)) {//查询用户是否在会议中

			try {
				json.put("userId", userId);
			} catch (JSONException e) {
				e.printStackTrace();
			}

		}else if(mode != null && mode.equals(Constants.VIDEO_AUDIO_HANG_UP)){//挂断会议
			try {
				json.put("userId", userId);
				json.put("meetNumber", created_roomid);
				json.put("numbers", 0);

				//Log.i("单人视频挂断", "userId ： " + " : " + userId + " : " + "created_roomid:"  + created_roomid + " : ");
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}

		else if(mode != null && mode.equals(Constants.VIDEO_AUDIO_UPDATE_MEETING)){//更新会议
//			try {
//				json.put("roomNumber", roomNumber);//房间号
//				json.put("originator", originator);//创建人
//				json.put("theme", theme);//主题
//				json.put("type", type);//1：为单人会议，2：多人会议
//
//
//				JSONArray jsonArray = new JSONArray();
//
//				for(int i = 0;i < 1 ; i++){
//					JSONObject json = new JSONObject();
//
//					json.put("userId", userId);
//					json.put("systemUserId", systemUserId);
//					json.put("headPortrait", headPortrait);
//					json.put("userName", userName);
//
//					jsonArray.put(json);
//				}
//
//				json.put("list", jsonArray.toString());//成员
//
//			} catch (JSONException e) {
//				e.printStackTrace();
//			}
		}

		return json.toString().replace("\\/", "/").replace("\\n", "");
	}

	@Override
	public void onReceivedData(String mode, JSONObject result) {

			if (result == null) {
				return;
			}else{
				if(mode != null && mode.equals(Constants.VIDEO_AUDIO_HANG_UP)){
//					if (result.getString("status").equals("200")) {
//						finish();
//					}
					finish();
				}else if(mode != null && mode.equals(Constants.VIDEO_AUDIO_IS_MEETING)){

				}
			}
	}

	class WaitTime extends Thread{
		private int type=0;
		private int s;
		private boolean bRuning = false;
		public WaitTime(){
			bRuning = true;
		}
		public void stopRecordTime(){
			bRuning = false;
		}

		@Override
		public void run() {
			while(bRuning){
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				if(s++ > 30){
					mHandler.sendEmptyMessage(2);
					bRuning = false;
				}
			}
		}
	}


}
