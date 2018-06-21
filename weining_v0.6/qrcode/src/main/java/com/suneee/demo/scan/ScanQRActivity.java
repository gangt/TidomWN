/*
    Suneee Android Client, ScanQRActivity
    Copyright (c) 2015 Suneee Tech Company Limited
 */

package com.suneee.demo.scan;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.Vibrator;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.DecodeHintType;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.ReaderException;
import com.google.zxing.Result;
import com.google.zxing.common.HybridBinarizer;
import com.suneee.demo.scan.camera.CameraManager;
import com.suneee.demo.scan.decode.CaptureActivityHandler;
import com.suneee.demo.scan.decode.InactivityTimer;
import com.suneee.demo.scan.decode.RGBLuminanceSource;
import com.suneee.demo.scan.view.ViewfinderView;
import com.suneee.mis.MISActivity;
import com.suneee.qrcode.R;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Hashtable;
import java.util.List;
import java.util.Vector;

import de.greenrobot.event.EventBus;

/**
 * [扫描二维码 选择图片进行扫描 手动输入]
 *
 * @author galaxy_xiong
 * @version 1.0
 * @date 2015年8月26日
 **/
public class ScanQRActivity extends FragmentActivity implements Callback,
        OnClickListener {
    public static final String INTENT_EXTRA_SCAN_RESULT = "scanResult"; //result返回值的key
    public static final int SCAN_QR_RESULT_CODE = 7964;        //二维码扫描result返回码

    private static final float BEEP_VOLUME = 0.10f;
    private static final int PARSE_BARCODE_FAIL = 303;
    private static final long VIBRATE_DURATION = 200L;

    private static final int PARSE_BARCODEING = 304;
    private static final String INTENT_EXTRA_ENTRY_TYPE = "returnScanResult"; //进入类型是否为直接返回结果类型（erp商品扫描）

    private ProgressDialog mProgress;
    private ViewfinderView viewfinderView;
    private SurfaceView surfaceView;

    private ImageButton exitImgBtn;
    private TextView scanTv;
    private TextView albumScanTv;
    private TextView inputTv;

    private CaptureActivityHandler handler;
    private InactivityTimer inactivityTimer;
    private MediaPlayer mediaPlayer;
    private MultiFormatReader multiFormatReader;

    private boolean hasSurface;
    private boolean playBeep;
    private boolean vibrate;

    private Vector<BarcodeFormat> decodeFormats;
    private String characterSet;
    private String photo_path;
    private Bitmap scanBitmap;

    private RelativeLayout scanView;
    private ImageView scanLine;

    private boolean needScanResult = false;//cp.add 扫描成功后直接返回扫描结果（h5扫描商品）

    public static QScanResultCallBackInterface qScanResultCallBackInterface = null;

    public static void setQScanResultCallBackInterface(QScanResultCallBackInterface callback) {
        qScanResultCallBackInterface = callback;
    }

    public interface QScanResultCallBackInterface {
        public void QScanResultCallBack(String result);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.scan_qr_code_layout);
        CameraManager.init(getApplication());
        initView();
        hasSurface = false;
        inactivityTimer = new InactivityTimer(this);
    }

    //cp.add(用于erp扫码，扫描直接返回扫描内容)
    public static Intent getIntentInstance(Context context) {
        Intent tent = new Intent(context, ScanQRActivity.class);
        tent.putExtra(INTENT_EXTRA_ENTRY_TYPE, true);
        return tent;
    }

    private void initView() {
        needScanResult = getIntent().getBooleanExtra(INTENT_EXTRA_ENTRY_TYPE, false);

        this.scanView = (RelativeLayout) this.findViewById(R.id.scan_view);
        this.scanLine = (ImageView) this.findViewById(R.id.scan_line);
        viewfinderView = (ViewfinderView) findViewById(R.id.viewfinder_view);
        this.exitImgBtn = (ImageButton) this
                .findViewById(R.id.exit_scan_img_btn);
        this.scanTv = (TextView) this.findViewById(R.id.scan_tv);
        this.albumScanTv = (TextView) this.findViewById(R.id.album_scan_tv);
        this.inputTv = (TextView) this.findViewById(R.id.input_scan_tv);

        this.exitImgBtn.setOnClickListener(this);
        this.scanTv.setOnClickListener(this);
        this.albumScanTv.setOnClickListener(this);
        this.inputTv.setOnClickListener(this);
        surfaceView = (SurfaceView) findViewById(R.id.preview_view);

        TranslateAnimation animation = new TranslateAnimation(
                Animation.RELATIVE_TO_PARENT, 0.0f,
                Animation.RELATIVE_TO_PARENT, 0.0f,
                Animation.RELATIVE_TO_PARENT, 0.0f,
                Animation.RELATIVE_TO_PARENT, 1.0f);
        animation.setRepeatCount(-1);
        animation.setRepeatMode(Animation.RESTART);
        animation.setInterpolator(new LinearInterpolator());
        animation.setDuration(1200);
        scanLine.startAnimation(animation);

    }

    @Override
    protected void onResume() {
        super.onResume();
        new Thread() {

            @Override
            public void run() {
                super.run();
                Looper.prepare();
                SurfaceHolder surfaceHolder = surfaceView.getHolder();
                if (hasSurface) {
                    initCamera(surfaceHolder);
                } else {
                    surfaceHolder.addCallback(ScanQRActivity.this);
                    surfaceHolder
                            .setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
                }
                decodeFormats = null;
                characterSet = null;

                playBeep = true;
                AudioManager audioService = (AudioManager) getSystemService(AUDIO_SERVICE);
                if (audioService.getRingerMode() != AudioManager.RINGER_MODE_NORMAL) {
                    playBeep = false;
                }
                initBeepSound();
                vibrate = true;
                Looper.loop();
            }

        }.start();
    }

    @Override
    protected void onPause() {
        super.onPause();
        new Thread() {

            @Override
            public void run() {
                super.run();
                if (handler != null) {
                    handler.quitSynchronously();
                    handler = null;
                }
                CameraManager.get().closeDriver();
            }

        }.start();
    }

    @Override
    protected void onDestroy() {
        if (handler != null) {
            handler.quitSynchronously();
            handler = null;
        }
        CameraManager.get().closeDriver();
        inactivityTimer.shutdown();
        super.onDestroy();
    }

    private void initCamera(SurfaceHolder surfaceHolder) {
        try {
            CameraManager.get().openDriver(surfaceHolder);
        } catch (IOException ioe) {
            return;
        } catch (RuntimeException e) {
            return;
        }
        if (handler == null) {
            handler = new CaptureActivityHandler(this, decodeFormats,
                    characterSet);
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width,
                               int height) {
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        if (!hasSurface) {
            hasSurface = true;
            initCamera(holder);
        }

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        hasSurface = false;

    }

    public ViewfinderView getViewfinderView() {
        return viewfinderView;
    }

    public Handler getHandler() {
        return handler;
    }

    public void drawViewfinder() {
        viewfinderView.drawViewfinder();

    }

    public void handleDecode(Result obj, Bitmap barcode) {
        inactivityTimer.onActivity();
        viewfinderView.drawResultBitmap(barcode);
        playBeepSoundAndVibrate();
        dealWithScanResult(obj);
        finish();
    }

    /**
     * 处理扫描结果
     *
     * @param obj
     */
    private void dealWithScanResult(Result obj) {
        String Barcode = obj.getBarcodeFormat().toString();
        String result = obj.getText().toString();
        if ("QR_CODE".equals(Barcode) || "DATA_MATRIX".equals(Barcode)) {//二维码
            dealWithQRCode(result);
        } else if ("UPC_E".equals(Barcode) || "EAN_13".equals(Barcode) || "EAN_8".equals(Barcode) || "CODE_128".equals(Barcode) || "CODE_39".equals(Barcode) || "CODE_93".equals(Barcode) || "RSS_14".equals(Barcode)) {          //条形码
            dealWithQRCode(result);
//			dealWithBarCode(result);
        }
    }

    /**
     * 处理二维码
     *
     * @param result
     */
    private void dealWithQRCode(String result) {
        if (!TextUtils.isEmpty(result)) {
            dealWithLinkResult(result);
//			if (result.startsWith("http://") || result.startsWith("https://")) {     //链接
//				dealWithLinkResult(result);
//			} else {                                    //非链接
//				if (result.startsWith(QRCodeDialog.WEILIANFFRIENDTAG)) {//好友
//					dealWithFriendResult(result);
//					this.finish();
//				} else if (result.startsWith(QRCodeDialog.WEILIANGROUPTAG)) {   //群组
//					dealWithGroupResult(result);
//					this.finish();
//				} else if (result.startsWith(QRCodeDialog.WEILIANDISCUSSTIONTAG)) {  //讨论组
//					dealWithDiscussionResult(result);
//					this.finish();
//				} else {            //直接显示扫描结果
//					dealWithTextResult(result);
//					this.finish();
//				}
//			}
        } else {
            Toast.makeText(ScanQRActivity.this, "解析失败", Toast.LENGTH_LONG)
                    .show();
            this.finish();
        }
    }

    /**
     * 处理扫描好友
     *
     * @param result
     */
    private void dealWithFriendResult(String result) {
        EventBus.getDefault().post(new ScanResultEvents(ScanResultEvents.SUCCESS, result, ScanResultEvents.TYPE_FRIEND_QRCODE));
    }

    /**
     * 处理扫描文本
     *
     * @param result
     */
    private void dealWithTextResult(String result) {
        //cp.add
        if (needScanResult) {
            //返回扫描结果到调起页面并关闭当前页
            doReturnScanResultAndFinish(result);
            return;
        }
        EventBus.getDefault().post(new ScanResultEvents(ScanResultEvents.SUCCESS, result, ScanResultEvents.TYPE_TEXT_QRCODE));
    }

    /**
     * 处理扫描群组
     *
     * @param result
     */
    private void dealWithGroupResult(String result) {
        EventBus.getDefault().post(new ScanResultEvents(ScanResultEvents.SUCCESS, result, ScanResultEvents.TYPE_GROUP_QRCODE));
    }

    /**
     * 处理扫描讨论组
     *
     * @param result
     */
    private void dealWithDiscussionResult(String result) {
        EventBus.getDefault().post(new ScanResultEvents(ScanResultEvents.SUCCESS, result, ScanResultEvents.TYPE_DISCUSSION_QRCODE));
    }

    /**
     * 处理扫描链接
     *
     * @param result
     */
    private void dealWithLinkResult(String result) {
        //final ScanResultEvents r = new ScanResultEvents(ScanResultEvents.SUCCESS,result,ScanResultEvents.TYPE_LINK_QRCODE);

        if (qScanResultCallBackInterface != null) {
            qScanResultCallBackInterface.QScanResultCallBack(result);
        }

//		new Thread(new Runnable() {
//			@Override
//			public void run() {
//				try{
//					EventBus.getDefault().post((Object) r);
//				}catch (Exception e){};
//			}
//		}).start();
    }

    /**
     * 条形码处理
     *
     * @param result
     */
    private void dealWithBarCode(String result) {
        //cp.add
        if (needScanResult) {
            //返回扫描结果到调起页面并关闭当前页
            doReturnScanResultAndFinish(result);
            return;
        }
        EventBus.getDefault().post(new ScanResultEvents(ScanResultEvents.SUCCESS, result, ScanResultEvents.TYPE_TEXT_BARCODE));
        this.finish();
    }

    /**
     * 返回扫描结果到调起页面并关闭当前页(erp扫描页面使用)
     */
    private void doReturnScanResultAndFinish(String result) {
        if (qScanResultCallBackInterface != null) {
            qScanResultCallBackInterface.QScanResultCallBack(result);
        }

        EventBus.getDefault().post(new ScanResultEvents(ScanResultEvents.SUCCESS, result, ScanResultEvents.TYPE_DISCUSSION_QRCODE));
        //因为Scan页面是singleTask类型，返回获取不到返回参数，需要用EventBus传递参数

        Intent tent = new Intent();
        tent.putExtra("action", SCAN_QR_RESULT_CODE);
        tent.putExtra(INTENT_EXTRA_SCAN_RESULT, result);
        EventBus.getDefault().post(tent);
        this.finish();
    }


    private void initBeepSound() {
        if (playBeep && mediaPlayer == null) {
            setVolumeControlStream(AudioManager.STREAM_MUSIC);
            mediaPlayer = new MediaPlayer();
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mediaPlayer.setOnCompletionListener(beepListener);

            AssetFileDescriptor file = getResources().openRawResourceFd(
                    R.raw.beep);
            try {
                mediaPlayer.setDataSource(file.getFileDescriptor(),
                        file.getStartOffset(), file.getLength());
                file.close();
                mediaPlayer.setVolume(BEEP_VOLUME, BEEP_VOLUME);
                mediaPlayer.prepare();
            } catch (IOException e) {
                mediaPlayer = null;
            }
        }
    }

    private void playBeepSoundAndVibrate() {
        if (playBeep && mediaPlayer != null) {
            mediaPlayer.start();
        }
        if (vibrate) {
            Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
            vibrator.vibrate(VIBRATE_DURATION);
        }
    }

    private final OnCompletionListener beepListener = new OnCompletionListener() {
        public void onCompletion(MediaPlayer mediaPlayer) {
            mediaPlayer.seekTo(0);
        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == 0) {
                List<String> photos = data.getStringArrayListExtra(MISActivity.EXTRA_RESULT);
                if (null != photos && photos.size() > 0) {
                    photo_path = photos.get(0);

                    mProgress = new ProgressDialog(ScanQRActivity.this);
                    mProgress.setMessage("玩命解析中，请稍等。。。");
                    mProgress.setCancelable(false);
                    mProgress.show();
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            Result result = scanningImage(photo_path);
                            if (result != null) {
                                Message message = new Message();
                                message.obj = result;
                                message.what = PARSE_BARCODEING;
                                mHandler.sendMessage(message);
                                // mProgress.dismiss();
                                // dealWithScanResult(result);
                            } else {
                                Message m = mHandler.obtainMessage();
                                m.what = PARSE_BARCODE_FAIL;
                                mHandler.sendMessage(m);
                            }
                        }
                    }).start();
                }

            } else if (requestCode == 1) {

            }

        }
    }

    private Handler mHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            mProgress.dismiss();
            switch (msg.what) {
                case PARSE_BARCODE_FAIL:
                    Toast.makeText(ScanQRActivity.this, "解析失败", Toast.LENGTH_LONG)
                            .show();
                    break;
                case PARSE_BARCODEING:
                    Result result = (Result) msg.obj;
                    if (result != null) {
                        mProgress.dismiss();
                        dealWithScanResult(result);

                    }

                    break;

            }
        }

    };

    public Result scanningImage(String path) {
        if (TextUtils.isEmpty(path)) {
            return null;
        }
        multiFormatReader = new MultiFormatReader();

        Hashtable<DecodeHintType, String> hints = new Hashtable<DecodeHintType, String>();
        hints.put(DecodeHintType.CHARACTER_SET, "UTF8");

        multiFormatReader.setHints(hints);
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        scanBitmap = BitmapFactory.decodeFile(path, options);
        options.inJustDecodeBounds = false;
        int sampleSize = (int) (options.outHeight / (float) 200);
        if (sampleSize <= 0)
            sampleSize = 1;
        options.inSampleSize = sampleSize;
        scanBitmap = BitmapFactory.decodeFile(path, options);
        RGBLuminanceSource source = new RGBLuminanceSource(scanBitmap);
        BinaryBitmap bitmap1 = new BinaryBitmap(new HybridBinarizer(source));
        int width = scanBitmap.getWidth();
        int height = scanBitmap.getHeight();
        try {
            return multiFormatReader.decode(bitmap1, hints);
        } catch (ReaderException re) {
        } finally {
            multiFormatReader.reset();
        }
        return null;
    }

    public byte[] Bitmap2Bytes(Bitmap bm) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.PNG, 100, baos);
        return baos.toByteArray();
    }

    @Override
    public void onClick(View v) {
        int vid = v.getId();
        if (vid == R.id.exit_scan_img_btn) {
            finish();
        } else if (vid == R.id.input_scan_tv) {
            Intent inputIntent = new Intent(this, HandInputActivity.class);
            this.startActivity(inputIntent);
        } else if (vid == R.id.album_scan_tv) {
            albumScanTv.post(new Runnable() {

                @Override
                public void run() {
                    chooseQRFile();
                }
            });
        }
    }

    //选择本地二维码
    private void chooseQRFile() {
        /*Intent scanIntent = new Intent(ScanQRActivity.this,MISActivity.class);
		scanIntent.putExtra(MISActivity.EXTRA_SHOW_CAMERA, false);
		scanIntent.putExtra(MISActivity.EXTRA_SELECT_MODE,MISActivity.MODE_SINGLE);
		startActivityForResult(scanIntent, 0);*/
    }
}
