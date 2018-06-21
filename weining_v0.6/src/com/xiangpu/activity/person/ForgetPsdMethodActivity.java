package com.xiangpu.activity.person;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ImageButton;

import com.konecty.rocket.chat.R;
import com.xiangpu.activity.BaseActivity;

/**
 * Created by fangfumin on 2017/8/31.
 */

public class ForgetPsdMethodActivity extends BaseActivity {

    private ImageButton mobileBtn;
    private ImageButton emailBtn;
    private ImageButton backBtn;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.forget_psd_method_layout);

        initView();
        initData();
        
    }

    private void initView() {
        mobileBtn = (ImageButton) findViewById(R.id.mobile_check_btn);
        emailBtn = (ImageButton) findViewById(R.id.email_check_btn);
        backBtn = (ImageButton) findViewById(R.id.img_back_id);
    }

    private void initData() {
        mobileBtn.setOnClickListener(this);
        emailBtn.setOnClickListener(this);
        backBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.mobile_check_btn:
                Intent mobileIntent = new Intent(this, LocalCheckActivity.class);
                mobileIntent.putExtra("type", "mobile");
                startActivity(mobileIntent);
                break;

            case R.id.email_check_btn:
                Intent emailIntent = new Intent(this, LocalCheckActivity.class);
                emailIntent.putExtra("type", "email");
                startActivity(emailIntent);
                break;

            case R.id.img_back_id:
                this.finish();
                break;

            default:
                super.onClick(v);
                break;
        }
    }
}
