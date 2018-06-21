package com.xiangpu.activity.usercenter;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.konecty.rocket.chat.R;
import com.lssl.activity.SuneeeApplication;
import com.xiangpu.activity.BaseActivity;
import com.xiangpu.common.Constants;
import com.xiangpu.utils.LogUtil;
import com.xiangpu.utils.SharedPrefUtils;

import org.apache.cordova.ConfigXmlParser;
import org.apache.cordova.CordovaInterfaceImpl;
import org.apache.cordova.CordovaWebViewImpl;
import org.apache.cordova.engine.SystemWebView;
import org.apache.cordova.engine.SystemWebViewEngine;

/**
 * Created by Administrator on 2017/12/15 0015.
 * Info：
 */

public class PersonDataSecondActivity extends BaseActivity {
    private String loadUrl = "";
    private SystemWebView cordova_web_view = null;
    private ImageView iv_title = null;
    private TextView tv_title = null;

    private CordovaInterfaceImpl cordovaInterface = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_person_data_second);
        loadPoint("", "个人中心");
        initView();
        initNetBroadcastReceiver();
    }

    private void initView() {
        iv_title = (ImageView) findViewById(R.id.iv_title);
        iv_title.setImageResource(R.drawable.person_center_back);
        iv_title.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        tv_title = (TextView) findViewById(R.id.tv_title);
        cordova_web_view = (SystemWebView) findViewById(R.id.cordova_web_view);

        Intent intent = getIntent();
        if (intent.hasExtra(Constants.INTENT_ACTIVITY)) {
            tv_title.setText(intent.getStringExtra(Constants.INTENT_ACTIVITY));
        }
        ConfigXmlParser parser = new ConfigXmlParser();
        parser.parse(this);
        cordovaInterface = new CordovaInterfaceImpl(this);
        cordovaWebView = new CordovaWebViewImpl(new SystemWebViewEngine(cordova_web_view));
        cordovaWebView.init(cordovaInterface, parser.getPluginEntries(), parser.getPreferences());
    }

    @Override
    protected void netBrResult(boolean netResult) {
        super.netBrResult(netResult);
        if (netResult) {
            initCordova();
        }
    }

    private void initCordova() {
        String sessionId = SharedPrefUtils.getSessionId(this);
        loadUrl = Constants.PERSON_CENTER_DATA_SECOND + sessionId;

        cordovaWebView.loadUrl(loadUrl);
        cordovaWebView.handlePause(true);
    }

}
