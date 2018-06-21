package com.xiangpu.activity.person;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageFormat;
import android.graphics.Rect;
import android.graphics.SurfaceTexture;
import android.graphics.YuvImage;
import android.hardware.Camera;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.konecty.rocket.chat.R;
import com.lssl.activity.SuneeeApplication;
import com.megvii.cloud.http.CommonOperate;
import com.megvii.cloud.http.FaceSetOperate;
import com.megvii.facepp.sdk.Facepp;
import com.xiangpu.activity.BaseActivity;
import com.xiangpu.common.Constants;
import com.xiangpu.interfaces.LoginInterface;
import com.xiangpu.utils.LogUtil;
import com.xiangpu.utils.LoginUtils;
import com.xiangpu.utils.SharedPrefUtils;
import com.xiangpu.utils.SizeUtils;
import com.xiangpu.utils.SkipUtils;
import com.xiangpu.utils.ToastUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import face.CameraMatrix;
import face.ConUtil;
import face.ICameraUtil;
import face.OpenGLUtil;
import face.PointsMatrix;
import face.ScreenUtil;
import face.SensorEventUtil;

/**
 * Created by Administrator on 2017/9/25 0025.
 * Info：开启脸部解锁界面
 */

public class FaceLoginActivity extends BaseActivity implements Camera.PreviewCallback,
        GLSurfaceView.Renderer, SurfaceTexture.OnFrameAvailableListener {
    private boolean isTiming = true; // 是否是定时去刷新界面;
    private int printTime = 31;
    private GLSurfaceView mGlSurfaceView;
    private ICameraUtil mICamera;
    private Camera mCamera;
    private HandlerThread mHandlerThread = new HandlerThread("facepp");
    private Handler mHandler;
    private Facepp facepp;
    private int min_face_size = 200;
    private int detection_interval = 25;
    private SensorEventUtil sensorUtil;

    private String userPwd = "";
    private String account = "";
    private int Angle;
    boolean isSuccess = false;
    float pitch, yaw, roll;
    long startTime;
    int rotation = Angle;

    private int compareCount = 0; // 人脸比对次数
    private boolean saveFile = true;
    private String faceToken = "";
    private ImageView iv_back = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ScreenUtil.initialize(this);
        setContentView(R.layout.activity_face_lock);
        init();
    }

    private void init() {
        if (android.os.Build.MODEL.equals("PLK-AL10"))
            printTime = 50;

        facepp = new Facepp();
        sensorUtil = new SensorEventUtil(this);
        mHandlerThread.start();
        mHandler = new Handler(mHandlerThread.getLooper());

        mGlSurfaceView = (GLSurfaceView) findViewById(R.id.gls_view);
        mGlSurfaceView.setEGLContextClientVersion(2);// 创建一个OpenGL ES 2.0
        mGlSurfaceView.setRenderer(this);// 设置渲染器进入gl
        // RENDERMODE_CONTINUOUSLY不停渲染
        // RENDERMODE_WHEN_DIRTY懒惰渲染，需要手动调用 glSurfaceView.requestRender() 才会进行更新
        mGlSurfaceView.setRenderMode(mGlSurfaceView.RENDERMODE_WHEN_DIRTY);// 设置渲染器模式
        mICamera = new ICameraUtil();

        account = SharedPrefUtils.getStringData(this, "userName", "");
        userPwd = SharedPrefUtils.getStringData(this, "psdWord", "");
        iv_back = (ImageView) findViewById(R.id.iv_back);
        iv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        ConUtil.acquireWakeLock(this);
        startTime = System.currentTimeMillis();
        mCamera = mICamera.openCamera(false, this, null);
        if (mCamera != null) {
            Angle = 360 - mICamera.Angle;

            Log.d("FaceLockActivity", "Angle: " + Angle);
            RelativeLayout.LayoutParams layout_params = mICamera.getLayoutParam();
            layout_params.setMargins(0, SizeUtils.convertDp2Px(this, 60), 0, 0);
            mGlSurfaceView.setLayoutParams(layout_params);

            int width = mICamera.cameraWidth;
            int height = mICamera.cameraHeight;

            int left = 0;
            int top = 0;
            int right = width;
            int bottom = height;

            String errorCode = facepp.init(this, ConUtil.getFileContent(this, R.raw.megviifacepp_0_4_7_model));
            Facepp.FaceppConfig faceppConfig = facepp.getFaceppConfig();
            faceppConfig.interval = detection_interval;
            faceppConfig.minFaceSize = min_face_size;
            faceppConfig.roi_left = left;
            faceppConfig.roi_top = top;
            faceppConfig.roi_right = right;
            faceppConfig.roi_bottom = bottom;
            faceppConfig.one_face_tracking = 0;
            faceppConfig.detectionMode = Facepp.FaceppConfig.DETECTION_MODE_TRACKING;
            facepp.setFaceppConfig(faceppConfig);
        }
    }

    private void setConfig(int rotation) {
        Facepp.FaceppConfig faceppConfig = facepp.getFaceppConfig();
        if (faceppConfig.rotation != rotation) {
            faceppConfig.rotation = rotation;
            facepp.setFaceppConfig(faceppConfig);
        }
    }

    @Override
    public void onPreviewFrame(final byte[] imgData, final Camera camera) {
        if (isSuccess)
            return;
        isSuccess = true;

        mHandler.post(new Runnable() {
            @Override
            public void run() {
                int width = mICamera.cameraWidth;
                int height = mICamera.cameraHeight;

                int orientation = sensorUtil.orientation;
                if (orientation == 0)
                    rotation = Angle;
                else if (orientation == 1)
                    rotation = 0;
                else if (orientation == 2)
                    rotation = 180;
                else if (orientation == 3)
                    rotation = 360 - Angle;

                setConfig(rotation);

                final Facepp.Face[] faces = facepp.detect(imgData, width, height, Facepp.IMAGEMODE_NV21);

                if (faces != null) {
                    float confidence = 0.0f;

                    if (faces.length > 0) {
                        if (faces.length > 1) {
                            ToastUtils.showCenterToast(FaceLoginActivity.this, "请保持摄像头中只有一张人脸");
                            return;
                        }
                        Facepp.Face face = faces[0];
                        pitch = face.pitch;
                        yaw = face.yaw;
                        roll = face.roll;
                        confidence = face.confidence;

                        if (confidence > 0.5) {
                            if (saveFile) {
                                saveFile = false;
                                Bitmap bitmap = getCameraBitmap(imgData, camera);
                                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                                bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
                                CommonOperate commonOperate = new CommonOperate(Constants.API_KEY, Constants.API_SECRET, false);
                                FaceSetOperate FaceSet = new FaceSetOperate(Constants.API_KEY, Constants.API_SECRET, false);
                                ArrayList<String> facesList = new ArrayList<>();
                                try {
                                    com.megvii.cloud.http.Response response1 = commonOperate.detectByte(baos.toByteArray(), 0, null);
                                    faceToken = getFaceToken(response1);
                                    LogUtil.d("faceToken: " + faceToken);
                                    facesList.add(faceToken);
                                    String userFaceToken = SharedPrefUtils.getStringData(FaceLoginActivity.this, Constants.FACE_TOKEN, "");
                                    String faceDisValue = SharedPrefUtils.getStringData(FaceLoginActivity.this, Constants.FACE_DIS_VALUE, "");
                                    LogUtil.d("userFaceToken: " + userFaceToken + " faceDisValue: " + faceDisValue);
                                    com.megvii.cloud.http.Response response4 = commonOperate.compare(userFaceToken, null, null, null, faceToken, null, null, null);
                                    double compareConfidence = getConfidence(response4);
                                    if (compareConfidence > Double.parseDouble(faceDisValue)) {
                                        timeHandle.removeMessages(0);
                                        showProgressDialog();
                                        LoginUtils.getLoginInfo(FaceLoginActivity.this, SuneeeApplication.weilianType, account, userPwd, new LoginInterface() {
                                            @Override
                                            public void loginSuccess(String type) {
                                                SkipUtils.getInstance().skipToByUserPower(FaceLoginActivity.this, type);
                                            }

                                            @Override
                                            public void loginFailed(String errorMsg) {
                                                ToastUtils.showCenterToast(FaceLoginActivity.this, errorMsg);
                                            }
                                        });
                                    } else {
                                        saveFile = true;
                                        compareCount++;
                                        if (compareCount == 1) {
                                            ToastUtils.showCenterToast(FaceLoginActivity.this, "请调整姿势");
                                        } else {
                                            Intent intent = new Intent();
                                            setResult(Constants.FACE_LOGIN_FAIL, intent);
                                            finish();
                                        }
                                    }
                                } catch (Exception exception) {
                                    exception.printStackTrace();
                                }
                            }
                        }
                    } else {
                        pitch = 0.0f;
                        yaw = 0.0f;
                        roll = 0.0f;
                    }
                }
                isSuccess = false;
                if (!isTiming) {
                    timeHandle.sendEmptyMessage(1);
                }
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        ConUtil.releaseWakeLock();
        mICamera.closeCamera();
        mCamera = null;
        timeHandle.removeMessages(0);
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        facepp.release();
    }

    private int mTextureID = -1;
    private SurfaceTexture mSurface;
    private CameraMatrix mCameraMatrix;
    private PointsMatrix mPointsMatrix;

    @Override
    public void onFrameAvailable(SurfaceTexture surfaceTexture) {

    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        // 黑色背景
        GLES20.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);

        mTextureID = OpenGLUtil.createTextureID();
        mSurface = new SurfaceTexture(mTextureID);
        // 这个接口就干了这么一件事，当有数据上来后会进到onFrameAvailable方法
        mSurface.setOnFrameAvailableListener(this);// 设置照相机有数据时进入
        mCameraMatrix = new CameraMatrix(mTextureID);
        mPointsMatrix = new PointsMatrix();
        mICamera.startPreview(mSurface);// 设置预览容器
        mICamera.actionDetect(this);
        if (isTiming) {
            timeHandle.sendEmptyMessageDelayed(0, printTime);
        }
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        // 设置画面的大小
        GLES20.glViewport(0, 0, width, height);

        float ratio = (float) width / height;
        ratio = 1; // 这样OpenGL就可以按照屏幕框来画了，不是一个正方形了

        // this projection matrix is applied to object coordinates
        // in the onDrawFrame() method
        Matrix.frustumM(mProjMatrix, 0, -ratio, ratio, -1, 1, 3, 7);
        // Matrix.perspectiveM(mProjMatrix, 0, 0.382f, ratio, 3, 700);
    }

    private final float[] mMVPMatrix = new float[16];
    private final float[] mProjMatrix = new float[16];
    private final float[] mVMatrix = new float[16];

    @Override
    public void onDrawFrame(GL10 gl) {
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);// 清除屏幕和深度缓存
        float[] mtx = new float[16];
        mSurface.getTransformMatrix(mtx);
        mCameraMatrix.draw(mtx);
        // Set the camera position (View matrix)
        Matrix.setLookAtM(mVMatrix, 0, 0, 0, -3, 0f, 0f, 0f, 0f, 1f, 0f);

        // Calculate the projection and view transformation
        Matrix.multiplyMM(mMVPMatrix, 0, mProjMatrix, 0, mVMatrix, 0);

        mPointsMatrix.draw(mMVPMatrix);
        mSurface.updateTexImage();// 更新image，会调用onFrameAvailable方法
    }

    Handler timeHandle = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    mGlSurfaceView.requestRender();// 发送去绘制照相机不断去回调
                    timeHandle.sendEmptyMessageDelayed(0, printTime);
                    break;
                case 1:
                    mGlSurfaceView.requestRender();// 发送去绘制照相机不断去回调
                    break;
            }
        }
    };


    private String getFaceToken(com.megvii.cloud.http.Response response) throws JSONException {
        if (response.getStatus() != 200) {
            LogUtil.d(new String(response.getContent()));
            return "";
        }
        String res = new String(response.getContent());
        JSONObject json = new JSONObject(res);
        String faceToken = json.optJSONArray("faces").optJSONObject(0).optString("face_token");
        return faceToken;
    }

    private double getConfidence(com.megvii.cloud.http.Response response) throws JSONException {
        if (response.getStatus() != 200) {
            LogUtil.d(new String(response.getContent()));
            return 0;
        }
        String res = new String(response.getContent());
        JSONObject json = new JSONObject(res);
        return json.getDouble("confidence");
    }

    private Bitmap getCameraBitmap(final byte[] imgData, final Camera camera) {
        Camera.Size previewSize = camera.getParameters().getPreviewSize();
        BitmapFactory.Options newOpts = new BitmapFactory.Options();
        newOpts.inJustDecodeBounds = true;
        YuvImage yuvImage = new YuvImage(imgData, ImageFormat.NV21, previewSize.width, previewSize.height, null);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        yuvImage.compressToJpeg(new Rect(0, 0, previewSize.width, previewSize.height), 100, outputStream);
        byte[] rawImage = outputStream.toByteArray();
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPreferredConfig = Bitmap.Config.RGB_565;
        Bitmap bitmap = BitmapFactory.decodeByteArray(rawImage, 0, rawImage.length, options);
        return rotaingImageView(Angle, bitmap);
    }

    public static Bitmap rotaingImageView(int angle, Bitmap bitmap) {
        // 旋转图片 动作
        android.graphics.Matrix matrix = new android.graphics.Matrix();
        matrix.postRotate(angle);
        System.out.println("angle2=" + angle);
        // 创建新的图片
        Bitmap resizedBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
        return resizedBitmap;
    }

}
