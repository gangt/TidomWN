package com.lssl.activity;

import android.app.ActivityManager;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.Display;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.igexin.sdk.PushManager;
import com.konecty.rocket.chat.R;
import com.lssl.entity.DonutProgressInfo;
import com.lssl.utils.CacheManager;
import com.lssl.weight.CircleTextProgressbar;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.pgyersdk.javabean.AppBean;
import com.pgyersdk.update.PgyUpdateManager;
import com.pgyersdk.update.UpdateManagerListener;
import com.suneee.demo.scan.ScanQRActivity;
import com.xiangpu.activity.CompanyGridActivity;
import com.xiangpu.activity.ViedoGroupInviteActivity;
import com.xiangpu.activity.ViedoInviteActivity;
import com.xiangpu.activity.WebMainActivity;
import com.xiangpu.activity.WebMainViewPageActivity;
import com.xiangpu.activity.person.ServerMainLoginActivity;
import com.xiangpu.activity.usercenter.PersonCenterActivity;
import com.xiangpu.appversion.CheckVersionService;
import com.xiangpu.bean.ChatUserInfo;
import com.xiangpu.bean.DataCache;
import com.xiangpu.bean.ErrorCode;
import com.xiangpu.bean.PointUserInfoBean;
import com.xiangpu.common.Constants;
import com.xiangpu.dialog.CustomProgressDialog;
import com.xiangpu.dialog.CustomerDialog;
import com.xiangpu.plugin.TimeSpacePlugin;
import com.xiangpu.receivers.NetBroadcastReceiver;
import com.xiangpu.utils.BitmapUtils;
import com.xiangpu.utils.CRequest;
import com.xiangpu.utils.DensityUtil;
import com.xiangpu.utils.LoginUtils;
import com.xiangpu.utils.NoticeMessageUtils;
import com.xiangpu.utils.SharedPrefUtils;
import com.xiangpu.utils.SizeUtils;
import com.xiangpu.utils.ToastUtils;
import com.xiangpu.utils.Utils;
import com.xiangpu.utils.VerifyUtils;
import com.xiangpu.utils.WebServiceUtil;

import org.apache.http.NameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import chat.rocket.android.InitializeUtils;
import chat.rocket.android.RocketChatCache;
import io.agora.openvcall.model.ConstantApp;

/**
 * 启动页
 * Created by suneee on 2016/12/17.
 */
