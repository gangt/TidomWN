package com.xiangpu.plugin;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;

import com.konecty.rocket.chat.R;
import com.lssl.activity.SuneeeApplication;
import com.xiangpu.activity.headereditor.ClipImageActivity;
import com.xiangpu.activity.usercenter.PersonCenterActivity;
import com.xiangpu.bean.UserInfo;
import com.xiangpu.common.Constants;
import com.xiangpu.utils.BitmapUtils;
import com.xiangpu.utils.DialogUtil;
import com.xiangpu.utils.FileUtils;
import com.xiangpu.utils.LogUtil;
import com.xiangpu.utils.StringUtils;
import com.xiangpu.utils.ToastUtils;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaArgs;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.PluginResult;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;

import okhttp3.Call;

/**
 * Created by Administrator on 2017/12/7 0007.
 * Info：
 */

public class GetPhotoPlugin extends CordovaPlugin implements DialogUtil.DialogUiAdpter {

    private CallbackContext callbackContext = null;
    private Activity activity = null;

    private static View mView;
    private static DialogUtil.DialogUiAdpter dialogUiAdapter;

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

    /**
     * 发送指挥命令 (原生发给js) json 格式
     *
     * @param fileName
     */
    public void sendPluginResult(String fileName) {
        if (callbackContext != null) {
            PluginResult dataResult = new PluginResult(PluginResult.Status.OK, fileName);
            dataResult.setKeepCallback(true);
            callbackContext.sendPluginResult(dataResult);
        } else {
            LogUtil.e("filePath", "GetPhotoPlugin callbackContext is null!");
        }
    }

    @Override
    public void receiveData(int status) {
        if (status == 0) {
            getImageFromRoom();
        } else if (status == 1) {
            getImageFromCamera();
        }
    }

    public static final int TAKE_BIG_PICTURE = 1;
    public static final int CHOOSE_BIG_PICTURE = 2;
    public static final int PICTURE_CUSTOM_CAIJIE = 3;

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
                setPicToView(null, intent);
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
                        // 将Application中的headerBmp清空
                        SuneeeApplication.getInstance().user.headerBmp = null;
                        Intent intentClip = new Intent(cordova.getActivity(), ClipImageActivity.class);
                        intentClip.putExtra("imagePath", filePath);
                        cordova.startActivityForResult(this, intentClip, PICTURE_CUSTOM_CAIJIE);
                    }
                }
                break;
            case PICTURE_CUSTOM_CAIJIE:
                if (intent == null) {
                    ToastUtils.showCenterToast(activity, "请选择照片");
                    return;
                }
                String clipName = intent.getStringExtra("clipName");
                String clipFilePath = intent.getStringExtra("clipFilePath");
                uploadImg(clipFilePath, clipName);
                break;
        }
    }

    public void setPicToView(Uri uri, Intent picdata) {
        if (uri == null) {
            intentClipPicture(picdata.getData());
        } else {
            intentClipPicture(uri);
        }
    }

    private void intentClipPicture(Uri uri) {
        Intent intent = new Intent(cordova.getActivity(), ClipImageActivity.class);
        String imagePath = BitmapUtils.getPathByUri4kitkat(activity, uri);
        intent.putExtra("imagePath", imagePath);
        cordova.startActivityForResult(this, intent, PICTURE_CUSTOM_CAIJIE);
    }

    private void uploadImg(final String imagePath, final String imageName) {
        OkHttpUtils
                .post()
                .addFile("image", imageName, new File(imagePath))
                .url(Constants.BASE_UPLOADIMG)
                .build()
                .execute(new StringCallback() {

                    @Override
                    public void onResponse(String response, int arg1) {
                        if (StringUtils.isEmpty(response)) {
                            Log.e("图片上传", "上传失败");
                            return;
                        }
                        try {
                            JSONObject result = new JSONObject(response);
                            if (result.getInt("status") != 1) {
                                Log.e("图片上传", result.getString("message"));
                                return;
                            } else {
                                Log.e("图片上传", "上传成功");
                            }
                            JSONObject data = result.getJSONObject("data");
                            String fileName = data.getString("fileName");

                            SuneeeApplication.user.headName = fileName;
                            UserInfo userInfo = new UserInfo();
                            userInfo.setPhoto(fileName);
                            SuneeeApplication.setUser(userInfo);
                            if (activity instanceof PersonCenterActivity) {
                                ((PersonCenterActivity) activity).refreshPersonHead(imagePath);
                            }
                            sendPluginResult(fileName);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }

                    @Override
                    public void onError(Call arg0, Exception exception, int arg2) {
                        ToastUtils.showCenterToast(cordova.getActivity(), "上传失败");
                        LogUtil.i("", "上传失败");
                        exception.printStackTrace();
                    }
                });
    }

}
