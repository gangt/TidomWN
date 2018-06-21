package com.suneee.mis;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.text.TextUtils;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.suneee.mis.adapter.PhotoViewAdapter;
import com.suneee.mis.utils.FileUtils;
import com.suneee.mis.utils.ImageCompress;
import com.suneee.mis.view.PhotoViewPager;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PhotoViewActivity extends FragmentActivity {

    private static final String TAG = PhotoViewActivity.class.getSimpleName();
    private TextView indexDisplayView;
    private ImageView functionMoreView;

    private PhotoViewPager viewPager;
    private PhotoViewAdapter photoViewAdapter;
    private List<String> picUrls;// 存储外面传过来的数据源
    private List<String> adapterDatas = new ArrayList<String>();// PhotoViewPager的数据源

    public static final String EXTRA_PIC_URLS = "picUrls";
    public static final String EXTRA_DEFAUL_INDEX = "initIndex";
    public static final String EXTRA_VIEW_MODEL = "view_model";
    public static final String EXTRA_TARGET_ACTIVITY = "target_activity";
    public static final String EXTRA_RESULT = "result";
    public static final String EXTRA_IMAGE_CACHE_DIR = "image_cache_dir";// 存储处理过来的文件目录
    public static final String EXTRA_TARGET_COMPRESS_WIDTH = "target_format_widht";// 压缩后的宽度
    public static final String EXTRA_TARGET_COMPRESS_HEIGHT = "target_format_height";// 压缩后的高度
    public static final String EXTRA_COMPLETE_BTN_TEXT = "complete_btn_text";
    public static final String EXTRA_DELETE_ORIGIN_PICTURE = "after_complete_delete_origin_picture";
    /**
     * 开启长按点击事件
     */
    public static final String EXTRA_OPEN_LONG_CLICK_ACTION = "imageFileSaveName";
    /**
     * 长按事件图片保存的文件夹名
     */
    public static final String EXTRA_LONG_CLICK_IMAGE_SAVE_FILE_NAME = "extra_long_click_image_save_file_name";

    private ArrayList<String> resultList = new ArrayList<String>();

    private int initIndex;
    private boolean viewModel = true;// 默认为预览模式
    private String mTargetActivity;
    private String mImageCacheDir;// 存储处理过来的文件目录
    private String mCompleteBtnText;
    private boolean mDeleteOriginPicture = true;
    /**
     * 开启长按点击事件，默认关闭
     */
    private boolean mOpenLongClickActionFlag = false;
    /**
     * 图片保存的文件夹名
     */
    private String imageFileSaveName = "";

    private static final int DEFAULT_WIDTH = 540;
    private static final int DEFAULT_HEIGHT = 960;

    private int targetCompressWidth = DEFAULT_WIDTH;
    private int targetCompressHeight = DEFAULT_HEIGHT;

    private RelativeLayout misPhotoviewTitleBarLayout;
    private RelativeLayout misPhotoviewBottomLayout;
    private RelativeLayout misBottomControlLayout;
    private CheckBox misPhotoviewOrginImage;
    private TextView misPhotoviewBackView;
    private Button misPhotoviewSend;

    private ImageView misLoadIndicatorView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.im_activity_photoview_layout);

        Intent intent = getIntent();
        picUrls = intent.getStringArrayListExtra(EXTRA_PIC_URLS);
        initIndex = intent.getIntExtra(EXTRA_DEFAUL_INDEX, 0);
        viewModel = intent.getBooleanExtra(EXTRA_VIEW_MODEL, true);
        mTargetActivity = intent.getStringExtra(EXTRA_TARGET_ACTIVITY);
        mImageCacheDir = intent.getStringExtra(EXTRA_IMAGE_CACHE_DIR);
        targetCompressWidth = intent.getIntExtra(EXTRA_TARGET_COMPRESS_WIDTH, DEFAULT_WIDTH);
        targetCompressHeight = intent.getIntExtra(EXTRA_TARGET_COMPRESS_HEIGHT, DEFAULT_HEIGHT);
        mCompleteBtnText = intent.getStringExtra(EXTRA_COMPLETE_BTN_TEXT);
        mDeleteOriginPicture = intent.getBooleanExtra(EXTRA_DELETE_ORIGIN_PICTURE, true);

        mOpenLongClickActionFlag = intent.getBooleanExtra(EXTRA_OPEN_LONG_CLICK_ACTION, false);
        imageFileSaveName = intent.getStringExtra(EXTRA_LONG_CLICK_IMAGE_SAVE_FILE_NAME);

        initView();

        if (viewModel) {
            adapterDatas.clear();
            if (null != picUrls) {
                adapterDatas.addAll(picUrls);
            }
            initData();
        } else {
            misPhotoviewSend.setEnabled(false);
            compress(picUrls);
        }

    }

    ;

    public void initView() {
        viewPager = (PhotoViewPager) findViewById(R.id.view_pager);

        misBottomControlLayout = (RelativeLayout) findViewById(R.id.mis_bottom_control_layout);
        misLoadIndicatorView = (ImageView) findViewById(R.id.mis_load_indicator_view);

        if (!viewModel) {
            misBottomControlLayout.setVisibility(View.GONE);
            misPhotoviewTitleBarLayout = (RelativeLayout) findViewById(R.id.mis_photoview_title_bar_layout);
            misPhotoviewBottomLayout = (RelativeLayout) findViewById(R.id.mis_photoview_bottom_layout);
            misPhotoviewOrginImage = (CheckBox) findViewById(R.id.mis_photoview_orgin_image);
            misPhotoviewOrginImage.setOnCheckedChangeListener(new OnCheckedChangeListener() {

                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    showImageSize(isChecked);
                }
            });
            misPhotoviewBackView = (TextView) findViewById(R.id.mis_photoview_back_view);
            misPhotoviewBackView.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    clearTempPicture();
                    finish();
                }
            });
            misPhotoviewSend = (Button) findViewById(R.id.mis_photoview_send);
            misPhotoviewSend.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    send();
                }
            });
            if (!TextUtils.isEmpty(mCompleteBtnText)) {
                misPhotoviewSend.setText(mCompleteBtnText);
            }

            misPhotoviewBottomLayout.setVisibility(View.VISIBLE);
            misPhotoviewTitleBarLayout.setVisibility(View.VISIBLE);
        }

        indexDisplayView = (TextView) findViewById(R.id.index_display_tv);
        functionMoreView = (ImageView) findViewById(R.id.function_more_view);
        functionMoreView.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {

            }
        });

        if (null != picUrls) {
            Log.i(TAG, "~~~~~ picUrls=" + picUrls);
            if (initIndex == 0) {
                indexDisplayView.setText(1 + "/" + picUrls.size());
            } else {
                indexDisplayView.setText((initIndex + 1) + "/" + picUrls.size());
            }
        } else {
            if (!viewModel) {
                misPhotoviewSend.setEnabled(false);
            }
        }

    }

    private void showImageSize(boolean show) {
        if (show) {
            if (null != picUrls && picUrls.size() > 0) {
                File file = new File(picUrls.get(0));
                if (file.exists()) {
                    long lenght = file.length();
                    String afterFormat = FileUtils.getFormatSize(lenght);
                    misPhotoviewOrginImage.setText("原图(" + afterFormat + ")");
                }
            }
        } else {
            misPhotoviewOrginImage.setText("原图");
        }
    }

    public void initData() {
        photoViewAdapter = new PhotoViewAdapter(PhotoViewActivity.this, adapterDatas);
        viewPager.setAdapter(photoViewAdapter);
        viewPager.setCurrentItem(initIndex);
        viewPager.setOnPageChangeListener(new OnPageChangeListener() {

            @Override
            public void onPageSelected(int position) {
                Log.i(TAG, "~~~~~~~ position=" + position + ",  Url=" + adapterDatas.get(position));
                indexDisplayView.setText((position + 1) + "/" + adapterDatas.size());
            }

            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2) {

            }

            @Override
            public void onPageScrollStateChanged(int arg0) {

            }
        });

        photoViewAdapter.setOnItemClickListener(new PhotoViewAdapter.OnItemClickListener() {

            @Override
            public void onClick(View view) {
                if (viewModel) {
                    Log.i(TAG, "~~~~~ View click");
                    finish();
                }
            }

            @Override
            public void onLongClick(View view, final String imgUrl) {
                if (mOpenLongClickActionFlag) {
                    if (imgUrl.contains("http://")) {
                        // 弹出dialog
                        final Dialog dialog = new Dialog(PhotoViewActivity.this, R.style.im_phone_dialog);
                        View contentView = View.inflate(PhotoViewActivity.this, R.layout.im_photo_dialog, null);
                        TextView saveTv = (TextView) contentView.findViewById(R.id.saveTv);
                        saveTv.setOnClickListener(new OnClickListener() {
                            @Override
                            public void onClick(View arg0) {
                                // 保存图片到手机
                                ImageLoader.getInstance().loadImage(imgUrl, new ImageLoadingListener() {

                                    @Override
                                    public void onLoadingCancelled(String arg0, View arg1) {

                                    }

                                    @Override
                                    public void onLoadingComplete(String arg0, View view, Bitmap bitmap) {
                                        saveImageToGallery(bitmap, imageFileSaveName);
                                    }

                                    @Override
                                    public void onLoadingFailed(String arg0, View arg1, FailReason arg2) {
                                        Toast.makeText(PhotoViewActivity.this, "下载失败...", Toast.LENGTH_LONG).show();
                                    }

                                    @Override
                                    public void onLoadingStarted(String arg0, View arg1) {

                                    }
                                });
                                dialog.dismiss();
                            }
                        });
                        TextView deleteTv = (TextView) contentView.findViewById(R.id.deleteTv);
                        deleteTv.setOnClickListener(new OnClickListener() {
                            @Override
                            public void onClick(View arg0) {
                                dialog.dismiss();
                            }
                        });
                        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                        dialog.setContentView(contentView);

                        Window dialogWindow = dialog.getWindow();
                        // 获取屏幕宽、高用
                        WindowManager wm = (WindowManager) PhotoViewActivity.this.getSystemService(Context.WINDOW_SERVICE);
                        Display display = wm.getDefaultDisplay();
                        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
                        lp.width = (int) (display.getWidth() * 0.65); // 宽度设置为屏幕的0.7
                        dialogWindow.setGravity(Gravity.CENTER);
                        dialogWindow.setAttributes(lp);
                        dialog.show();
                    }
                }
            }
        });
    }

    private void send() {
        resultList.clear();
        if (misPhotoviewOrginImage.isChecked()) {// 发送原文件
            resultList.addAll(picUrls);
            clearCompressPicture();
        } else {
            // 发送处理过的图片
            resultList.addAll(adapterDatas);// adapterDatas里面存储有压缩的和没有压缩成功而显示原始的所有图片
            if (mDeleteOriginPicture) {
                clearOriginPicture();
            }
        }
        if (!TextUtils.isEmpty(mTargetActivity)) {
            try {
                Intent goTargetActivity = new Intent();
                goTargetActivity.setClassName(getPackageName(), mTargetActivity);
                goTargetActivity.putStringArrayListExtra(EXTRA_RESULT, resultList);
                startActivity(goTargetActivity);
                finish();
                return;
            } catch (Exception e) {
            }
        }
        Intent data = new Intent();
        data.putStringArrayListExtra(EXTRA_RESULT, resultList);
        setResult(RESULT_OK, data);
        finish();
    }

    private int quality = 80;
    private static final int HANDLE_COMPRESS_MULTI = 0x00001;
    private Map<String, String> mCompressSuccessMap = new HashMap<String, String>();

    private Handler handler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case HANDLE_COMPRESS_MULTI:
                    misPhotoviewSend.setEnabled(true);
                    stopLoadAnimation();
                    adapterDatas.clear();
                    Map<String, String> compressSuccessMap = (Map<String, String>) msg.obj;
                    if (null != compressSuccessMap && compressSuccessMap.size() > 0) {// 说明有压缩成功的文件
                        mCompressSuccessMap.putAll(compressSuccessMap);
                        System.out.println("~~~~~~~~  原始有 " + picUrls.size() + "张图片");
                        System.out.println("~~~~~~~~  压缩成功有 " + picUrls.size() + "张图片");
                        if (null != picUrls && picUrls.size() > 0) {
                            for (String orginUrl : picUrls) {// 对应Map里面的Key
                                if (compressSuccessMap.containsKey(orginUrl)) {// 说明这张原始图片有压缩成功的图片
                                    adapterDatas.add(compressSuccessMap.get(orginUrl));
                                } else {
                                    adapterDatas.add(orginUrl);// 说明没有压缩成功，显示原始图片
                                }
                            }
                        }
                    } else {// 说明全部没有压缩成功，使用原始图片进行显示
                        adapterDatas.addAll(picUrls);
                    }
                    initData();
                    break;
            }
        }

        ;
    };

    private void stopLoadAnimation() {
        misLoadIndicatorView.setVisibility(View.GONE);
        misLoadIndicatorView.clearAnimation();
    }

    private void startLoadAnimation() {
        misLoadIndicatorView.setVisibility(View.VISIBLE);
        Animation loadAnimation = AnimationUtils.loadAnimation(this, R.anim.mis_anim_loading);
        misLoadIndicatorView.startAnimation(loadAnimation);
    }

    private void compress(final List<String> targetCompressPicUrls) {
        if (null == targetCompressPicUrls || targetCompressPicUrls.size() == 0) {
            return;
        }

        startLoadAnimation();
        Thread compressThread = new Thread(new Runnable() {

            @Override
            public void run() {
                int i = 1;
                Message message = handler.obtainMessage();
                message.what = HANDLE_COMPRESS_MULTI;

                Map<String, String> compressSuccessMap = new HashMap<String, String>();// 用来存储压缩成功的图片
                for (String compressPicUrl : targetCompressPicUrls) {
                    // 设置压缩后的图片存储目录
                    File outFile = FileUtils.createTmpFile(PhotoViewActivity.this, mImageCacheDir);
                    if (null != outFile) {
                        String tmpfileAbsolutePath = outFile.getAbsolutePath();
                        System.out.println("~~~~  开始压缩第" + i + "张图片");
                        try {
                            if (!TextUtils.isEmpty(compressPicUrl)) {
                                boolean result = ImageCompress
                                        .compress(compressPicUrl, tmpfileAbsolutePath, quality, targetCompressWidth, targetCompressHeight);
                                if (result) {
                                    System.out.println("~~~~~~ 压缩后的图片大小=" + FileUtils.getFormatSize(tmpfileAbsolutePath.length()));
                                    compressSuccessMap.put(compressPicUrl, tmpfileAbsolutePath);// key为原始图片地址，value为压缩后的图片地址
                                } else {
                                    System.out.println("~~~~~  图片压缩失败");
                                }
                                System.out.println("~~~~~  第" + i + "张图片压缩完成, 存放路径=" + tmpfileAbsolutePath);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            System.out.println("~~~~~  第" + i + "张图片压缩出现异常");
                        }
                    }
                    i++;
                }
                message.obj = compressSuccessMap;
                handler.sendMessage(message);
            }
        });
        compressThread.start();

    }

    public void clearTempPicture() {
        // 清理原始拍照的图片和被压缩处理后的图片
        if (null != picUrls && picUrls.size() > 0) {
            for (String orginPicUrl : picUrls) {
                File file = new File(orginPicUrl);
                if (file.exists()) {
                    file.delete();
                }
            }
        }
        if (null != mCompressSuccessMap && mCompressSuccessMap.size() > 0) {
            for (String key : mCompressSuccessMap.keySet()) {
                String compressUrl = mCompressSuccessMap.get(key);
                File file = new File(compressUrl);
                if (file.exists()) {
                    file.delete();
                }
            }
        }
    }

    public void clearCompressPicture() {
        // 清理处理过的临时文件
        if (null != mCompressSuccessMap && mCompressSuccessMap.size() > 0) {
            for (String key : mCompressSuccessMap.keySet()) {
                String url = mCompressSuccessMap.get(key);
                File file = new File(url);
                if (file.exists()) {
                    file.delete();
                }
            }
        }
    }

    public void clearOriginPicture() {
        if (null != picUrls && picUrls.size() > 0) {
            for (String url : picUrls) {
                if (!adapterDatas.contains(url)) {
                    File file = new File(url);
                    if (file.exists()) {
                        file.delete();
                    }
                }
            }
        }
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (!viewModel) {
                clearTempPicture();
            }
        }
        return super.onKeyUp(keyCode, event);
    }

    /**
     * cp.add 图片存储到系统图库 fileName 保存的文件夹名字
     */
    public void saveImageToGallery(Bitmap bmp, String fileName) {
        // 首先保存图片
        File appDir = new File(Environment.getExternalStorageDirectory(), fileName);
        if (!appDir.exists()) {
            appDir.mkdir();
        }
        String imgName = "wl" + System.currentTimeMillis() + ".jpg";
        File file = new File(appDir, imgName);
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(file);
            bmp.compress(CompressFormat.JPEG, 100, fos);
            fos.flush();
            fos.close();

            // 其次把文件插入到系统图库
            String path = appDir.getAbsolutePath() + File.separator + imgName;
            try {
                MediaStore.Images.Media.insertImage(getContentResolver(), file.getAbsolutePath(), imgName, imgName);
                //add by yiw  删除图片
                appDir.delete();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            // 最后通知图库更新
            if (!TextUtils.isEmpty(path)) {
                sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.parse("file://" + path)));
            }
            Toast.makeText(PhotoViewActivity.this, "图片保存至" + appDir.getPath() + File.separator + "文件夹", Toast.LENGTH_LONG).show();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
            Toast.makeText(PhotoViewActivity.this, "保存失败,请重试", Toast.LENGTH_LONG).show();
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(PhotoViewActivity.this, "保存失败,请重试", Toast.LENGTH_LONG).show();
        } finally {
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
