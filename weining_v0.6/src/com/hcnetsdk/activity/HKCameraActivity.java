/**
 * <p>DemoActivity Class</p>
 *
 * @author zhuzhenlei 2014-7-17
 * @version V1.0
 * @modificationHistory
 * @modify by user:
 * @modify by reason:
 */
package com.hcnetsdk.activity;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.hcnetsdk.bean.HKCamera;
import com.hcnetsdk.views.PlaySurfaceView;
import com.hikvision.netsdk.ExceptionCallBack;
import com.hikvision.netsdk.HCNetSDK;
import com.hikvision.netsdk.INT_PTR;
import com.hikvision.netsdk.NET_DVR_COMPRESSIONCFG_V30;
import com.hikvision.netsdk.NET_DVR_DEVICEINFO_V30;
import com.hikvision.netsdk.NET_DVR_PLAYBACK_INFO;
import com.hikvision.netsdk.NET_DVR_PREVIEWINFO;
import com.hikvision.netsdk.PTZCommand;
import com.hikvision.netsdk.PlaybackControlCommand;
import com.konecty.rocket.chat.R;

import org.MediaPlayer.PlayM4.Player;

import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.widget.ImageButton;
import android.content.pm.ActivityInfo;
import android.os.AsyncTask;

/**
 * <pre>
 *  ClassName  DemoActivity Class
 * </pre>
 *
 * @author zhuzhenlei
 *
 * @version V1.0
 * @modificationHistory
 */
public class HKCameraActivity extends Activity implements OnClickListener, OnTouchListener,OnGestureListener{

    private Button m_oLoginBtn = null;
    private Button m_oPreviewBtn = null;
    private Button m_oPlaybackBtn = null;
    private Button m_oParamCfgBtn = null;
    private Button m_oCaptureBtn = null;
    private Button m_oRecordBtn = null;
    private Button m_oTalkBtn = null;
    private Button m_oPTZBtn = null;
    private Button m_oOtherBtn = null;

    private TextView txt_video_title;
    private Button btn_back;
    private Button btn_full;
    //private EditText m_oIPAddr = null;
    //private EditText m_oPort = null;
    //private EditText m_oUser = null;
    //private EditText m_oPsd = null;

    private NET_DVR_DEVICEINFO_V30 m_oNetDvrDeviceInfoV30 = null;

    private int m_iLogID = -1; // return by NET_DVR_Login_v30
    private int m_iPlayID = -1; // return by NET_DVR_RealPlay_V30
    private int m_iPlaybackID = -1; // return by NET_DVR_PlayBackByTime

    private int m_iPort = -1; // play port
    private int m_iStartChan = 0; // start channel no
    private int m_iChanNum = 0; // channel number
    private PlaySurfaceView[] playView = new PlaySurfaceView[4];

    private final String TAG = "DemoActivity";

    private boolean m_bTalkOn = false;
    private boolean m_bPTZL = false;
    private boolean m_bMultiPlay = false;

    private boolean m_bNeedDecode = true;
    private boolean m_bSaveRealData = false;
    private boolean m_bStopPlayback = false;

    private HKCamera camera = null;

    private RelativeLayout Relative_video_title_id;
    private Button mBtFull = null;
    private FrameLayout FrameLayout_video;
    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        CrashUtil crashUtil = CrashUtil.getInstance();
//        crashUtil.init(this);

        requestWindowFeature(Window.FEATURE_NO_TITLE);

        setContentView(R.layout.main);

        if (!initeSdk()) {
            this.finish();
            return;
        }

        if (!initeActivity()) {
            this.finish();
            return;
        }

        String IP = this.getIntent().getStringExtra("IP");
        String ipPort = this.getIntent().getStringExtra("ipPort");
        String username = this.getIntent().getStringExtra("username");

        String pwd = this.getIntent().getStringExtra("pwd");
        String devicePort = this.getIntent().getStringExtra("devicePort");
        String name = this.getIntent().getStringExtra("name");

        if(IP == null){
            Toast.makeText(this,"视频数据获取失败！",Toast.LENGTH_SHORT);
            this.finish();
        }
        camera = new HKCamera(IP, devicePort,username, pwd);

        login();

