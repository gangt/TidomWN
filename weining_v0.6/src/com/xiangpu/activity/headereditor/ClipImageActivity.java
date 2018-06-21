package com.xiangpu.activity.headereditor;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.konecty.rocket.chat.R;
import com.xiangpu.activity.BaseActivity;
import com.xiangpu.common.Constants;
import com.xiangpu.utils.BitmapUtils;
import com.xiangpu.utils.LogUtil;
import com.xiangpu.utils.Utils;

public class ClipImageActivity extends BaseActivity {
    private ImageView iv_title = null;
    private TextView tv_confirm = null;
    private ClipImageLayout clip_image_layout = null;
    private String imagePath = "";
    private Bitmap mBitmap = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_clip_header);
        findView();
        initView();
    }

    private void findView() {
        iv_title = (ImageView) findViewById(R.id.iv_title);
        tv_confirm = (TextView) findViewById(R.id.tv_confirm);
        clip_image_layout = (ClipImageLayout) findViewById(R.id.clip_image_layout);

        iv_title.setOnClickListener(this);
        tv_confirm.setOnClickListener(this);
    }

    private void initView() {
        if (getIntent().hasExtra("imagePath")) {
            imagePath = getIntent().getStringExtra("imagePath");
            String targetPath = Utils.getMakeRecordPath("makeRecord");
            BitmapUtils.compressBitmap(imagePath, targetPath, 960); // 压缩

            if (targetPath != null) {
                Bitmap bitmap = BitmapUtils.decodeBitmap(targetPath, 480, 480);
                if (bitmap != null) {
                    clip_image_layout.setZoomImageView(Utils.BmpToDraw(bitmap));
                }
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_title:
                finish();
                break;
            case R.id.tv_confirm:
                mBitmap = clip_image_layout.clip();
                if (mBitmap != null) {
                    String clipName = System.currentTimeMillis() + "";
                    String clipFilePath = Utils.saveBitmapToFile(clipName, mBitmap);
                    Intent resultIntent = new Intent();
                    clipName = Constants.CROP_CACHE_FILE_NAME + clipName + ".jpg";
                    LogUtil.d("clipName: " + clipName + " clipFilePath: " + clipFilePath);
                    resultIntent.putExtra("clipName", clipName);
                    resultIntent.putExtra("clipFilePath", clipFilePath);
                    setResult(RESULT_OK, resultIntent);
                    finish();
                }
        }
    }

}