public class MainActivity extends WLActivity implements
        View.OnClickListener, BDLocationListener, WebServiceUtil.OnDataListener,
        ScanQRActivity.QScanResultCallBackInterface {

    private static final String TAG = MainActivity.class.getSimpleName();
    private ImageView lssl_scan;
    private ImageView lssl_person;
    private TextView time;
    private ImageButton home1Btn;
    private ImageButton home2Btn;
    private ImageButton home3Btn;
    private ImageButton homeBtn;
    private ImageButton iv1Btn;
    private ImageButton iv2Btn;
    private ImageButton iv3Btn;
    private ImageButton iv4Btn;

    private LinearLayout skzxLl;
    private CircleTextProgressbar progressView;
    private LinearLayout circleLayout;
    private ImageButton btnBlue;
    private ImageButton btnOrange;
    private ImageView imgLogo;

    private LinearLayout ll_mian_activity_bg;
    private View v_vertical_line_1;
    private View v_vertical_line_2;

    private TextView shidianBtn;
    private TextView jnqBtn;
    private TextView fnqBtn;
    private String mWeek = "星期五";//当前星期
    private String mMonth = "";//月份
    private String mDay = "";//日期
    private ImageView timeSpace;
    //定位
    public LocationClient mLocationClient = null;

    public String cityname = "";

    public String source = SuneeeApplication.weilianType;

    public Map<String, Boolean> map = new HashMap<>();

    private CustomerDialog dialog;

    private PointUserInfoBean puBean;

    private NetBroadcastReceiver nbReceiver;
    private BaseBroadcastReceiver baseReceiver;//广播

    //临时保存视频会议相关信息
    private Intent intentVideoAudioMeetingTemp = null;
    private String userIdVideoAudio = null;

    private String created_roomid;

    // 是否自动登录，false不是自动登录，true自动登录，是为了自动登录在主页进行版本检测，不是自动登录在鉴权页进行版本检测
    private boolean isAutoLoad = false;

    private static CommonResultCallback commonResultCallback = null;

    public static void setCommonResultCallback(CommonResultCallback callback) {
        commonResultCallback = callback;
    }

    public interface CommonResultCallback {
        void onCommonCmd(String cmd);
    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == -1) { // 关闭当前页面
                dismissProgressDialog();
                showDialogMessage(MSG_ACTION_DATA_EXCEPTION, getString(R.string.data_error_notice));
            } else if (msg.what == -2) { // 不关闭当前页面
                dismissProgressDialog();
                ToastUtils.showCenterToast(MainActivity.this, msg.obj + "");
            } else if (msg.what == 1) {
                SuneeeApplication.weilianType = source;
                doOnCreate();
                SharedPrefUtils.saveStringData(MainActivity.this, Constants.WEI_LIAN_TYPE, SuneeeApplication.weilianType);
                SharedPrefUtils.saveStringData(MainActivity.this, Constants.SELECT_COMPANY_ID, SuneeeApplication.getUser().getSelectCompanyId());

                //andi,企业编码切换，定子链跟随切换
                if (commonResultCallback != null) {
                    commonResultCallback.onCommonCmd(SuneeeApplication.getUser().getSelectCompanyId());
                }

            } else if (msg.what == MSG_ACTION_VIDEO_NOTIFY) {
                Log.i("-------------：", "showDialogMessage ： " + " : ");
                showDialogMessage(MSG_ACTION_VIDEO_NOTIFY, getString(R.string.data_video_notice));
            }
        }
    };

    private int MSG_ACTION_DATA_EXCEPTION = 1000; // 数据加载异常
    private int MSG_ACTION_VIDEO_NOTIFY = 1001; // 视频加载提示；

    private String sessionId;

    private void showDialogMessage(final int actionEvent, String message) {

        if (dialog == null) {
            dialog = new CustomerDialog(MainActivity.this);
        }
        dialog.show();
        dialog.setMessageText(message);
        dialog.setButtonEvent(getString(R.string.sure_notice_txt), new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                if (MSG_ACTION_DATA_EXCEPTION == actionEvent) {
                    showProgressDialog();
                    SuneeeApplication.configManage.loadComplayConfig(true, SuneeeApplication.weilianType, SuneeeApplication.getUser().getSelectCompanyId(), handler);
                } else if (MSG_ACTION_VIDEO_NOTIFY == actionEvent) {
                    //gotoVideoAudioMeeting(intentVideoAudioMeetingTemp);
                }
            }
        });
        dialog.setCancelButtonEvent(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SuneeeApplication.getInstance().addActivity_(this);
        setContentView(R.layout.activity_lssl_main);

        sessionId = SharedPrefUtils.getSessionId(this);

        loadPoint("", "象谱"); // 加载小黄点
        puBean = new PointUserInfoBean();

        initBroadcastReceiver();
    }

    private int count = 0;

    //注册广播
    private void initBroadcastReceiver() {
        //注册网络广播
        nbReceiver = new NetBroadcastReceiver();
        if (getIntent().hasExtra(Constants.INTENT_ACTIVITY)) {
            isAutoLoad = getIntent().getBooleanExtra(Constants.INTENT_ACTIVITY, false);
        }
        nbReceiver.setListener(new NetBroadcastReceiver.ActionListener() {
            @Override
            public void receiveResult(boolean isNetworkOk) {
                isShowErrorNotice(isNetworkOk);
                if (popupWindow != null && popupWindow.isShowing()) popupWindow.dismiss();
                count++;
                if (!isNetworkOk && count > 1) {
                    Intent intent = new Intent();
                    intent.setAction("disNetworkConnected");//
                    sendBroadcast(intent);
                    return;
                }
                showProgressDialog();
                SuneeeApplication.configManage.loadComplayConfig(true, SuneeeApplication.weilianType, SuneeeApplication.getUser().getSelectCompanyId(), handler);
                if (isNetworkOk && isAutoLoad) {
                    // 有网络且是自动登录
                    if (!SuneeeApplication.checkVersion) {
                        CheckVersionService versionService = new CheckVersionService(MainActivity.this);
                        versionService.checkVersion();
                    }
                }
            }
        });

        IntentFilter netFilter = new IntentFilter();
        netFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(nbReceiver, netFilter);

        baseReceiver = new BaseBroadcastReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(Constants.MODULE_UCP);
        registerReceiver(baseReceiver, filter);
    }

    private void unRegisterReceiver() {
        if (nbReceiver != null) {
            nbReceiver.setListener(null);
            unregisterReceiver(nbReceiver);
        }

        if (baseReceiver != null) {
            unregisterReceiver(baseReceiver);
        }
    }

    private class BaseBroadcastReceiver extends BroadcastReceiver { //注册基础广播
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            if (Constants.MODULE_UCP.equals(action)) {

                Log.i("MODULE_UCP", "action ： " + " : " + action);

                if (isForeground(MainActivity.this, "ViedoInviteActivity")) {
                    SuneeeApplication.getInstance().showNotificationAudioVideo(1);
                } else if (isForeground(MainActivity.this, "ViedoGroupInviteActivity")) {
                    SuneeeApplication.getInstance().showNotificationAudioVideo(0);
                }

                if (intent != null) {

                    intentVideoAudioMeetingTemp = intent;

                    ArrayList<ChatUserInfo> userInfoList = new ArrayList<ChatUserInfo>();

                    try {
                        String router = intent.getStringExtra("router");
                        String webrtcParam = router.substring(6, router.length());
                        JSONObject result = new JSONObject(webrtcParam);

                        created_roomid = result.getString("created_roomid");
                        String video_type = result.getString("av_type");

                        if (video_type.equals("3") || video_type.equals("4")) {//单人邀请页面
                            userIdVideoAudio = result.getString("userId");
                        } else {
                            userIdVideoAudio = result.getString("selfId");
                        }


//                        JSONArray jUserList = result.getJSONArray("userlist");
//                        for (int i = 0; i < jUserList.length(); i++) {
//                            JSONObject JsonUser = new JSONObject(jUserList.get(i).toString());
//                            String name = JsonUser.getString("name");
//                            String avatar = JsonUser.getString("avatar");
//                            String userId = JsonUser.getString("userId");
//
//                            ChatUserInfo user = new ChatUserInfo();
//                            user.name = name;
//                            user.avatar = avatar;
//                            user.userId = userId;
//                            userInfoList.add(user);
//                        }
//
//                        if (jUserList.length() > 1) {
//
//                            int selfindex = result.getInt("selfindex");//指定成员列表，自己所在的位置。方便后续挂断用到自己的信息。
//                            ChatUserInfo user = userInfoList.get(selfindex);
//
//                            userIdVideoAudio = user.userId;
//
//                        } else {
//                            userIdVideoAudio = result.getString("userId");
//                        }

                        Log.i("推送单人视频", "userId ： " + " : " + userIdVideoAudio);
                        //isMeeting();//先进行会议房间检测

                        queryMeeting();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }

            }
        }
    }

    private void doOnCreate() {
        setContentView(R.layout.activity_lssl_main);
        isNetworkOk();

        mLocationClient = new LocationClient(getApplicationContext());     //声明LocationClient类
        mLocationClient.registerLocationListener(this);    //注册监听函数

        registerUpdate();
        // com.getui.demo.DemoPushService 为第三方自定义推送服务
//        PushManager.getInstance().initialize(this.getApplicationContext(), SEPushService.class);
//        PushManager.getInstance().registerPushIntentService(this.getApplicationContext(), SEPushIntentService.class);

        CacheManager.setSysCachePath(getCacheDir().getPath());

        skzxLl = (LinearLayout) findViewById(R.id.view_skzx);
        progressView = (CircleTextProgressbar) findViewById(R.id.progress_view);
        progressView.setProgressType(CircleTextProgressbar.ProgressType.COUNT);
        progressView.setProgressColor(Color.parseColor("#ff8500"));
        progressView.setProgressLineWidth(Utils.dipToPx(this, 10));
        lssl_scan = (ImageView) findViewById(R.id.img_scan);
        lssl_person = (ImageView) findViewById(R.id.img_person);
        time = (TextView) findViewById(R.id.data);
        timeSpace = (ImageView) findViewById(R.id.imageView5);
        lssl_scan.setOnClickListener(this);
        lssl_person.setOnClickListener(this);
        skzxLl.setOnClickListener(this);

        home1Btn = (ImageButton) findViewById(R.id.home_1);
        home2Btn = (ImageButton) findViewById(R.id.home_2);
        home3Btn = (ImageButton) findViewById(R.id.home_3);
        homeBtn = (ImageButton) findViewById(R.id.home);

        circleLayout = (LinearLayout) findViewById(R.id.circle_layout);
        btnBlue = (ImageButton) findViewById(R.id.blue_btn);
        btnOrange = (ImageButton) findViewById(R.id.orange_btn);
        imgLogo = (ImageView) findViewById(R.id.img_logo);

        iv1Btn = (ImageButton) findViewById(R.id.iv_btn1);
        iv2Btn = (ImageButton) findViewById(R.id.iv_btn2);
        iv3Btn = (ImageButton) findViewById(R.id.iv_btn3);
        iv4Btn = (ImageButton) findViewById(R.id.iv_btn4);

        shidianBtn = (TextView) findViewById(R.id.shidianBtn);
        jnqBtn = (TextView) findViewById(R.id.jnqBtn);
        fnqBtn = (TextView) findViewById(R.id.fnqBtn);

        ll_mian_activity_bg = (LinearLayout) findViewById(R.id.ll_mian_activity_bg);
        ImageView iv_zhuye_horizontal_line = (ImageView) findViewById(R.id.iv_zhuye_horizontal_line);
        v_vertical_line_1 = findViewById(R.id.v_vertical_line_1);
        v_vertical_line_2 = findViewById(R.id.v_vertical_line_2);

        if (Constants.WEI_LIAN_BAO.equals(source)) {
            ll_mian_activity_bg.setBackgroundColor(getResources().getColor(R.color.color_00204C));
            iv_zhuye_horizontal_line.setBackgroundColor(getResources().getColor(R.color.zhuye_horizontal_line_weilianbao));
            circleLayout.setBackgroundResource(R.drawable.circle_full_bao_bg);
            v_vertical_line_1.setBackgroundColor(getResources().getColor(R.color.zhuye_vertical_line_weilianbao));
            v_vertical_line_2.setBackgroundColor(getResources().getColor(R.color.zhuye_vertical_line_weilianbao));
        } else if (Constants.WEI_LIAN_WA.equals(source)) {
            ll_mian_activity_bg.setBackgroundColor(getResources().getColor(R.color.color_C56813));
            iv_zhuye_horizontal_line.setBackgroundColor(getResources().getColor(R.color.zhuye_horizontal_line_weilianwa));
            circleLayout.setBackgroundResource(R.drawable.circle_full_wa_bg);
            v_vertical_line_1.setBackgroundColor(getResources().getColor(R.color.zhuye_vertical_line_weilianwa));
            v_vertical_line_2.setBackgroundColor(getResources().getColor(R.color.zhuye_vertical_line_weilianwa));
        }

        puBean.setAvatar(SuneeeApplication.getUser().getPhoto());
        puBean.setUserName(SuneeeApplication.getUser().getName());
        puBean.setCompanyCode(SuneeeApplication.getUser().getSelectCompanyId());
        puBean.setCompanyName(null);

        initData();

        loadPoint("", "象谱"); // 加载小黄点

        initTime();//显示当前日期

        WindowManager windowManager = getWindowManager();
        Display display = windowManager.getDefaultDisplay();
        SuneeeApplication.getInstance().screenWidth = display.getWidth();
        SuneeeApplication.getInstance().screenHeight = display.getHeight();

        registerReceiver(mHomeKeyEventReceiver, new IntentFilter(
                Intent.ACTION_CLOSE_SYSTEM_DIALOGS));

        initView();
    }

    @Override
    protected void setMainPageDataAndPower(String weilianType) {
        source = weilianType;
        showProgressDialog();
        SuneeeApplication.configManage.loadComplayConfig(false, weilianType, getString(R.string.api_compcode), handler);
    }

    private void initView() {
        setFunctionIcon("sdg", home1Btn.getId());
        setFunctionIcon("cjg", home2Btn.getId());
        setFunctionIcon("hlg", home3Btn.getId());
        setFunctionIcon("jdq", progressView.getId());
        setFunctionIcon("dzl", iv1Btn.getId());
        setFunctionIcon("zzl", iv2Btn.getId());
        setFunctionIcon("lzl", iv3Btn.getId());
        setFunctionIcon("xxzx", iv4Btn.getId());
        setFunctionIcon("skq", timeSpace.getId());
        setFunctionName("hnq", R.id.shidianBtn);
        setFunctionIcon("hnq", R.id.shidianBtn);
        setFunctionName("jnq", jnqBtn.getId());
        setFunctionIcon("jnq", jnqBtn.getId());
        setFunctionName("fnq", R.id.fnqBtn);
        setFunctionIcon("fnq", R.id.fnqBtn);

//        chat.rocket.android.service.ConnectivityManager.getInstance(this).keepAliveServer();
//        String hostname = RocketChatCache.INSTANCE.getSelectedServerHostname();
//        String sessionId = RocketChatCache.INSTANCE.getSessionId();

//        if (hostname == null) {
//            InitializeUtils.getInstance().serviceConnection(this);
//        }
// else if (RocketChatCache.INSTANCE.getDownLine()||!SharedPrefUtils.getSelectCompanyCode(MainActivity.this).equals(RocketChatCache.INSTANCE.getCompanyId())||!SharedPrefUtils.getUserId(MainActivity.this).equals(RocketChatCache.INSTANCE.getFrameUserId())) {
//            if (!SharedPrefUtils.getUserId(MainActivity.this).equals(RocketChatCache.INSTANCE.getFrameUserId())){
//                InitializeUtils.getInstance().clearSession();
//                InitializeUtils.getInstance().clearSubscription();
//                InitializeUtils.getInstance().clearUser();
//            }
//            InitializeUtils.getInstance().login(this, this.sessionId,SharedPrefUtils.getUserId(MainActivity.this),SharedPrefUtils.getSelectCompanyCode(MainActivity.this));
//        }
    }

    /**
     * 设置主页每个按钮的点击事件
     */
    private void initOnClickEvent() {
        home1Btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doPermissionVerification("sdg", "", "视点观", Constants.PERSPECTIVE_VIEW_URL);
            }
        });
        home2Btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doPermissionVerification("cjg", "", "场景观", Constants.LANDSCAPE_VIEW_URL);

            }
        });
        home3Btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doPermissionVerification("hlg", "", "活流观", Constants.LIVELY_VIEW_URL);
            }
        });
        btnBlue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doPermissionVerification("syzx", "ShoppingMall", "商业中心", Constants.BUSINESS_CENTER_URL);
            }
        });
        btnOrange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doPermissionVerification("jdq", "", "指挥中心", Constants.COMMAND_CENTER_URL);
            }
        });
        iv1Btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!map.containsKey("dzl") || !map.get("dzl")) {
                    ToastUtils.showCenterToast(MainActivity.this, "无权限访问");
                    return;
                }
                if (getFunctionUrl("dzl") == null) {
                    ToastUtils.showCenterToast(MainActivity.this, "此功能未开放");
                    return;
                }
                // 改用加载原生定子链页面
                String userId = SharedPrefUtils.getUserId(MainActivity.this);
                String companyId = SharedPrefUtils.getSelectCompanyCode(MainActivity.this);
