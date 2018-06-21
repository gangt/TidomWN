package com.xiangpu.activity;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.PowerManager;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.konecty.rocket.chat.R;
import com.lssl.activity.SuneeeApplication;
import com.xiangpu.bean.ChatUserInfo;
import com.xiangpu.utils.BitmapUtils;
import com.xiangpu.utils.NpcCommon;
import com.xiangpu.utils.Utils;

import java.util.ArrayList;

import io.agora.openvcall.model.ConstantApp;

import android.os.PowerManager;
import android.os.PowerManager.WakeLock;

import android.view.Window;
import android.view.WindowManager;

import com.xiangpu.common.Constants;
import java.util.Map;
import java.util.List;
import org.apache.http.NameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import com.xiangpu.utils.WebServiceUtil;
import com.xiangpu.utils.ToastUtils;

import com.xiangpu.utils.NetWorkUtils;
import com.xiangpu.utils.Utils;
/**
 * 邀請界面
 * @author chengang
 *
 */
public class ViedoGroupInviteActivity extends Activity implements WebServiceUtil.OnDataListener{
	private ImageButton call_reject_btn;
	private ImageButton call_receive_btn;
	private ImageButton call_reject_btn_invite;
	
	private LinearLayout call_receive_layout;
	
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

	private String channelName;
	private String encryptionKey;
	private String encryptionMode;
	private String video_type;

	private TextView txt_network_id;

	public ArrayList<ChatUserInfo> userInfoList = null;
	private String userId;
	private String callback = null;

	private WaitTime waitTimer = null;
	private MyInViTationBroadCast InvitationBroadCast;

	private PowerManager.WakeLock wakeLock;

	private static GroupInviteResultCallback inviteGroupResultCallback;
	public static void setGroupInviteResultCallback(GroupInviteResultCallback callback){
		inviteGroupResultCallback = callback;
	}
	public interface GroupInviteResultCallback {
	    void onInviteSuccess(String callback);
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

		setContentView(R.layout.im_group_view_ccp_call_interface_layout);


		userInfoList = this.getIntent().getParcelableArrayListExtra("chatList");

		initview();
		initonclick();

		registerBroadCast();

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

		txt_network_id = (TextView) this.findViewById(R.id.txt_network_id);

		if(NpcCommon.verifyNetwork(this)){
			if(NpcCommon.mNetWorkType == NpcCommon.NETWORK_TYPE.NETWORK_WIFI){
				txt_network_id.setText("您正在使用WIFI网络，通话完全免费！");
			}else{
				txt_network_id.setText("您正在使用手机网络，通话会产生流量费用！");
			}
		}

		showHeader();

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

		userId  = getIntent().getStringExtra(
				"userId");

		callback =  getIntent().getStringExtra(
				"callback");


		waitTimer = new WaitTime();
		waitTimer.start();

		SuneeeApplication.getInstance().playSounde(0);
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

				if(callback != null){
					onFaile(1);
				}else{
					if(inviteGroupResultCallback!=null){
						inviteGroupResultCallback.onInviteFaile("超时");
					}
				}
				hangUp();

			}
		}
	};

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
	
	private void initonclick() {

		call_reject_btn.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {

				hangUp();

				if(callback != null){

					onFaile(0);

				}else{
					if(inviteGroupResultCallback!=null){
						inviteGroupResultCallback.onInviteFaile("video reject");
					}
				}

			}
		});

		call_receive_btn.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {

				if(NetWorkUtils.isNetworkConnected(ViedoGroupInviteActivity.this)) {//没网络。

					Log.i("-onGroupChatSuccess", "callback ： " + " : " + callback);

					if(callback != null){
						try{
							JSONObject result = new JSONObject();
							result.put("av_type",video_type);
							result.put("data",callback);

							inviteGroupResultCallback.onInviteSuccess(result.toString());

							Log.i("-onGroupChatSuccess", "onGroupChatSuccess ： " + " : " + result.toString());
						}catch (JSONException e) {
							e.printStackTrace();
						}

					}else{
						inviteGroupResultCallback.onInviteSuccess(null);
					}

					finish();
				}else{
					ToastUtils.showCenterToast(ViedoGroupInviteActivity.this,""+String.valueOf("当前无网络！"));
				}
			}
		});

		call_reject_btn_invite.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				hangUp();

				if(callback != null){

					onFaile(0);

				}else{
					if(inviteGroupResultCallback!=null){
						inviteGroupResultCallback.onInviteFaile("reject");
					}
				}
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

			if(inviteGroupResultCallback!=null){
				inviteGroupResultCallback.onInviteFaile(errorMessage);
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
	}

	@Override
	public void onResume(){
		PowerManager pm = (PowerManager) getSystemService(POWER_SERVICE);
		wakeLock =  pm.newWakeLock(PowerManager.ACQUIRE_CAUSES_WAKEUP | PowerManager.SCREEN_DIM_WAKE_LOCK,"bright");

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
		filter.addAction("updataUserList");
		filter.addAction("disNetworkConnected");
		registerReceiver(InvitationBroadCast, filter);
	}

	private class MyInViTationBroadCast extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			//String action = intent.getAction();
			
			String action = intent.getAction();
			
			if("closeInvitationWnd".equals(action)){
				ViedoGroupInviteActivity.this.finish();
				//hangUp();

			}else if("closeInvitationWndTime".equals(action)){
				if(inviteGroupResultCallback!=null ){
					inviteGroupResultCallback.onInviteFaile("超时");
				}
				//ViedoGroupInviteActivity.this.finish();

				hangUp();

			}else if("updataUserList".equals(action)){
				userInfoList = intent.getParcelableArrayListExtra("chatList");
				showHeader();
			}
			else if("disNetworkConnected".equals(action)){
				ViedoGroupInviteActivity.this.finish();
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
				json.put("meetNumber", channelName);
				json.put("numbers",userInfoList.size()>=1 ? userInfoList.size() -1:0);

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
				finish();
			}else if(mode != null && mode.equals(Constants.VIDEO_AUDIO_IS_MEETING)){

			}
		}
	}
}
