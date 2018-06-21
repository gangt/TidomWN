package com.xiangpu.plugin;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;

import com.konecty.rocket.chat.R;
import com.xiangpu.activity.usercenter.PersonCenterActivity;
import com.xiangpu.common.Constants;
import com.xiangpu.utils.BitmapUtils;
import com.xiangpu.utils.DialogUtil;
import com.xiangpu.utils.FileUtils;
import com.xiangpu.utils.LogUtil;
import com.xiangpu.utils.ToastUtils;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaArgs;
import org.apache.cordova.CordovaPlugin;
import org.json.JSONException;

/**
 * Created by Administrator on 2017/12/20 0020.
 * Info：定子链
 */

public class UcpGetPhotoPlugin extends CordovaPlugin implements DialogUtil.DialogUiAdpter {
    private CallbackContext callbackContext = null;
    private Activity activity = null;
    private static View mView;
    private static DialogUtil.DialogUiAdpter dialogUiAdapter;

    public static final int TAKE_BIG_PICTURE = 1;
    public static final int CHOOSE_BIG_PICTURE = 2;

    @Override
    public boolean execute(String action, CordovaArgs args, CallbackContext callbackContext) throws JSONException {
        this.callbackContext = callbackContext;
        this.activity = cordova.getActivity();
        if (action.equals("getPhoto")) {
            dialogUiAdapter = this;
            mView = activity.getWindow().getDecorView();

            DialogUtil.setDialogUiAdpter(dialogUiAdapter);
            DialogUtil.showCusPopUp(activity, mView, activity.getResources().getString(R.string.person_app_3_set_from_moblie_pic),
                    activity.getResources().getString(R.string.person_app_3_set_take_photos));
            return true;
        }
        return false;
    }

    @Override
    public Boolean shouldAllowRequest(String url) {
        return true;
    }

    @Override
    public Boolean shouldAllowBridgeAccess(String url) {
        return true;
    }

    @Override
    public void receiveData(int status) {
        if (status == 0) {
            getImageFromRoom();
        } else if (status == 1) {
            getImageFromCamera();
        }
    }

    public void getImageFromRoom() {
        Intent intent = new Intent(Intent.ACTION_PICK, null);
        intent.setData(MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
        PersonCenterActivity.activityResultCallback = this;
        cordova.startActivityForResult(this, intent, CHOOSE_BIG_PICTURE);
    }

    public void getImageFromCamera() {
        String state = Environment.getExternalStorageState(); // 拿到sdcard是否可用的状态码
        if (state.equals(Environment.MEDIA_MOUNTED)) { // 如果可用
            Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
            PersonCenterActivity.activityResultCallback = this;
            cordova.startActivityForResult(this, intent, TAKE_BIG_PICTURE);
        } else {
            ToastUtils.showCenterToast(activity, "sdcard不可用");
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        switch (requestCode) {
            case CHOOSE_BIG_PICTURE:
                // 相册
                if (intent == null) {
                    ToastUtils.showCenterToast(activity, "请选择照片");
                    return;
                }
                String imagePath = BitmapUtils.getPathByUri4kitkat(activity, intent.getData());
                callbackContext.success(imagePath);
                break;
            case TAKE_BIG_PICTURE:
                // 照相
                if (intent == null) {
                    ToastUtils.showCenterToast(activity, "请选择照片");
                    return;
                }
                if (intent.getData() != null || intent.getExtras() != null) { // 防止没有返回结果
                    Uri uri = intent.getData();
                    Bitmap photo = null;
                    // 测试发现返回的uri为空
                    if (uri != null) {
                        photo = BitmapFactory.decodeFile(uri.getPath()); // 拿到图片
                    }

                    if (photo == null) {
                        Bundle bundle = intent.getExtras();
                        if (bundle != null) {
                            photo = (Bitmap) bundle.get("data");
                        } else {
                            ToastUtils.showCenterToast(activity, "找不到图片");
                        }
                    }

                    if (photo != null) {
                        FileUtils fileUtils = FileUtils.getInstance(Constants.DEFAULT_SAVE_IMAGE_PATH);
                        String fileName = Constants.CROP_CACHE_FILE_NAME + System.currentTimeMillis() + ".jpg";
                        fileUtils.saveFile(photo, fileName);
                        String filePath = fileUtils.getFilePath(fileName);
                        LogUtil.d("拍照存储的全路径：" + filePath);
                        callbackContext.success(filePath);
                    }
                }
                break;
        }
    }

}