//                if (!userId.equals(RocketChatCache.INSTANCE.getFrameUserId()))
//                    InitializeUtils.getInstance().exit();
                if (!chat.rocket.android.helper.TextUtils.isEmpty(RocketChatCache.INSTANCE.getUserId())&&!userId.equals(RocketChatCache.INSTANCE.getFrameUserId())){
                    try {
                        InitializeUtils.getInstance().clearSession();
//                    InitializeUtils.getInstance().clearSubscription();
                        InitializeUtils.getInstance().clearUser();
                    } catch (Exception e) {
                    }
                }
                Intent itDzl = new Intent(MainActivity.this, chat.rocket.android.activity.ChatMainActivity.class);
                String sessionId = SharedPrefUtils.getSessionId(MainActivity.this);
                String sessionId1 = RocketChatCache.INSTANCE.getSessionId();
                itDzl.putExtra("sessionId", sessionId);
                itDzl.putExtra("userId", userId);
                itDzl.putExtra("companyId", companyId);
                startActivity(itDzl);
            }
        });
        iv2Btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doPermissionVerification("zzl", "", "转子链", Constants.ZHUANZILIAN_URL);
            }
        });
        iv3Btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doPermissionVerification("lzl", "", "粒子链", Constants.LIZILIAN_URL);
            }
        });
        iv4Btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doPermissionVerification("xxzx", "", "学习中心", Constants.XIANGYISHUYUAN);
            }
        });
        timeSpace.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doPermissionVerification("skq", TimeSpacePlugin.TAG, "时空中心", Constants.SPACETIME_URL);
            }
        });
        shidianBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doPermissionVerification("hnq", "", "汇能器", Constants.HUINENGQI_URL);
            }
        });
        jnqBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doPermissionVerification("jnq", "JuNengQi", "聚能器", Constants.JUNENGQI_URL);
            }
        });
        fnqBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doPermissionVerification("fnq", "FuNengQi", "赋能器", Constants.FUNENGQI_URL);
            }
        });
    }

    /**
     * 校验点击的图标是否配置和有权限
     *
     * @param key         fnq
     * @param tag         FuNengQi
     * @param defuatTitle 赋能器
     * @param defautUrl   h5链接
     */
    private void doPermissionVerification(String key, String tag, String defuatTitle, String defautUrl) {
        if (getFunctionUrl(key) == null) {
            ToastUtils.showCenterToast(MainActivity.this, "此功能未开放");
            return;
        }
        if (!map.containsKey(key) || !map.get(key)) {
            ToastUtils.showCenterToast(MainActivity.this, "无权限访问");
            return;
        }
        setFunctionUrl(key, tag, defuatTitle, defautUrl);
    }

    private void initTime() { // 获取时间
        String times = getData();
        time.setText(times);
    }

    public void initData() { // 新权限
        String strSessionId = SharedPrefUtils.getSessionId(this);
        if (TextUtils.isEmpty(strSessionId)) {
            ToastUtils.showCenterToast(this, getString(R.string.get_user_info_fail));
            LoginUtils.logoutSessionIdIsInvalid(this);
            return;
        }
        WebServiceUtil.request(Constants.DISNEYORDER_POWER_URL, "json", this);
    }

    private void isMeeting() {//查询用户是否在会议中，（调试相关方，用户中心和威宁通）
        //Log.i("推送单人视频房间检测");
        WebServiceUtil.request(Constants.VIDEO_AUDIO_IS_MEETING, "json", this);
    }

    private void queryMeeting() {//查询房间成员
        WebServiceUtil.request(Constants.VIDEO_AUDIO_QUERY_MEETING, "json", this);
    }

    @Override
    public void onClick(View view) {
        int viewId = view.getId();
        if (viewId == R.id.img_scan) {
            Intent codeIntent = new Intent(MainActivity.this, ScanQRActivity.class);
            startActivity(codeIntent);
        } else if (viewId == R.id.img_person) {
            Intent intent = new Intent(MainActivity.this, PersonCenterActivity.class);
            startActivity(intent);
        } else if (viewId == R.id.ll_home_page) {
            if (!Constants.WEI_LIAN_HAI.equals(SuneeeApplication.weilianType)) {
                Utils.startActivity(this, PersonCenterActivity.class);
                popupWindow.dismiss();
            }
        } else if (viewId == R.id.img_server_icon) {
            if (Constants.WEI_LIAN_HAI.equals(SuneeeApplication.weilianType)) {
                Intent intent = new Intent(MainActivity.this, ServerMainLoginActivity.class);
                intent.putExtra("isAutoSkip", false);
                startActivity(intent);
            } else {
                goToAuthenticationActivity();
            }
            popupWindow.dismiss();
        }
    }

    private void goWebMainActivity(String link, String title, String tagPlugin) {
        Intent intent = new Intent(this, WebMainActivity.class);
        intent.putExtra("title", title);
        intent.putExtra("link", link);
        intent.putExtra("TagPlugin", tagPlugin);
        this.startActivity(intent);
    }

    private void goWebMainPageActivity(String link, String title, String tagPlugin) {
        Intent intent = new Intent(this, WebMainViewPageActivity.class);
        intent.putExtra("title", title);
        intent.putExtra("link", link);
        intent.putExtra("TagPlugin", tagPlugin);
        this.startActivity(intent);
    }

    /**
     * 设置按钮的图标
     *
     * @param key 对应按钮的key
     * @param id  按钮的id
     */
    private void setFunctionIcon(String key, final int id) {
        if (SuneeeApplication.configManage != null) {
            final String iconUrl = SuneeeApplication.configManage.getFunctionIcon(key);

            if (id == R.id.shidianBtn || id == R.id.fnqBtn || id == R.id.jnqBtn) {
                if (iconUrl != null) {
                    showImg(iconUrl, id);
                }
            } else if (id == progressView.getId()) {
                if (iconUrl != null) {
                    ImageLoader.getInstance().displayImage(iconUrl, imgLogo);
                }
            } else if (id == timeSpace.getId()) {
                ImageView imageView = (ImageView) findViewById(id);
                if (iconUrl != null) {
                    ImageLoader.getInstance().displayImage(iconUrl, imageView);
                }
            } else {
                ImageButton imgBtn = (ImageButton) findViewById(id);
                if (iconUrl != null) {
                    ImageLoader.getInstance().displayImage(iconUrl, imgBtn);
                }
            }
        }
    }

    /**
     * 获取对应按钮的url地址
     *
     * @param key 按钮的对应key
     * @return url地址
     */
    private String getFunctionUrl(String key) {
        if (SuneeeApplication.configManage != null) {
            if (SuneeeApplication.configManage.getFunctionUrl(key) != null &&
                    SuneeeApplication.configManage.getFunctionUrl(key).length() > 0 &&
                    !SuneeeApplication.configManage.getFunctionUrl(key).equalsIgnoreCase("Empty")) {
                return SuneeeApplication.configManage.getFunctionUrl(key);
            }
        }
        return null;
    }

    private void setFunctionUrl(String key, String tag, String defuatTitle, String defautUrl) {
        if (SuneeeApplication.configManage != null) {

            String link = SuneeeApplication.configManage.getFunctionUrl(key);
            String title = SuneeeApplication.configManage.getFunctionName(key);

            if (link != null && title != null) {
                if ("syzx".equals(key)) {
                    goWebMainActivity(link, title, tag);
                } else if ("skq".equals(key)) {
                    goWebMainActivity(link, title, tag);
                } else if ("jdq".equals(key)) {
                    if (map.get("jdq")) {
                        goWebMainPageActivity(link, title, tag);
                    } else {
                        ToastUtils.showCenterToast(this, "无权限访问");
                    }
                } else {
                    goWebMainActivity(link, title, tag);
                }
            } else if (link == null) {
                goWebMainActivity(defautUrl, defuatTitle, tag);
            }
        }
    }

    private void setFunctionName(String key, int id) {
        String title = SuneeeApplication.configManage.getFunctionName(key);

        if (title != null) {
            ((TextView) this.findViewById(id)).setText(title);
        }
    }

    private void showImg(final String iconUrl, final int id) {
        ImageLoader.getInstance().loadImage(iconUrl, new ImageLoadingListener() {
            @Override
            public void onLoadingStarted(String s, View view) {

            }

            @Override
            public void onLoadingFailed(String s, View view, FailReason failReason) {

            }

            @Override
            public void onLoadingComplete(String s, View view, Bitmap bitmap) {
                Bitmap newBitmap = bitmap;
                if (id == shidianBtn.getId()) {
                    newBitmap = BitmapUtils.zoomImg(bitmap, DensityUtil.dip2px(MainActivity.this, 28), DensityUtil.dip2px(MainActivity.this, 28));
                } else if (id == fnqBtn.getId()) {
                    newBitmap = BitmapUtils.zoomImg(bitmap, DensityUtil.dip2px(MainActivity.this, 28), DensityUtil.dip2px(MainActivity.this, 28));
                } else if (id == jnqBtn.getId()) {
                    newBitmap = BitmapUtils.zoomImg(bitmap, DensityUtil.dip2px(MainActivity.this, 28), DensityUtil.dip2px(MainActivity.this, 28));
                }
                Drawable drawable = new BitmapDrawable(getResources(), newBitmap);
                TextView textView = (TextView) findViewById(id);
                textView.setCompoundDrawablesWithIntrinsicBounds(null, drawable, null, null);
            }

            @Override
            public void onLoadingCancelled(String s, View view) {

            }
        });
    }

    private void registerUpdate() {
        PgyUpdateManager.register(this, "com.suneee.weining.fileprovider",
                new UpdateManagerListener() {

                    @Override
                    public void onUpdateAvailable(final String result) {

                        // 将新版本信息封装到AppBean中
                        final AppBean appBean = getAppBeanFromString(result);
                        String releaseNote = appBean.getReleaseNote();
                        //强制升级
                        new AlertDialog.Builder(MainActivity.this)
                                .setTitle("更新")
                                .setMessage(releaseNote)
                                .setNegativeButton("确定", new DialogInterface.OnClickListener() {

                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        startDownloadTask(MainActivity.this, appBean.getDownloadURL());
                                    }
                                })
                                .setPositiveButton("取消", null)
                                .setCancelable(true)
                                .show();
                    }

                    @Override
                    public void onNoUpdateAvailable() {
                    }
                });
    }

    /**
     * 获取当前日期
     *
     * @return
     */
    private String getData() {
        final Calendar c = Calendar.getInstance();
        mMonth = String.valueOf(c.get(Calendar.MONTH) + 1);// 获取当前月份
        mDay = String.valueOf(c.get(Calendar.DAY_OF_MONTH));//获取当前月份的日期号码
        mWeek = String.valueOf(c.get(Calendar.DAY_OF_WEEK));//星期几
        if ("1".equals(mWeek)) {
            mWeek = "天";
        } else if ("2".equals(mWeek)) {
            mWeek = "一";
        } else if ("3".equals(mWeek)) {
            mWeek = "二";
        } else if ("4".equals(mWeek)) {
            mWeek = "三";
        } else if ("5".equals(mWeek)) {
            mWeek = "四";
        } else if ("6".equals(mWeek)) {
            mWeek = "五";
        } else if ("7".equals(mWeek)) {
            mWeek = "六";
        }
        return mMonth + "/" + mDay + " " + "星期" + mWeek;
    }

    long clickTime = 0;

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
//            if ((System.currentTimeMillis() - clickTime) > 2000) {
//                Toast.makeText(getApplicationContext(), "再次返回退出应用", Toast.LENGTH_SHORT).show();
//                clickTime = System.currentTimeMillis();
//                return false;
//            } else
            {
                int currentVersion = android.os.Build.VERSION.SDK_INT;
                if (currentVersion > android.os.Build.VERSION_CODES.ECLAIR_MR1) {  //android 版本高于2.2
                    Intent startMain = new Intent(Intent.ACTION_MAIN);
                    startMain.addCategory(Intent.CATEGORY_HOME);
                    startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(startMain);
                    //MainActivity.this.finish();
                    //SuneeeApplication.getInstance().removeALLActivity_();
                } else {    //android版本低于2.2，android 2.2之后，restartPackage()不可以强制将整个APP退出。
                    ActivityManager am = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
                    am.restartPackage(getPackageName());
                }
                return true;
            }

        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onReceiveLocation(BDLocation bdLocation) {
        //定位结果
        if (bdLocation == null) {
            return;
        }
        cityname = bdLocation.getCity();
//        if (!StringUtils.isEmpty(cityname)) {
//            mainAction.getWeatherCode(cityname);//获取城市Code
//            if (!StringUtils.isEmpty(SuneeeApplication.getInstance().getProperty(SuneeeApplication.PRESH_CONFIG_WEATHER_CODE))) {
//                weathcode = SuneeeApplication.getInstance().getProperty(SuneeeApplication.PRESH_CONFIG_WEATHER_CODE);
//            }
//            mainAction.getWeather(weathcode, mWeek);
//            initWeather();
//        }
        //Toast.makeText(getApplicationContext(),bdLocation.getLocType()+"", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onReceivedData(String mode, JSONObject result) {
        dismissProgressDialog();

        if (result == null) {//为空 取本地数据
            if (mode.equals(Constants.DISNEYORDER_URL)) {
                ToastUtils.showCenterToast(this, getString(R.string.data_error_notice_txt));
                DataCache dataCache = DataCache.where("url = ? and compId = ?", "DISNEYORDER_URL", SuneeeApplication.getUser().getSelectCompanyId()).findFirst(DataCache.class);
                if (dataCache == null) {
                    return;
                }
                initDisneyOrderData(dataCache.getJsonData());
            } else if (mode.equals(Constants.DISNEYORDER_POWER_URL)) {
//                ToastUtils.showCenterToast(this, getString(R.string.data_error_notice));
                String account = SharedPrefUtils.getStringData(MainActivity.this, "userName", "");
                DataCache dataCache = DataCache.where("url = ? and compId = ? and userName = ?", "DISNEYORDER_POWER_URL", SuneeeApplication.getUser().getSelectCompanyId(), account).findFirst(DataCache.class);
                if (dataCache == null) {
                    if (dialog == null) {
                        dialog = new CustomerDialog(MainActivity.this);
                    }
                    dialog.show();
                    dialog.setMessageText(getString(R.string.data_error_notice));
                    dialog.setButtonEvent(getString(R.string.sure_notice_txt), new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.dismiss();
                            initData();
                        }
                    });
                    dialog.setCancelButtonEvent(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.dismiss();
                        }
                    });
                    return;
                }
                initDisneyOrderPowerData(dataCache.getJsonData());
            }
        } else {
            try {
                if (mode != null && mode.equals(Constants.DISNEYORDER_URL)) {
                    if (!result.getString("code").equals("1")) {
//                        ToastUtils.showCenterToast(this, result.getString("msg"));
                        return;
                    }
                    String jsonData = result.getJSONObject("data").getString("jsonData");
                    DataCache dataCache = new DataCache("DISNEYORDER_URL", SuneeeApplication.getUser().getSelectCompanyId(), null, jsonData);
                    dataCache.save();
                    initDisneyOrderData(dataCache.getJsonData());
                } else if (mode != null && mode.equals(Constants.DISNEYORDER_POWER_URL)) {
                    if (!result.getString("status").equals("1")) {
                        String code = result.getString("code");
                        String errorMsgByCode = NoticeMessageUtils.getErrorMsgByCode(MainActivity.this, code);
                        if (TextUtils.isEmpty(errorMsgByCode)) {
                            ToastUtils.showCenterToast(this, result.getString("message"));
                        } else {
                            ToastUtils.showCenterToast(this, errorMsgByCode);
                            if (ErrorCode.CONNECTION_FAIL.equals(code)) {
                                //sessionId失效，退出登录
                                LoginUtils.logoutSessionIdIsInvalid(this);
                            }
                        }
                        return;
                    }
                    try {
                        JSONObject data = result.getJSONObject("data");
                        String account = SharedPrefUtils.getStringData(MainActivity.this, "userName", "");
                        DataCache dataCache = new DataCache("DISNEYORDER_POWER_URL", SuneeeApplication.getUser().getSelectCompanyId(), account, data.toString());
                        dataCache.save();
                        initDisneyOrderPowerData(dataCache.getJsonData());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else if (mode != null && mode.equals(Constants.VIDEO_AUDIO_IS_MEETING)) {//判断视频是否正在会议。

                    if (result.getBoolean("data") == true) {
                        if (intentVideoAudioMeetingTemp != null) {
                            //gotoVideoAudioMeeting(intentVideoAudioMeetingTemp);
                        } else {
                            Log.i("推送单人视频参数应答完毕：", "intentVideoAudioMeetingTemp ： " + " : " + intentVideoAudioMeetingTemp + "result:" + result);
                        }
                    } else {
                        ToastUtils.showCenterToast(this, "会议已经结束或已取消！");
                    }
                } else if (mode != null && mode.equals(Constants.VIDEO_AUDIO_QUERY_MEETING)) {

                    if (result.getString("message").equals("success")) {
                        if (intentVideoAudioMeetingTemp != null) {
                            gotoVideoAudioMeeting(intentVideoAudioMeetingTemp, result);
                        }
//                        try {
//                            JSONObject data = result.getJSONObject("data");
//
//                            int userCount = data.getInt("userCount");
//
//                            JSONArray jUserList = result.getJSONArray("list");
//                            for (int i = 0; i < jUserList.length(); i++) {
//                                JSONObject JsonUser = new JSONObject(jUserList.get(i).toString());
//                                String systemUserId = JsonUser.getString("systemUserId");
//                                String userName = JsonUser.getString("userName");
//                                String meetNumber = JsonUser.getString("meetNumber");
//                                String userId =  JsonUser.getString("userId");
//                                String header = JsonUser.getString("headPort");
//
//                                ChatUserInfo user = new ChatUserInfo();
//                                user.name = name;
//                                user.avatar = avatar;
//                                user.userId = userId;
//                                user.headerUrl = header;
//                                userInfoList.add(user);
//                            }
//
//                        } catch (JSONException e) {
//                            e.printStackTrace();
//                        }
                    }
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private void initDisneyOrderPowerData(String jsonData) {
        try {
            JSONObject data = new JSONObject(jsonData);
            if (data.get("dataPower") instanceof JSONArray) {
                JSONArray jsonArray = data.getJSONArray("dataPower");
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    switch (jsonObject.getString("resourceCode")) {
                        case "SHIDIANGUAN": // {"name":"视点观","haspower":0,"updateTime":1508570548994,"id":1}
                            map.put("sdg", jsonObject.getInt("hasPower") == 1);
                            break;

                        case "CHANGJINGGUAN": // {"name":"场景观","haspower":0,"updateTime":1508570548994,"id":2}
                            map.put("cjg", jsonObject.getInt("hasPower") == 1);
                            break;

                        case "HUOLIUGUAN": // {"name":"活流观","haspower":0,"updateTime":1508570548994,"id":3}
                            map.put("hlg", jsonObject.getInt("hasPower") == 1);
                            break;

                        case "SHANGYEZHONGXIN": // {"name":"商业中心","haspower":0,"updateTime":1508570548994,"id":4}
                            map.put("syzx", jsonObject.getInt("hasPower") == 1);
                            break;

                        case "ZHIHUIZHONGXIN": // {"name":"指挥中心","haspower":0,"updateTime":1508570548994,"id":17}
                            map.put("jdq", jsonObject.getInt("hasPower") == 1);
                            break;

                        case "DINGZILIAN": // {"name":"定子链","haspower":0,"updateTime":1508570548994,"id":6}
                            map.put("dzl", jsonObject.getInt("hasPower") == 1);
                            break;

                        case "ZHUANZILIAN": // {"name":"转子链","haspower":0,"updateTime":1508570548994,"id":7}
                            map.put("zzl", jsonObject.getInt("hasPower") == 1);
                            break;

                        case "LIZILIAN": // {"name":"粒子链","haspower":0,"updateTime":1508570548994,"id":15}
                            map.put("lzl", jsonObject.getInt("hasPower") == 1);
                            break;

                        case "XUEXIZHONGXIN": // {"name":"学习中心","haspower":0,"updateTime":1508570548994,"id":8}
                            map.put("xxzx", jsonObject.getInt("hasPower") == 1);
                            break;

                        case "SHIKONGZHONGXIN": // {"name":"时空中心","haspower":0,"updateTime":1508570548994,"id":9}
                            map.put("skq", jsonObject.getInt("hasPower") == 1);
                            break;

                        case "HUINENGQI": // {"name":"汇能器","haspower":0,"updateTime":1508570548994,"id":10}
                            map.put("hnq", jsonObject.getInt("hasPower") == 1);
                            break;

                        case "JUNENGQI": // {"name":"聚能器","haspower":0,"updateTime":1508570548994,"id":11}
                            map.put("jnq", jsonObject.getInt("hasPower") == 1);
                            break;

                        case "FUNENGQI": // {"name":"赋能器","haspower":0,"updateTime":1508570548994,"id":12}
                            map.put("fnq", jsonObject.getInt("hasPower") == 1);
                            break;

                        case "JIAODIANQUANSHIKONG": // {"name":"焦点圈-时空中心","haspower":0,"updateTime":1508570548994,"id":5}
                            map.put("jdq_url", jsonObject.getInt("hasPower") == 1 ? true : true);
                            if (map.containsKey("jdq_url") && map.get("jdq_url")) {
                                if (SuneeeApplication.configManage != null &&
                                        SuneeeApplication.configManage.getFunctionUrl("jdq_url") != null &&
                                        SuneeeApplication.configManage.getFunctionUrl("jdq_url").length() > 0 &&
                                        !SuneeeApplication.configManage.getFunctionUrl("jdq_url").equalsIgnoreCase("Empty")) {
                                    Constants.DISNEYORDER_URL = SuneeeApplication.configManage.getFunctionUrl("jdq_url");
                                }
                                WebServiceUtil.request(Constants.DISNEYORDER_URL, "json", this);
                            } else {
                                DonutProgressInfo info = new DonutProgressInfo();
                                info.topStr = "";
                                info.midStr = "";
                                info.bottomStr = "";
                                progressView.setInfo(info);
                            }
                            break;

                        default:
                            break;
                    }
                }
                initOnClickEvent();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void initDisneyOrderData(String jsonData) {
        JSONObject jsonObject;
        try {
            jsonObject = new JSONObject(jsonData);
            if (!"1".equals(jsonObject.getString("returnCode"))) {
                return;
            }
            if (jsonObject.has("data")) {
                JSONArray jsonArray = jsonObject.getJSONArray("data");
                DonutProgressInfo info = new DonutProgressInfo();
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonobject = jsonArray.getJSONObject(i);
                    switch (jsonobject.getString("orderNo")) {
                        case "1":
                            info.topStr = jsonobject.getString("item") + "：" + String.format("%.2f", jsonobject.getDouble("value"));
                            break;

                        case "2":
                            info.midStr = jsonobject.getString("item") + "：" + jsonobject.getInt("value");
                            break;

                        case "3":
                            info.bottomStr = jsonobject.getString("item") + "：" + SizeUtils.conversion(Float.parseFloat(jsonobject.getDouble("value") + ""), 1) + "";
                            break;

                        default:
                            break;
                    }
                }
                progressView.setInfo(info);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
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
        String sessionId = SharedPrefUtils.getSessionId(this);
        if (mode.equals(Constants.DISNEYORDER_URL)) {
            Map<String, String> paramter = CRequest.URLRequest(Constants.DISNEYORDER_URL);
            try {
                for (Map.Entry<String, String> entry : paramter.entrySet()) {
                    json.put(entry.getKey(), entry.getValue());
                }
                json.put("sessionId", sessionId);
                json.put("compCode", SuneeeApplication.getUser().getSelectCompanyId());
            } catch (JSONException e) {
                e.printStackTrace();
                ToastUtils.showCenterToast(this, "焦点圈接口参数解析错误");
            }
        } else if (mode.equals(Constants.DISNEYORDER_POWER_URL)) {
            try {
                json.put("sessionId", sessionId);
                if (Constants.WEI_LIAN_HAI.equals(SuneeeApplication.weilianType)) {
                    json.put("entrance", Constants.HAI);
                } else if (Constants.WEI_LIAN_BAO.equals(SuneeeApplication.weilianType)) {
                    json.put("entrance", Constants.BAO);
                } else if (Constants.WEI_LIAN_WA.equals(SuneeeApplication.weilianType)) {
                    json.put("entrance", Constants.WA);
                }
                if (Constants.WEI_LIAN_HAI.equals(SuneeeApplication.weilianType)) {
                    json.put("enterpriseCode", SuneeeApplication.getUser().getSelectCompanyId());
                } else {
                    json.put("enterpriseCode", getString(R.string.api_compcode));
                }
                json.put("sign", VerifyUtils.signValueEncryption(json.toString(), getString(R.string.api_encryptCode)));
                Log.e("主页权限参数", json + "");
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else if (mode.equals(Constants.VIDEO_AUDIO_IS_MEETING)) {

            try {
                json.put("userId", userIdVideoAudio);
                userIdVideoAudio = null;
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else if (mode != null && mode.equals(Constants.VIDEO_AUDIO_QUERY_MEETING)) {
            try {
                json.put("meetNumber", created_roomid);
                created_roomid = null;
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        // 由于接口后台没有对应的转义字符处理，暂由前端自己处理。
        return json.toString().replace("\\/", "/").replace("\\n", "");
    }

    protected CustomProgressDialog mProgressDialog;

    private final int CHOOSE_COMPANY = 100;

    @Override
    protected void changeCompanyAction() {
        super.changeCompanyAction();
        Intent intent = new Intent(this, CompanyGridActivity.class);
        intent.putExtra("selectCompanyCode", SuneeeApplication.getUser().getSelectCompanyId());
        startActivityForResult(intent, CHOOSE_COMPANY);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case CHOOSE_COMPANY:
                if (resultCode == RESULT_OK) {
                    String companyCode = data.getStringExtra("companyCode");
                    SuneeeApplication.getUser().setSelectCompanyId(companyCode);

                    showProgressDialog();
                    SuneeeApplication.configManage.loadComplayConfig(true, SuneeeApplication.weilianType, SuneeeApplication.getUser().getSelectCompanyId(), handler);
                }
                break;

            default:
                break;
        }
    }

    /**
     * 显示自定义进度框
     */
    protected void showProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            return;
        }
        mProgressDialog = new CustomProgressDialog(this, R.style.CustomProgressDialog);
        mProgressDialog.show();
    }

    /**
     * 取消自定义进度框
     */
    protected void dismissProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mLocationClient != null) {
            mLocationClient.unRegisterLocationListener(this);
        }

        unRegisterReceiver();

        PushManager.getInstance().stopService(this.getApplicationContext());

        SuneeeApplication.getInstance().removeActivity_(this);
    }

    @Override
    public void QScanResultCallBack(String result) {
        if (result.startsWith("http://") || result.startsWith("https://")) {     //链接
            goWebMainActivity(result, "", "");
        } else {
            ToastUtils.showCenterToast(this, result);
        }
    }

    private BroadcastReceiver mHomeKeyEventReceiver = new BroadcastReceiver() {
        String SYSTEM_REASON = "reason";
        String SYSTEM_HOME_KEY = "homekey";
        String SYSTEM_HOME_KEY_LONG = "recentapps";

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(Intent.ACTION_CLOSE_SYSTEM_DIALOGS)) {
                String reason = intent.getStringExtra(SYSTEM_REASON);
                if (TextUtils.equals(reason, SYSTEM_HOME_KEY)) {
//                    PushManager.getInstance().initialize(MainActivity.this.getApplicationContext(), SEPushService.class);
//                    PushManager.getInstance().registerPushIntentService(MainActivity.this.getApplicationContext(), SEPushIntentService.class);

//                    if(isForeground(MainActivity.this,"ChatActivity")){
//                        SuneeeApplication.getInstance().showNotificationAudioVideo(1);
//                    }else if(isForeground(MainActivity.this,"ChatAudioActivity")){
//                        SuneeeApplication.getInstance().showNotificationAudioVideo(0);
//                    }else
//                    {
//                        SuneeeApplication.getInstance().showNotification();
//                    }

                } else if (TextUtils.equals(reason, SYSTEM_HOME_KEY_LONG)) {
                    //表示长按home键,显示最近使用的程序列表
                }
            }
        }
    };

    /**
     * 判断某个界面是否在前台
     *
     * @param context   Context
     * @param className 界面的类名
     * @return 是否在前台显示
     */
    public boolean isForeground(Context context, String className) {
        if (context == null || TextUtils.isEmpty(className))
            return false;
        ActivityManager am = (ActivityManager) context.getSystemService(ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> list = am.getRunningTasks(1);

        for (ActivityManager.RunningTaskInfo taskInfo : list) {
            if (taskInfo.topActivity.getShortClassName().contains(className)) { // 说明它已经启动了
                return true;
            }
        }
        return false;
    }

    /**
     * 视频会议相关信息解析，
     */

    private void gotoVideoAudioMeeting(Intent intent, JSONObject resultData/*后台返回回来的数据*/) {
        try {
            ArrayList<ChatUserInfo> userInfoList = new ArrayList<ChatUserInfo>();

            String router = intent.getStringExtra("router");
            String callback = intent.getStringExtra("callback");

            String webrtcParam = router.substring(6, router.length());
            JSONObject result = new JSONObject(webrtcParam);

            String created_roomid = result.getString("created_roomid");
            String video_type = result.getString("av_type");
            String room_name = result.getString("room_name");
            String selfuserId;

            if (video_type.equals("3") || video_type.equals("4")) {//单人邀请页面
                selfuserId = result.getString("userId");
            } else {
                selfuserId = result.getString("selfId");
            }

            userInfoList.clear();

            if (video_type.equals("3") || video_type.equals("4")) {//单人邀请页面

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

            } else {
                try {
                    JSONObject data = resultData.getJSONObject("data");

                    int userCount = data.getInt("userCount");

                    JSONArray jUserList = data.getJSONArray("list");
                    for (int i = 0; i < jUserList.length(); i++) {
                        JSONObject JsonUser = new JSONObject(jUserList.get(i).toString());
                        String systemUserId = JsonUser.getString("systemUserId");
                        String userName = JsonUser.getString("userName");
                        String meetNumber = JsonUser.getString("meetNumber");
                        String userId = JsonUser.getString("userId");
                        String avatar = JsonUser.getString("headPort");

                        ChatUserInfo user = new ChatUserInfo();
                        user.name = userName;
                        user.avatar = avatar;
                        user.userId = userId;

                        userInfoList.add(user);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
//            JSONArray jUserList = result.getJSONArray("userlist");
//            for (int i = 0; i < jUserList.length(); i++) {
//                JSONObject JsonUser = new JSONObject(jUserList.get(i).toString());
//                String name = JsonUser.getString("name");
//                String avatar = JsonUser.getString("avatar");
//                String userId = JsonUser.getString("userId");
//
//                ChatUserInfo user = new ChatUserInfo();
//                user.name = name;
//                user.avatar = avatar;
//                user.userId = userId;
//                userInfoList.add(user);
//            }
//
//            int selfindex = 0;
//            if (jUserList.length() > 1) {
//                selfindex = result.getInt("selfindex");//指定成员列表，自己所在的位置。方便后续挂断用到自己的信息。
//            }

            if (video_type.equals("3") || video_type.equals("4")) {//单人邀请页面
                Intent i = new Intent(MainActivity.this, ViedoInviteActivity.class);
                i.putExtra(ConstantApp.ACTION_KEY_CHANNEL_NAME, created_roomid);
                i.putExtra(ConstantApp.ACTION_KEY_ENCRYPTION_KEY, "");
                i.putExtra(ConstantApp.ACTION_KEY_ENCRYPTION_MODE, "AES-128-XTS");
                i.putExtra(ConstantApp.ACTION_KEY_VIDEO_TYPE, video_type);

                ChatUserInfo user = userInfoList.get(0);

                i.putExtra("avatar", user.avatar);
                i.putExtra("name", user.name);
                i.putExtra("userId", user.userId);
                i.putExtra("callback", callback);

                MainActivity.this.startActivity(i);

            } else if (video_type.equals("5")) {//多人邀请页面

                Intent i = new Intent(MainActivity.this, ViedoGroupInviteActivity.class);
                i.putExtra(ConstantApp.ACTION_KEY_CHANNEL_NAME, created_roomid);
                i.putExtra(ConstantApp.ACTION_KEY_ENCRYPTION_KEY, "");
                i.putExtra(ConstantApp.ACTION_KEY_ENCRYPTION_MODE, "AES-128-XTS");
                i.putExtra(ConstantApp.ACTION_KEY_VIDEO_TYPE, video_type);


                Bundle bundle = new Bundle();
                bundle.putParcelableArrayList("chatList", userInfoList);
                i.putExtras(bundle);
                i.putExtra("callback", callback);
                i.putExtra("userId", selfuserId);

                MainActivity.this.startActivity(i);
            }

        } catch (JSONException e) {

        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        isSessionIdInvalid();
    }

    public  void isSessionIdInvalid(){
        String strSessionId = SharedPrefUtils.getSessionId(this);
        if (TextUtils.isEmpty(strSessionId)) {
            ToastUtils.showCenterToast(this, getString(R.string.get_user_info_fail));
            LoginUtils.logoutSessionIdIsInvalid(this);
            return;
        }
    }
}