        Handler handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                // TODO Auto-generated method stub
                super.handleMessage(msg);
                openVideo();
            }
        };

        Message msg = new Message();
        handler.sendMessageDelayed(msg, 2000);

        txt_video_title.setText(name);
        Relative_video_title_id = (RelativeLayout)this.findViewById(R.id.Relative_video_title_id);
        FrameLayout_video = (FrameLayout)this.findViewById(R.id.FrameLayout_video);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putInt("m_iPort", m_iPort);
        super.onSaveInstanceState(outState);
        Log.i(TAG, "onSaveInstanceState");
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        m_iPort = savedInstanceState.getInt("m_iPort");
        super.onRestoreInstanceState(savedInstanceState);
        Log.i(TAG, "onRestoreInstanceState");
    }

    @Override
    protected void onPause() {
        super.onPause();
        colseVideo();
        loginOut();
    }


    @Override
    protected void onDestroy() {
//        PgyUpdateManager.unregister();
        super.onDestroy();

        Cleanup();
    }

    /**
     * @param NULL [in]
     * @param NULL [out]
     * @return true - success;false - fail
     * @fn initeSdk
     * @author zhuzhenlei
     * @brief SDK init
     */
    private boolean initeSdk() {
        // init net sdk
        if (!HCNetSDK.getInstance().NET_DVR_Init()) {
            Log.e(TAG, "HCNetSDK init is failed!");
            return false;
        }
        HCNetSDK.getInstance().NET_DVR_SetLogToFile(3, "/mnt/sdcard/sdklog/",
                true);
        return true;
    }

    // GUI init
    private boolean initeActivity() {
        findViews();
        setListeners();

        return true;
    }

    private void ChangeSingleSurFace(boolean bSingle,boolean bFull) {
        DisplayMetrics metric = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metric);

        for (int i = 0; i < 4; i++) {
            if (playView[i] == null) {
                playView[i] = new PlaySurfaceView(this);
                playView[i].setParam(metric.widthPixels);

                FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
                        FrameLayout.LayoutParams.WRAP_CONTENT,
                        FrameLayout.LayoutParams.WRAP_CONTENT);
                params.bottomMargin = playView[i].getM_iHeight() - (i / 2)
                        * playView[i].getM_iHeight();
                params.leftMargin = (i % 2) * playView[i].getM_iWidth();
                params.gravity = Gravity.TOP | Gravity.LEFT;
                addContentView(playView[i], params);
                playView[i].setVisibility(View.INVISIBLE);

            }
        }

        if (bSingle) {
            // ��·ֻ��ʾ����1
            for (int i = 0; i < 4; ++i) {
                playView[i].setVisibility(View.INVISIBLE);
            }
            playView[0].setParam(metric.widthPixels * 2);
            FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
                    FrameLayout.LayoutParams.WRAP_CONTENT,
                    FrameLayout.LayoutParams.WRAP_CONTENT);
            params.bottomMargin = playView[3].getM_iHeight() - (3 / 2)
                    * playView[3].getM_iHeight();
            params.topMargin = bFull?0:120;
            params.leftMargin = 0;
            // params.
            params.gravity = Gravity.TOP | Gravity.LEFT;
            playView[0].setLayoutParams(params);
            playView[0].setVisibility(View.VISIBLE);
            playView[0].setOnTouchListener(this);

            //mBtFull = new Button(this);
            //mBtFull.setText("abc");
            //mBtFull.setTextColor(Color.BLACK);

            //setBtnAttribute( codeBtn, btnContent, index, Color.TRANSPARENT, Color.BLACK, 24 );
            //FrameLayout_video.addView(mBtFull);

            playView[0].setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                    Relative_video_title_id.setVisibility(View.VISIBLE);
                    return true;
                }
            });
        } else {
            for (int i = 0; i < 4; ++i) {
                playView[i].setVisibility(View.VISIBLE);
            }

            playView[0].setParam(metric.widthPixels);
            FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
                    FrameLayout.LayoutParams.WRAP_CONTENT,
                    FrameLayout.LayoutParams.WRAP_CONTENT);
            params.bottomMargin = playView[0].getM_iHeight() - (0 / 2)
                    * playView[0].getM_iHeight();
            params.leftMargin = (0 % 2) * playView[0].getM_iWidth();
            params.gravity = Gravity.TOP | Gravity.LEFT;
            playView[0].setLayoutParams(params);
            playView[0].setOnTouchListener(this);

            playView[0].setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                    Relative_video_title_id.setVisibility(View.VISIBLE);
                    return true;
                }
            });
        }

    }

    // get controller instance
    private void findViews() {
        m_oLoginBtn = (Button) findViewById(R.id.btn_Login);
        m_oPreviewBtn = (Button) findViewById(R.id.btn_Preview);
        m_oPlaybackBtn = (Button) findViewById(R.id.btn_Playback);
        m_oParamCfgBtn = (Button) findViewById(R.id.btn_ParamCfg);
        m_oCaptureBtn = (Button) findViewById(R.id.btn_Capture);
        m_oRecordBtn = (Button) findViewById(R.id.btn_Record);
        m_oTalkBtn = (Button) findViewById(R.id.btn_Talk);
        m_oPTZBtn = (Button) findViewById(R.id.btn_PTZ);
        m_oOtherBtn = (Button) findViewById(R.id.btn_OTHER);

        txt_video_title = (TextView) this.findViewById(R.id.txt_video_title);
        btn_back = (Button) findViewById(R.id.btn_back);
        btn_back.setOnClickListener(this);

        btn_full = (Button) findViewById(R.id.btn_full);
        btn_full.setOnClickListener(this);
        Button close = (Button) findViewById(R.id.close_full_video_id);
        close.setOnClickListener(this);

        //m_oIPAddr = (EditText) findViewById(R.id.EDT_IPAddr);
        //m_oPort = (EditText) findViewById(R.id.EDT_Port);
        //m_oUser = (EditText) findViewById(R.id.EDT_User);
        //m_oPsd = (EditText) findViewById(R.id.EDT_Psd);
    }

    // listen
    private void setListeners() {
        m_oLoginBtn.setOnClickListener(Login_Listener);
        m_oPreviewBtn.setOnClickListener(Preview_Listener);
        m_oPlaybackBtn.setOnClickListener(Playback_Listener);
        m_oParamCfgBtn.setOnClickListener(ParamCfg_Listener);
        m_oCaptureBtn.setOnClickListener(Capture_Listener);
        m_oRecordBtn.setOnClickListener(Record_Listener);
        m_oTalkBtn.setOnClickListener(Talk_Listener);
        m_oOtherBtn.setOnClickListener(OtherFunc_Listener);
        m_oPTZBtn.setOnTouchListener(PTZ_Listener);
    }

    // ptz listener
    private OnTouchListener PTZ_Listener = new OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            try {
                if (m_iLogID < 0) {
                    Log.e(TAG, "please login on a device first");
                    return false;
                }
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    if (m_bPTZL == false) {
                        if (!HCNetSDK.getInstance().NET_DVR_PTZControl_Other(
                                m_iLogID, m_iStartChan, PTZCommand.PAN_LEFT, 0)) {
                            Log.e(TAG,
                                    "start PAN_LEFT failed with error code: "
                                            + HCNetSDK.getInstance()
                                            .NET_DVR_GetLastError());
                        } else {
                            Log.i(TAG, "start PAN_LEFT succ");
                        }
                    } else {
                        if (!HCNetSDK.getInstance()
                                .NET_DVR_PTZControl_Other(m_iLogID,
                                        m_iStartChan, PTZCommand.PAN_RIGHT, 0)) {
                            Log.e(TAG,
                                    "start PAN_RIGHT failed with error code: "
                                            + HCNetSDK.getInstance()
                                            .NET_DVR_GetLastError());
                        } else {
                            Log.i(TAG, "start PAN_RIGHT succ");
                        }
                    }
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    if (m_bPTZL == false) {
                        if (!HCNetSDK.getInstance().NET_DVR_PTZControl_Other(
                                m_iLogID, m_iStartChan, PTZCommand.PAN_LEFT, 1)) {
                            Log.e(TAG, "stop PAN_LEFT failed with error code: "
                                    + HCNetSDK.getInstance()
                                    .NET_DVR_GetLastError());
                        } else {
                            Log.i(TAG, "stop PAN_LEFT succ");
                        }
                        m_bPTZL = true;
                        m_oPTZBtn.setText("PTZ(R)");
                    } else {
                        if (!HCNetSDK.getInstance()
                                .NET_DVR_PTZControl_Other(m_iLogID,
                                        m_iStartChan, PTZCommand.PAN_RIGHT, 1)) {
                            Log.e(TAG,
                                    "stop PAN_RIGHT failed with error code: "
                                            + HCNetSDK.getInstance()
                                            .NET_DVR_GetLastError());
                        } else {
                            Log.i(TAG, "stop PAN_RIGHT succ");
                        }
                        m_bPTZL = false;
                        m_oPTZBtn.setText("PTZ(L)");
                    }
                }
                return true;
            } catch (Exception err) {
                Log.e(TAG, "error: " + err.toString());
                return false;
            }
        }
    };
    // preset listener
    private OnClickListener OtherFunc_Listener = new OnClickListener() {
        public void onClick(View v) {
            // PTZTest.TEST_PTZ(m_iPlayID, m_iLogID, m_iStartChan);
            // ConfigTest.Test_ScreenConfig(m_iLogID, m_iStartChan);
            // PTZTest.TEST_PTZ(m_iPlayID, m_iLogID, m_iStartChan);

            /*
             * try { //PictureTest.PicUpload(m_iLogID); } catch
             * (InterruptedException e) { // TODO Auto-generated catch block
             * e.printStackTrace(); }
             */

            // PictureTest.BaseMap(m_iLogID);
            // DecodeTest.PicPreview(m_iLogID);
            // ManageTest.TEST_Manage(m_iLogID);
            // AlarmTest.Test_SetupAlarm(m_iLogID);
            // OtherFunction.TEST_OtherFunc(m_iPlayID, m_iLogID, m_iStartChan);
            // JNATest.TEST_Config(m_iPlayID, m_iLogID, m_iStartChan);

            //ConfigTest.TEST_Config(m_iPlayID, m_iLogID, m_iStartChan);
            // HttpTest.Test_HTTP();
            //ScreenTest.TEST_Screen(m_iLogID);
        }
    };
    // Talk listener
    private OnClickListener Talk_Listener = new OnClickListener() {
        public void onClick(View v) {
            try {
//                if (m_bTalkOn == false) {
//                    if (VoiceTalk.startVoiceTalk(m_iLogID) >= 0) {
//                        m_bTalkOn = true;
//                        m_oTalkBtn.setText("Stop");
//                    }
//                } else {
//                    if (VoiceTalk.stopVoiceTalk()) {
//                        m_bTalkOn = false;
//                        m_oTalkBtn.setText("Talk");
//                    }
//                }
            } catch (Exception err) {
                Log.e(TAG, "error: " + err.toString());
            }
        }
    };
    // record listener
    private OnClickListener Record_Listener = new OnClickListener() {
        public void onClick(View v) {
            if (!m_bSaveRealData) {
                if (!HCNetSDK.getInstance().NET_DVR_SaveRealData(m_iPlayID,
                        "/sdcard/test.mp4")) {
                    System.out.println("NET_DVR_SaveRealData failed! error: "
                            + HCNetSDK.getInstance().NET_DVR_GetLastError());
                    return;
                } else {
                    System.out.println("NET_DVR_SaveRealData succ!");
                }
                m_bSaveRealData = true;
            } else {
                if (!HCNetSDK.getInstance().NET_DVR_StopSaveRealData(m_iPlayID)) {
                    System.out
                            .println("NET_DVR_StopSaveRealData failed! error: "
                                    + HCNetSDK.getInstance()
                                    .NET_DVR_GetLastError());
                } else {
                    System.out.println("NET_DVR_StopSaveRealData succ!");
                }
                m_bSaveRealData = false;
            }
        }
    };
    // capture listener
    private OnClickListener Capture_Listener = new OnClickListener() {
        public void onClick(View v) {
            try {
                if (m_iPort < 0) {
                    Log.e(TAG, "please start preview first");
                    return;
                }
                Player.MPInteger stWidth = new Player.MPInteger();
                Player.MPInteger stHeight = new Player.MPInteger();
                if (!Player.getInstance().getPictureSize(m_iPort, stWidth,
                        stHeight)) {
                    Log.e(TAG, "getPictureSize failed with error code:"
                            + Player.getInstance().getLastError(m_iPort));
                    return;
                }
                int nSize = 5 * stWidth.value * stHeight.value;
                byte[] picBuf = new byte[nSize];
                Player.MPInteger stSize = new Player.MPInteger();
                if (!Player.getInstance()
                        .getBMP(m_iPort, picBuf, nSize, stSize)) {
                    Log.e(TAG, "getBMP failed with error code:"
                            + Player.getInstance().getLastError(m_iPort));
                    return;
                }

                SimpleDateFormat sDateFormat = new SimpleDateFormat(
                        "yyyy-MM-dd-hh:mm:ss");
                String date = sDateFormat.format(new java.util.Date());
                FileOutputStream file = new FileOutputStream("/mnt/sdcard/"
                        + date + ".bmp");
                file.write(picBuf, 0, stSize.value);
                file.close();
            } catch (Exception err) {
                Log.e(TAG, "error: " + err.toString());
            }
        }
    };
    // playback listener
    // private Button.OnClickListener Playback_Listener = new
    // Button.OnClickListener() {
    //
    // public void onClick(View v) {
    // try {
    // if (m_iLogID < 0) {
    // Log.e(TAG, "please login on a device first");
    // return;
    // }
    // if (m_iPlaybackID < 0) {
    // if (m_iPlayID >= 0) {
    // Log.i(TAG, "Please stop preview first");
    // return;
    // }
    //
    // ChangeSingleSurFace(true);
    //
    // NET_DVR_TIME struBegin = new NET_DVR_TIME();
    // NET_DVR_TIME struEnd = new NET_DVR_TIME();
    //
    // struBegin.dwYear = 2016;
    // struBegin.dwMonth = 6;
    // struBegin.dwDay = 28;
    //
    // struEnd.dwYear = 2016;
    // struEnd.dwMonth = 6;
    // struEnd.dwDay = 29;
    //
    // NET_DVR_VOD_PARA struVod = new NET_DVR_VOD_PARA();
    // struVod.struBeginTime = struBegin;
    // struVod.struEndTime = struEnd;
    // struVod.byStreamType = 0;
    // struVod.struIDInfo.dwChannel = m_iStartChan;
    // struVod.hWnd = playView[0].getHolder().getSurface();
    //
    //
    //
    // m_iPlaybackID =
    // HCNetSDK.getInstance().NET_DVR_PlayBackByTime_V40(m_iLogID, struVod);
    //
    // // m_iPlaybackID = HCNetSDK.getInstance()
    // // .NET_DVR_PlayBackByTime(m_iLogID, m_iStartChan,
    // // struBegin, struEnd);
    // if (m_iPlaybackID >= 0) {
    // NET_DVR_PLAYBACK_INFO struPlaybackInfo = null;
    // if (!HCNetSDK
    // .getInstance()
    // .NET_DVR_PlayBackControl_V40(
    // m_iPlaybackID,
    // PlaybackControlCommand.NET_DVR_PLAYSTART,
    // null, 0, struPlaybackInfo)) {
    // Log.e(TAG, "net sdk playback start failed!");
    // return;
    // }
    // m_bStopPlayback = false;
    // m_oPlaybackBtn.setText("Stop");
    //
    // Thread thread = new Thread() {
    // public void run() {
    // int nProgress = -1;
    // while (true) {
    // nProgress = HCNetSDK.getInstance()
    // .NET_DVR_GetPlayBackPos(
    // m_iPlaybackID);
    // System.out
    // .println("NET_DVR_GetPlayBackPos:"
    // + nProgress);
    // if (nProgress < 0 || nProgress >= 100) {
    // break;
    // }
    //
    // try {
    // Thread.sleep(1000);
    // } catch (InterruptedException e) {
    // // TODO Auto-generated catch block
    // e.printStackTrace();
    // }
    // }
    // }
    // };
    // thread.start();
    // } else {
    // Log.i(TAG,
    // "NET_DVR_PlayBackByTime failed, error code: "
    // + HCNetSDK.getInstance()
    // .NET_DVR_GetLastError());
    // }
    // } else {
    // m_bStopPlayback = true;
    // if (!HCNetSDK.getInstance().NET_DVR_StopPlayBack(
    // m_iPlaybackID)) {
    // Log.e(TAG, "net sdk stop playback failed");
    // }
    // m_oPlaybackBtn.setText("Playback");
    // m_iPlaybackID = -1;
    //
    // ChangeSingleSurFace(false);
    // }
    // } catch (Exception err) {
    // Log.e(TAG, "error: " + err.toString());
    // }
    // }
    // };

    private OnClickListener Playback_Listener = new OnClickListener() {

        public void onClick(View v) {
            try {
                if (m_iLogID < 0) {
                    Log.e(TAG, "please login on a device first");
                    return;
                }
                if (m_iPlaybackID < 0) {
                    if (m_iPlayID >= 0) {
                        Log.i(TAG, "Please stop preview first");
                        return;
                    }

                    ChangeSingleSurFace(true,false);
                    m_iPlaybackID = HCNetSDK.getInstance()
                            .NET_DVR_PlayBackByName(m_iLogID,
                                    new String("ch0002_00010000459000200"), playView[0].getHolder().getSurface());
                    if (m_iPlaybackID >= 0) {
                        NET_DVR_PLAYBACK_INFO struPlaybackInfo = null;
                        if (!HCNetSDK
                                .getInstance()
                                .NET_DVR_PlayBackControl_V40(
                                        m_iPlaybackID,
                                        PlaybackControlCommand.NET_DVR_PLAYSTART,
                                        null, 0, struPlaybackInfo)) {
                            Log.e(TAG, "net sdk playback start failed!");
                            return;
                        }
                        m_bStopPlayback = false;
                        m_oPlaybackBtn.setText("Stop");

                        Thread thread = new Thread() {
                            public void run() {
                                int nProgress = -1;
                                while (true) {
                                    nProgress = HCNetSDK.getInstance()
                                            .NET_DVR_GetPlayBackPos(
                                                    m_iPlaybackID);
                                    System.out
                                            .println("NET_DVR_GetPlayBackPos:"
                                                    + nProgress);
                                    if (nProgress < 0 || nProgress >= 100) {
                                        break;
                                    }
                                    try {
                                        Thread.sleep(1000);
                                    } catch (InterruptedException e) { // TODO
                                        // Auto-generated
                                        // catch
                                        // block
                                        e.printStackTrace();
                                    }

                                }
                            }
                        };
                        thread.start();
                    } else {
                        Log.i(TAG,
                                "NET_DVR_PlayBackByName failed, error code: "
                                        + HCNetSDK.getInstance()
                                        .NET_DVR_GetLastError());
                    }
                } else {
                    m_bStopPlayback = true;
                    if (!HCNetSDK.getInstance().NET_DVR_StopPlayBack(
                            m_iPlaybackID)) {
                        Log.e(TAG, "net sdk stop playback failed");
                    } // player stop play
                    m_oPlaybackBtn.setText("Playback");
                    m_iPlaybackID = -1;

                    ChangeSingleSurFace(false,false);
                }
            } catch (Exception err) {
                Log.e(TAG, "error: " + err.toString());
            }
        }
    };

    // login listener
    private OnClickListener Login_Listener = new OnClickListener() {
        public void onClick(View v) {
            try {
                if (m_iLogID < 0) {
                    // login on the device
                    m_iLogID = loginDevice();
                    if (m_iLogID < 0) {
                        Log.e(TAG, "This device logins failed!");
                        return;
                    } else {
                        System.out.println("m_iLogID=" + m_iLogID);
                    }
                    // get instance of exception callback and set
                    ExceptionCallBack oexceptionCbf = getExceptiongCbf();
                    if (oexceptionCbf == null) {
                        Log.e(TAG, "ExceptionCallBack object is failed!");
                        return;
                    }

                    if (!HCNetSDK.getInstance().NET_DVR_SetExceptionCallBack(
                            oexceptionCbf)) {
                        Log.e(TAG, "NET_DVR_SetExceptionCallBack is failed!");
                        return;
                    }

                    m_oLoginBtn.setText("断开设备");
                    Log.i(TAG,
                            "Login sucess ****************************1***************************");
                } else {
                    // whether we have logout
                    if (!HCNetSDK.getInstance().NET_DVR_Logout_V30(m_iLogID)) {
                        Log.e(TAG, " NET_DVR_Logout is failed!");
                        return;
                    }
                    m_oLoginBtn.setText("连接设备");
                    m_iLogID = -1;
                }
            } catch (Exception err) {
                Log.e(TAG, "error: " + err.toString());
            }
        }
    };

    private void login() {
        if (m_iLogID < 0) {
            // login on the device
            m_iLogID = loginDevice();
            if (m_iLogID < 0) {
                Log.e(TAG, "This device logins failed!");
                return;
            } else {
                System.out.println("m_iLogID=" + m_iLogID);
            }
            // get instance of exception callback and set
            ExceptionCallBack oexceptionCbf = getExceptiongCbf();
            if (oexceptionCbf == null) {
                Log.e(TAG, "ExceptionCallBack object is failed!");
                return;
            }

            if (!HCNetSDK.getInstance().NET_DVR_SetExceptionCallBack(
                    oexceptionCbf)) {
                Log.e(TAG, "NET_DVR_SetExceptionCallBack is failed!");
                return;
            }

            //m_oLoginBtn.setText("断开设备");
            Log.i(TAG,
                    "Login sucess ****************************1***************************");
        }
    }

    private void loginOut() {
        if (m_iLogID >= 0) {
            // whether we have logout
            if (!HCNetSDK.getInstance().NET_DVR_Logout_V30(m_iLogID)) {
                Log.e(TAG, " NET_DVR_Logout is failed!");
                return;
            }
            m_oLoginBtn.setText("连接设备");
            m_iLogID = -1;
        }
    }

    // Preview listener
    private OnClickListener Preview_Listener = new OnClickListener() {
        public void onClick(View v) {
            try {
//                ((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE))
//                        .hideSoftInputFromWindow(DemoActivity.this
//                                .getCurrentFocus().getWindowToken(),
//                                InputMethodManager.HIDE_NOT_ALWAYS);
                if (m_iLogID < 0) {
                    Log.e(TAG, "please login on device first");
                    return;
                }

                if (m_iPlaybackID >= 0) {
                    Log.i(TAG, "Please stop palyback first");
                    return;
                }

                if (m_bNeedDecode) {
                    if (m_iChanNum > 1)// preview more than a channel
                    {
                        if (!m_bMultiPlay) {
                            startMultiPreview();
                            // startMultiPreview();
                            m_bMultiPlay = true;
                            m_oPreviewBtn.setText("关闭视频");
                        } else {
                            stopMultiPreview();
                            m_bMultiPlay = false;
                            m_oPreviewBtn.setText("打开视频");
                        }
                    } else // preivew a channel
                    {
                        if (m_iPlayID < 0) {
                            startSinglePreview();
                        } else {
                            stopSinglePreview();
                            m_oPreviewBtn.setText("打开视频");
                        }
                    }
                } else {

                }
            } catch (Exception err) {
                Log.e(TAG, "error: " + err.toString());
            }
        }
    };

    private void openVideo() {
        try {
            if (m_iLogID < 0) {
                Log.e(TAG, "please login on device first");
                return;
            }

            if (m_iPlaybackID >= 0) {
                Log.i(TAG, "Please stop palyback first");
                return;
            }

            if (m_bNeedDecode) {
                if (m_iChanNum > 1)// preview more than a channel
                {
                    if (!m_bMultiPlay) {
                        startMultiPreview();
                        // startMultiPreview();
                        m_bMultiPlay = true;
                        //m_oPreviewBtn.setText("关闭视频");
                    } else {
                        stopMultiPreview();
                        m_bMultiPlay = false;
                        //m_oPreviewBtn.setText("打开视频");
                    }
                } else // preivew a channel
                {
                    if (m_iPlayID < 0) {
                        startSinglePreview();
                    } else {
                        stopSinglePreview();
                        //m_oPreviewBtn.setText("打开视频");
                    }
                }
            } else {

            }
        } catch (Exception err) {
            Log.e(TAG, "error: " + err.toString());
        }
    }

    private void colseVideo() {
        if (m_iLogID < 0) {
            Log.e(TAG, "please login on device first");
            return;
        }

        if (m_iPlaybackID >= 0) {
            Log.i(TAG, "Please stop palyback first");
            return;
        }

        if (m_iPlayID >= 0) {
            stopSinglePreview();
        }
    }

    // configuration listener
    private OnClickListener ParamCfg_Listener = new OnClickListener() {
        public void onClick(View v) {
            try {
                paramCfg(m_iLogID);
            } catch (Exception err) {
                Log.e(TAG, "error: " + err.toString());
            }
        }
    };

    private void startSinglePreview() {
        if (m_iPlaybackID >= 0) {
            Log.i(TAG, "Please stop palyback first");
            return;
        }

        Log.i(TAG, "m_iStartChan:" + m_iStartChan);

        NET_DVR_PREVIEWINFO previewInfo = new NET_DVR_PREVIEWINFO();
        previewInfo.lChannel = m_iStartChan;
        previewInfo.dwStreamType = 0; // substream
        previewInfo.bBlocked = 1;
        previewInfo.hHwnd = playView[0].getHolder();
        // HCNetSDK start preview
        m_iPlayID = HCNetSDK.getInstance().NET_DVR_RealPlay_V40(m_iLogID,
                previewInfo, null);
        if (m_iPlayID < 0) {
            Log.e(TAG, "NET_DVR_RealPlay is failed!Err:"
                    + HCNetSDK.getInstance().NET_DVR_GetLastError());
            return;
        }

        Log.i(TAG,
                "NetSdk Play sucess ***********************3***************************");
        // m_oPreviewBtn.setText("关闭视频");
    }

    private void startMultiPreview() {

        for (int i = 0; i < 4; i++) {
            playView[i].startPreview(m_iLogID, m_iStartChan + i);
        }

        // new Thread(new Runnable() {
        //
        // @Override
        // public void run() {
        // // TODO Auto-generated method stub
        // for (int i = 0; i < 4; i++) {
        // while (!playView[i].bCreate) {
        // try {
        // Thread.sleep(100);
        // Log.i(TAG, "wait for surface create");
        // } catch (InterruptedException e) {
        // // TODO Auto-generated catch block
        // e.printStackTrace();
        // }
        // }
        //
        // NET_DVR_PREVIEWINFO previewInfo = new NET_DVR_PREVIEWINFO();
        // previewInfo.lChannel = m_iStartChan + i;
        // previewInfo.dwStreamType = 0; // substream
        // previewInfo.bBlocked = 1;
        // previewInfo.hHwnd = playView[i].getHolder();
        //
        // playView[i].m_iPreviewHandle =
        // HCNetSDK.getInstance().NET_DVR_RealPlay_V40(
        // m_iLogID, previewInfo, null);
        // if (playView[i].m_iPreviewHandle < 0) {
        // Log.e(TAG, "NET_DVR_RealPlay is failed!Err:"
        // + HCNetSDK.getInstance().NET_DVR_GetLastError());
        // }
        // }
        // }
        // }).start();

        m_iPlayID = playView[0].m_iPreviewHandle;
    }

    private void stopMultiPreview() {
        int i = 0;
        for (i = 0; i < 4; i++) {
            playView[i].stopPreview();
        }
        m_iPlayID = -1;
    }

    /**
     * @param NULL [in]
     * @param NULL [out]
     * @return NULL
     * @fn stopSinglePreview
     * @author zhuzhenlei
     * @brief stop preview
     */
    private void stopSinglePreview() {
        if (m_iPlayID < 0) {
            Log.e(TAG, "m_iPlayID < 0");
            return;
        }

        // net sdk stop preview
        if (!HCNetSDK.getInstance().NET_DVR_StopRealPlay(m_iPlayID)) {
            Log.e(TAG, "StopRealPlay is failed!Err:"
                    + HCNetSDK.getInstance().NET_DVR_GetLastError());
            return;
        }

        m_iPlayID = -1;
    }

    /**
     * @param NULL [in]
     * @param NULL [out]
     * @return login ID
     * @fn loginNormalDevice
     * @author zhuzhenlei
     * @brief login on device
     */
    private int loginNormalDevice() {
        // get instance
        m_oNetDvrDeviceInfoV30 = new NET_DVR_DEVICEINFO_V30();
        if (null == m_oNetDvrDeviceInfoV30) {
            Log.e(TAG, "HKNetDvrDeviceInfoV30 new is failed!");
            return -1;
        }

        //String strIP = m_oIPAddr.getText().toString();
        //int nPort = Integer.parseInt(m_oPort.getText().toString());
        //String strUser = m_oUser.getText().toString();
        //String strPsd = m_oPsd.getText().toString();

        String strIP = camera.CamIP.toString();
        int nPort = Integer.parseInt(camera.CamPort.toString());
        String strUser = camera.CamLoginName.toString();
        String strPsd = camera.CamLoginPwd.toString();


        // call NET_DVR_Login_v30 to login on, port 8000 as default
        int iLogID = HCNetSDK.getInstance().NET_DVR_Login_V30(strIP, nPort,
                strUser, strPsd, m_oNetDvrDeviceInfoV30);
        if (iLogID < 0) {
            Log.e(TAG, "NET_DVR_Login is failed!Err:"
                    + HCNetSDK.getInstance().NET_DVR_GetLastError());
            return -1;
        }
        if (m_oNetDvrDeviceInfoV30.byChanNum > 0) {
            m_iStartChan = m_oNetDvrDeviceInfoV30.byStartChan;
            m_iChanNum = m_oNetDvrDeviceInfoV30.byChanNum;
        } else if (m_oNetDvrDeviceInfoV30.byIPChanNum > 0) {
            m_iStartChan = m_oNetDvrDeviceInfoV30.byStartDChan;
            m_iChanNum = 1/*m_oNetDvrDeviceInfoV30.byIPChanNum
                    + m_oNetDvrDeviceInfoV30.byHighDChanNum * 256*/;
        }

        if (m_iChanNum > 1) {
            ChangeSingleSurFace(false,false);
        } else {
            ChangeSingleSurFace(true,false);
        }
        Log.i(TAG, "NET_DVR_Login is Successful!");

        return iLogID;
    }

    public static void Test_XMLAbility(int iUserID) {
        byte[] arrayOutBuf = new byte[64 * 1024];
        INT_PTR intPtr = new INT_PTR();
        String strInput = new String(
                "<AlarmHostAbility version=\"2.0\"></AlarmHostAbility>");
        byte[] arrayInBuf = new byte[8 * 1024];
        arrayInBuf = strInput.getBytes();
        if (!HCNetSDK.getInstance().NET_DVR_GetXMLAbility(iUserID,
                HCNetSDK.DEVICE_ABILITY_INFO, arrayInBuf, strInput.length(),
                arrayOutBuf, 64 * 1024, intPtr)) {
            System.out.println("get DEVICE_ABILITY_INFO faild!" + " err: "
                    + HCNetSDK.getInstance().NET_DVR_GetLastError());
        } else {
            System.out.println("get DEVICE_ABILITY_INFO succ!");
        }
    }

    /**
     * @param NULL [in]
     * @param NULL [out]
     * @return login ID
     * @fn loginEzvizDevice
     * @author liuyu6
     * @brief login on ezviz device
     */
    private int loginEzvizDevice() {
        return -1;
        /*
         * NET_DVR_OPEN_EZVIZ_USER_LOGIN_INFO struLoginInfo = new
         * NET_DVR_OPEN_EZVIZ_USER_LOGIN_INFO(); NET_DVR_DEVICEINFO_V30
         * struDeviceInfo = new NET_DVR_DEVICEINFO_V30();
         * 
         * //String strInput = new String("pbsgp.p2papi.ezviz7.com"); String
         * strInput = new String("open.ys7.com"); //String strInput = new
         * String("pbdev.ys7.com"); //String strInput = new
         * String("183.136.184.67"); byte[] byInput = strInput.getBytes();
         * System.arraycopy(byInput, 0, struLoginInfo.sEzvizServerAddress, 0,
         * byInput.length);
         * 
         * struLoginInfo.wPort = 443;
         * 
         * strInput = new
         * String("at.43anfq0q9k8zt06vd0ppalfhc4bj177p-3k4ovrh4vu-105zgp6-jgt8edqst"
         * ); byInput = strInput.getBytes(); System.arraycopy(byInput, 0,
         * struLoginInfo.sAccessToken, 0, byInput.length);
         * 
         * //strInput = new String("67a7daedd4654dc5be329f2289914859");
         * //byInput = strInput.getBytes(); //System.arraycopy(byInput, 0,
         * struLoginInfo.sSessionID, 0, byInput.length);
         * 
         * //strInput = new String("ae1b9af9dcac4caeb88da6dbbf2dd8d5"); strInput
         * = new String("com.hik.visualintercom"); byInput =
         * strInput.getBytes(); System.arraycopy(byInput, 0,
         * struLoginInfo.sAppID, 0, byInput.length);
         * 
         * //strInput = new String("78313dadecd92bd11623638d57aa5128"); strInput
         * = new String("226f102a99ad0e078504d380b9ddf760"); byInput =
         * strInput.getBytes(); System.arraycopy(byInput, 0,
         * struLoginInfo.sFeatureCode, 0, byInput.length);
         * 
         * //strInput = new
         * String("https://pbopen.ys7.com:443/api/device/transmission");
         * strInput = new String("/api/device/transmission"); byInput =
         * strInput.getBytes(); System.arraycopy(byInput, 0, struLoginInfo.sUrl,
         * 0, byInput.length);
         * 
         * strInput = new String("520247131"); byInput = strInput.getBytes();
         * System.arraycopy(byInput, 0, struLoginInfo.sDeviceID, 0,
         * byInput.length);
         * 
         * strInput = new String("2"); byInput = strInput.getBytes();
         * System.arraycopy(byInput, 0, struLoginInfo.sClientType, 0,
         * byInput.length);
         * 
         * strInput = new String("UNKNOWN"); byInput = strInput.getBytes();
         * System.arraycopy(byInput, 0, struLoginInfo.sNetType, 0,
         * byInput.length);
         * 
         * strInput = new String("5.0.1"); byInput = strInput.getBytes();
         * System.arraycopy(byInput, 0, struLoginInfo.sOsVersion, 0,
         * byInput.length);
         * 
         * strInput = new String("v.5.1.5.30"); byInput = strInput.getBytes();
         * System.arraycopy(byInput, 0, struLoginInfo.sSdkVersion, 0,
         * byInput.length);
         * 
         * int iUserID = -1;
         * 
         * iUserID =
         * HCNetSDK.getInstance().NET_DVR_CreateOpenEzvizUser(struLoginInfo,
         * struDeviceInfo);
         * 
         * if (-1 == iUserID) { System.out.println("NET_DVR_CreateOpenEzvizUser"
         * + " err: " + HCNetSDK.getInstance().NET_DVR_GetLastError()); return
         * -1; } else {
         * System.out.println("NET_DVR_CreateOpenEzvizUser success"); }
         * 
         * Test_XMLAbility(iUserID); Test_XMLAbility(iUserID);
         * Test_XMLAbility(iUserID);
         * 
         * return iUserID;
         */

    }

    /**
     * @param NULL [in]
     * @param NULL [out]
     * @return login ID
     * @fn loginDevice
     * @author zhangqing
     * @brief login on device
     */
    private int loginDevice() {
        int iLogID = -1;

        iLogID = loginNormalDevice();

        // iLogID = JNATest.TEST_EzvizLogin();
        // iLogID = loginEzvizDevice();

        return iLogID;
    }

    /**
     * @param iUserID - login ID [in]
     * @param NULL    [out]
     * @return NULL
     * @fn paramCfg
     * @author zhuzhenlei
     * @brief configuration
     */
    private void paramCfg(final int iUserID) {
        // whether have logined on
        if (iUserID < 0) {
            Log.e(TAG, "iUserID < 0");
            return;
        }

        NET_DVR_COMPRESSIONCFG_V30 struCompress = new NET_DVR_COMPRESSIONCFG_V30();
        if (!HCNetSDK.getInstance().NET_DVR_GetDVRConfig(iUserID,
                HCNetSDK.NET_DVR_GET_COMPRESSCFG_V30, m_iStartChan,
                struCompress)) {
            Log.e(TAG, "NET_DVR_GET_COMPRESSCFG_V30 failed with error code:"
                    + HCNetSDK.getInstance().NET_DVR_GetLastError());
        } else {
            Log.i(TAG, "NET_DVR_GET_COMPRESSCFG_V30 succ");
        }
        // set substream resolution to cif
        struCompress.struNetPara.byResolution = 1;
        if (!HCNetSDK.getInstance().NET_DVR_SetDVRConfig(iUserID,
                HCNetSDK.NET_DVR_SET_COMPRESSCFG_V30, m_iStartChan,
                struCompress)) {
            Log.e(TAG, "NET_DVR_SET_COMPRESSCFG_V30 failed with error code:"
                    + HCNetSDK.getInstance().NET_DVR_GetLastError());
        } else {
            Log.i(TAG, "NET_DVR_SET_COMPRESSCFG_V30 succ");
        }
    }

    /**
     * @param NULL [in]
     * @param NULL [out]
     * @return exception instance
     * @fn getExceptiongCbf
     * @author zhuzhenlei
     * @brief process exception
     */
    private ExceptionCallBack getExceptiongCbf() {
        ExceptionCallBack oExceptionCbf = new ExceptionCallBack() {
            public void fExceptionCallBack(int iType, int iUserID, int iHandle) {
                System.out.println("recv exception, type:" + iType);
            }
        };
        return oExceptionCbf;
    }

    /**
     * @param NULL [in]
     * @param NULL [out]
     * @return NULL
     * @fn Cleanup
     * @author zhuzhenlei
     * @brief cleanup
     */
    public void Cleanup() {
        // release net SDK resource
        HCNetSDK.getInstance().NET_DVR_Cleanup();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_back:
                this.finish();
                break;
            case R.id.btn_full:


                //getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                 //       WindowManager.LayoutParams.FLAG_FULLSCREEN);

                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

                //Relative_video_title_id.setVisibility(View.GONE);
//                RelativeLayout.LayoutParams layoutParams=
//                        new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.FILL_PARENT, RelativeLayout.LayoutParams.FILL_PARENT);
//                layoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
//                layoutParams.addRule(RelativeLayout.ALIGN_PARENT_TOP);
//                layoutParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
//                layoutParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);

//               FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
//               FrameLayout.LayoutParams.FILL_PARENT, FrameLayout.LayoutParams.FILL_PARENT);
//
//                params.leftMargin = 0;
//                params.rightMargin = 0;
//                params.topMargin = 0;
//                params.bottomMargin = 0;
//
//                params.gravity = Gravity.CENTER;
//
//                playView[0].setLayoutParams(params);

                ChangeSingleSurFace(true,true);
                startSinglePreview();

                break;
            case R.id.close_full_video_id:
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                Relative_video_title_id.setVisibility(View.VISIBLE);
                //startMultiPreview();
                break;
            default:
                break;
        }
    }

    private boolean isDown = true;
    private boolean isMove = false;

    private float x1 = 0;
    private float x2 = 0;
    private float y1 = 0;
    private float y2 = 0;

    private GestureDetector mGesture = new GestureDetector(this);

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                x1 = event.getX();
                y1 = event.getY();
                break;
            case MotionEvent.ACTION_UP:
                isDown = false;
                break;
            case MotionEvent.ACTION_MOVE:
                x2 = event.getX();
                y2 = event.getY();

                if(Math.abs((x1 - x2)) >= 15){
                    isMove = true;
                }else if(Math.abs((y1 - y2)) >= 15){
                    isMove = true;
                }else{
                    isMove = false;
                }
                break;

            default:
                break;
        }


        return mGesture.onTouchEvent(event);
    }

    private boolean isLeftRight = false;
    private boolean isUpDown = false;
    private boolean m_bLeftRightMirror;
    private boolean m_bUpDownMirror;
    private boolean lefRitUpDowTag = false;

    private final int MINLEN = 80;

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        float x2 = e2.getX();
        float y1 = e1.getY();
        float y2 = e2.getY();

        float xx = x1 > x2 ? x1 - x2 : x2 - x1;
        float yy = y1 > y2 ? y1 - y2 : y2 - y1;



        if (xx > yy) {
            if ((x1 > x2) && (xx > MINLEN)) {// right
                if (lefRitUpDowTag == false) {

                    new AsyncTask<Void, Void, Void>() {
                        protected void onPreExecute() {
                            lefRitUpDowTag = true;

                        };

                        @Override
                        protected Void doInBackground(Void... params) {
                            // TODO Auto-generated method stub

                            if (!HCNetSDK.getInstance().NET_DVR_PTZControl_Other(
                                    m_iLogID, m_iStartChan, PTZCommand.PAN_RIGHT, 0)) {
                                Log.e(TAG,
                                        "start PAN_LEFT failed with error code: "
                                                + HCNetSDK.getInstance()
                                                .NET_DVR_GetLastError());
                            } else {
                                Log.i(TAG, "start PAN_LEFT succ");
                            }


                            try {
                                Thread.sleep(500);
                            } catch (InterruptedException e) {
                                // TODO Auto-generated catch block
                                e.printStackTrace();
                            }

                            if (!HCNetSDK.getInstance().NET_DVR_PTZControl_Other(
                                    m_iLogID, m_iStartChan, PTZCommand.PAN_RIGHT, 1)) {
                                Log.e(TAG, "stop PAN_LEFT failed with error code: "
                                        + HCNetSDK.getInstance()
                                        .NET_DVR_GetLastError());
                            } else {
                                Log.i(TAG, "stop PAN_LEFT succ");
                            }


                            return null;
                        }

                        protected void onPostExecute(Void result) {

                            lefRitUpDowTag = false;
                        };
                    }.execute();
                }
            }else if ((x1 < x2) && (xx > MINLEN) ) {// LEFT
                if (!lefRitUpDowTag ) {
                    new AsyncTask<Void, Void, Void>() {
                        protected void onPreExecute() {
                            lefRitUpDowTag = true;

                        };

                        @Override
                        protected Void doInBackground(Void... params) {
                            // TODO Auto-generated method stub
                            if (!HCNetSDK.getInstance().NET_DVR_PTZControl_Other(
                                    m_iLogID, m_iStartChan, PTZCommand.PAN_LEFT, 0)) {
                                Log.e(TAG,
                                        "start PAN_LEFT failed with error code: "
                                                + HCNetSDK.getInstance()
                                                .NET_DVR_GetLastError());
                            } else {
                                Log.i(TAG, "start PAN_LEFT succ");
                            }

                            try {
                                Thread.sleep(500);
                            } catch (InterruptedException e) {
                                // TODO Auto-generated catch block
                                e.printStackTrace();
                            }

                            if (!HCNetSDK.getInstance().NET_DVR_PTZControl_Other(
                                    m_iLogID, m_iStartChan, PTZCommand.PAN_LEFT, 1)) {
                                Log.e(TAG, "stop PAN_LEFT failed with error code: "
                                        + HCNetSDK.getInstance()
                                        .NET_DVR_GetLastError());
                            } else {
                                Log.i(TAG, "stop PAN_LEFT succ");
                            }

                            return null;
                        }

                        protected void onPostExecute(Void result) {

                            lefRitUpDowTag = false;
                        };
                    }.execute();
                }
            }

        }else{

            if ((y1 > y2) && (yy > MINLEN)) {// down
                if (!lefRitUpDowTag ) {
                    new AsyncTask<Void, Void, Void>() {
                        protected void onPreExecute() {
                            lefRitUpDowTag = true;

                        };

                        @Override
                        protected Void doInBackground(Void... params) {
                            // TODO Auto-generated method stub

                            if (!HCNetSDK.getInstance().NET_DVR_PTZControl_Other(
                                    m_iLogID, m_iStartChan, PTZCommand.DOWN_LEFT, 0)) {
                                Log.e(TAG, "stop PAN_LEFT failed with error code: "
                                        + HCNetSDK.getInstance()
                                        .NET_DVR_GetLastError());
                            } else {
                                Log.i(TAG, "stop PAN_LEFT succ");
                            }

                            try {
                                Thread.sleep(500);
                            } catch (InterruptedException e) {
                                // TODO Auto-generated catch block
                                e.printStackTrace();
                            }

                            if (!HCNetSDK.getInstance().NET_DVR_PTZControl_Other(
                                    m_iLogID, m_iStartChan, PTZCommand.DOWN_LEFT, 1)) {
                                Log.e(TAG, "stop PAN_LEFT failed with error code: "
                                        + HCNetSDK.getInstance()
                                        .NET_DVR_GetLastError());
                            } else {
                                Log.i(TAG, "stop PAN_LEFT succ");
                            }

                            return null;
                        }

                        protected void onPostExecute(Void result) {

                            lefRitUpDowTag = false;
                        };
                    }.execute();
                }

            } else if ((y1 < y2) && (yy > MINLEN)) {// up
                if (!lefRitUpDowTag ) {
                    new AsyncTask<Void, Void, Void>() {
                        protected void onPreExecute() {
                            lefRitUpDowTag = true;

                        };

                        @Override
                        protected Void doInBackground(Void... params) {
                            // TODO Auto-generated method stub
                            if (!HCNetSDK.getInstance().NET_DVR_PTZControl_Other(
                                    m_iLogID, m_iStartChan, PTZCommand.UP_LEFT, 0)) {
                                Log.e(TAG, "stop PAN_LEFT failed with error code: "
                                        + HCNetSDK.getInstance()
                                        .NET_DVR_GetLastError());
                            } else {
                                Log.i(TAG, "stop PAN_LEFT succ");
                            }

                            try {
                                Thread.sleep(500);
                            } catch (InterruptedException e) {
                                // TODO Auto-generated catch block
                                e.printStackTrace();
                            }

                            if (!HCNetSDK.getInstance().NET_DVR_PTZControl_Other(
                                    m_iLogID, m_iStartChan, PTZCommand.UP_LEFT, 1)) {
                                Log.e(TAG, "stop PAN_LEFT failed with error code: "
                                        + HCNetSDK.getInstance()
                                        .NET_DVR_GetLastError());
                            } else {
                                Log.i(TAG, "stop PAN_LEFT succ");
                            }
                            return null;
                        }

                        protected void onPostExecute(Void result) {

                            lefRitUpDowTag = false;
                        };
                    }.execute();
                }
            }

        }

        return false;
    }

    @Override
    public boolean onDown(MotionEvent e) {
        // TODO Auto-generated method stub
        return true;
    }

    @Override
    public void onShowPress(MotionEvent e) {
        // TODO Auto-generated method stub

    }

    @Override
    public boolean onSingleTapUp(MotionEvent e) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public void onLongPress(MotionEvent e) {
        // TODO Auto-generated method stub

    }
}
