package face;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.graphics.SurfaceTexture;
import android.graphics.YuvImage;
import android.hardware.Camera;
import android.hardware.Camera.CameraInfo;
import android.util.Log;
import android.view.Gravity;
import android.view.Surface;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

/**
 * 照相机工具类
 */
public class ICameraUtil {

    public Camera mCamera;
    public int cameraWidth;
    public int cameraHeight;
    public int cameraId = 1;// 前置摄像头
    public int Angle;

    /**
     * 打开相机
     */
    public Camera openCamera(boolean isBackCamera, Activity activity, HashMap<String, Integer> resolutionMap) {
        try {
            if (isBackCamera) {
                cameraId = 0;
            } else {
                cameraId = 1;
            }

            int width = 640;
            int height = 480;

            if (resolutionMap != null) {
                width = resolutionMap.get("width");
                height = resolutionMap.get("height");
            }

            mCamera = Camera.open(cameraId);
            CameraInfo cameraInfo = new CameraInfo();
            Camera.getCameraInfo(cameraId, cameraInfo);
            Camera.Parameters params = mCamera.getParameters();
            Camera.Size bestPreviewSize = calBestPreviewSize(mCamera.getParameters(), width, height);
            cameraWidth = bestPreviewSize.width;
            cameraHeight = bestPreviewSize.height;
//            cameraWidth = width;
//            cameraHeight = width;
            params.setPreviewSize(cameraWidth, cameraHeight);
            Angle = getCameraAngle(activity);
            Log.w("ceshi", "Angle==" + Angle);
//            mCamera.setDisplayOrientation(Angle);
            mCamera.setParameters(params);
            return mCamera;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    // 通过屏幕参数、相机预览尺寸计算布局参数
    public RelativeLayout.LayoutParams getLayoutParam() {
        float scale = cameraWidth * 1.0f / cameraHeight;

//        int layout_width = ScreenUtil.mWidth;
        int layout_width = cameraWidth;
        int layout_height = (int) (layout_width * scale);
        if (ScreenUtil.mWidth >= ScreenUtil.mHeight) {
            layout_height = ScreenUtil.mHeight;
            layout_width = (int) (layout_height / scale);
        }

        RelativeLayout.LayoutParams layout_params = new RelativeLayout.LayoutParams(layout_width, layout_height);
        layout_params.addRule(RelativeLayout.CENTER_IN_PARENT);
        return layout_params;
    }

    /**
     * 开始检测脸
     */
    public void actionDetect(Camera.PreviewCallback mActivity) {
        if (mCamera != null) {
            mCamera.setPreviewCallback(mActivity);
        }
    }

    public void startPreview(SurfaceTexture surfaceTexture) {
        if (mCamera != null) {
            try {
                mCamera.setPreviewTexture(surfaceTexture);
                mCamera.startPreview();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void closeCamera() {
        if (mCamera != null) {
            mCamera.stopPreview();
            mCamera.setPreviewCallback(null);
            mCamera.release();
            mCamera = null;
        }
    }

    /**
     * 通过传入的宽高算出最接近于宽高值的相机大小
     */
    private Camera.Size calBestPreviewSize(Camera.Parameters camPara,
                                           final int width, final int height) {
        List<Camera.Size> allSupportedSize = camPara.getSupportedPreviewSizes();
        ArrayList<Camera.Size> widthLargerSize = new ArrayList<Camera.Size>();
        for (Camera.Size tmpSize : allSupportedSize) {
            Log.w("ceshi", "tmpSize.width===" + tmpSize.width
                    + ", tmpSize.height===" + tmpSize.height);
            if (tmpSize.width > tmpSize.height) {
                widthLargerSize.add(tmpSize);
            }
        }

        Collections.sort(widthLargerSize, new Comparator<Camera.Size>() {
            @Override
            public int compare(Camera.Size lhs, Camera.Size rhs) {
                int off_one = Math.abs(lhs.width * lhs.height - width * height);
                int off_two = Math.abs(rhs.width * rhs.height - width * height);
                return off_one - off_two;
            }
        });

        return widthLargerSize.get(0);
    }

    /**
     * 获取照相机旋转角度
     */
    public int getCameraAngle(Activity activity) {
        int rotateAngle = 90;
        CameraInfo info = new CameraInfo();
        Camera.getCameraInfo(cameraId, info);
        int rotation = activity.getWindowManager().getDefaultDisplay()
                .getRotation();
        int degrees = 0;
        switch (rotation) {
            case Surface.ROTATION_0:
                degrees = 0;
                break;
            case Surface.ROTATION_90:
                degrees = 90;
                break;
            case Surface.ROTATION_180:
                degrees = 180;
                break;
            case Surface.ROTATION_270:
                degrees = 270;
                break;
        }

        if (info.facing == CameraInfo.CAMERA_FACING_FRONT) {
            rotateAngle = (info.orientation + degrees) % 360;
            rotateAngle = (360 - rotateAngle) % 360; // compensate the mirror
        } else { // back-facing
            rotateAngle = (info.orientation - degrees + 360) % 360;
        }
        return rotateAngle;
    }
}