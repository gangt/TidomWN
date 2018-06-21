package com.xiangpu.activity.usercenter;

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
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by fangfumin on 2017/5/13.
 * Info：
 */

public class ChangePasswordActivity extends BaseActivity {
    private String loadUrl = "";
    private TextView network_default = null;
    private SystemWebView cordova_web_view = null;
    private ImageView iv_title = null;
    private TextView tv_title = null;

    private CordovaInterfaceImpl cordovaInterface = new CordovaInterfaceImpl(this) {
        @Override
        public Object onMessage(String id, Object data) {
            if ("onPageStarted".equals(id)) {
            }
            if ("onPageFinished".equals(id)) {
                network_default.setVisibility(View.GONE);
            }
            if ("onReceivedError".equals(id)) {
                final JSONObject object = (JSONObject) data;
                network_default.setVisibility(View.VISIBLE);
                network_default.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        try {
                            cordovaWebView.loadUrl(object.getString("url"));
                            network_default.setVisibility(View.GONE);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
            return super.onMessage(id, data);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);
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
        tv_title.setText("修改密码");
        network_default = (TextView) findViewById(R.id.network_default);
        cordova_web_view = (SystemWebView) findViewById(R.id.cordova_web_view);

        ConfigXmlParser parser = new ConfigXmlParser();
        parser.parse(this);
        cordovaWebView = new CordovaWebViewImpl(new SystemWebViewEngine(cordova_web_view));
        cordovaWebView.init(cordovaInterface, parser.getPluginEntries(), parser.getPreferences());
    }

    @Override
    protected void netBrResult(boolean netResult) {
        super.netBrResult(netResult);
        if (netResult) {
            initWeb();
        }
    }

    private void initWeb() {
        String sessionId = SharedPrefUtils.getSessionId(this);
        loadUrl = Constants.PERSON_CENTER_CHANGE_PASSWORD + sessionId;
        LogUtil.d("loadUrl: " + loadUrl);

        cordovaWebView.loadUrl(loadUrl);
        cordovaWebView.handlePause(true);
    }

}
