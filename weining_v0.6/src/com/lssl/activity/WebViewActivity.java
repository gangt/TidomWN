package com.lssl.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.ImageButton;

import com.konecty.rocket.chat.R;
import com.lssl.AndroidUtilities;
import com.lssl.weight.ProgressWebView;
import com.lssl.weight.TitleHeaderBar;
import com.lssl.weight.WebBackImageView;
import com.xiangpu.activity.BaseActivity;


/**
 * Created by suneee on 2016/12/24.
 */

public class WebViewActivity extends BaseActivity {

    public static String type_xsrb;
    //自定义带进度条的webview
    private WebView appView;
    private ImageButton rightBtn;
    private boolean landscape;
    private String mtitle;
    private String url;
    private String urlV;
    private boolean loadhead;//是否显示标题
    private TitleHeaderBar titleBar;

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        mtitle = getIntent().getStringExtra("title");
        url = getIntent().getStringExtra("url");
        urlV = getIntent().getStringExtra("urlv");
        landscape = getIntent().getBooleanExtra("landscape", false);
        loadhead= getIntent().getBooleanExtra("loadhead", true);


        setContentView(R.layout.activity_webmain_login);

        appView = (WebView)this.findViewById(R.id.ll_system_webView);

        initWebView();

        appView.loadUrl(url);

//        if(!loadhead){
//            addBackView();
//        }

        if(mtitle.equals("象谱商城")){
            loadPoint("ShoppingMall", mtitle);
        } else {
            loadPoint("", mtitle);
        }
    }

//    private void addBackView() {
//        FrameLayout.LayoutParams backParams = new FrameLayout.LayoutParams
//                (FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT);
//        backParams.bottomMargin = 60;
//        backParams.rightMargin = 45;
//        //设置底部
//        backParams.gravity= Gravity.BOTTOM|Gravity.RIGHT;
//        WebBackImageView back = new WebBackImageView(this);
//        back.setBackgroundResource(R.drawable.lssl_web_back);
//        back.setClickable(true);
//        back.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                finish();
//            }
//        });
//        //添加控件
//        addContentView(back, backParams);
//    }

    @Override
    protected void onResume() {
        super.onResume();
        if(mtitle.equals(type_xsrb)){
            changeScreen();
        }
    }

    private void changeScreen() {

         //设置为横屏
        if(landscape && getRequestedOrientation()!= ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE){
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        }

        if(!landscape && getRequestedOrientation()!= ActivityInfo.SCREEN_ORIENTATION_PORTRAIT){
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
    }

    private void initWebView() {
        String defalutUA = appView.getSettings().getUserAgentString();
        appView.getSettings().setUserAgentString(defalutUA + "Weilian_Android");
        appView.setVerticalScrollBarEnabled(false);
        appView.setOnLongClickListener(new WebView.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                return true;
            }
        });

        WebSettings webSettings = appView.getSettings();
        // webview支持js脚本
        webSettings.setJavaScriptEnabled(true);

        //appView.getSettings().setBlockNetworkImage(true);//延迟加载图片

        //---------------------------cp.add 开启数据缓存功能（2016-03-10）
        // 设置可以使用localStorage
        webSettings.setDomStorageEnabled(true);
        // 应用可以有数据库
        webSettings.setDatabaseEnabled(true);
        String dbPath =this.getApplicationContext().getDir("database", Context.MODE_PRIVATE).getPath();
        webSettings.setDatabasePath(dbPath);
        // 应用可以有缓存
        webSettings.setAppCacheEnabled(true);
        String appCaceDir =this.getApplicationContext().getDir("cache", Context.MODE_PRIVATE).getPath();
        webSettings.setAppCachePath(appCaceDir);
        //-------------------------------end

        // -------------------------cp.add用于启动h5定位
        // 设置定位的数据库路径
        String dir = this.getApplicationContext().getDir("database", Context.MODE_PRIVATE).getPath();
        webSettings.setGeolocationDatabasePath(dir);
        // 启用地理定位
        webSettings.setGeolocationEnabled(true);
        // -----------------------------------------end

        // ----------------------------------------cp.add 开启页面缓存功能
        webSettings.setCacheMode(WebSettings.LOAD_NO_CACHE);
        webSettings.setAppCacheMaxSize(5*1024*1024);   //缓存最多可以有5M
        webSettings.setAllowFileAccess(true);
        // syncCookie(this, preUrl);
        // ------------------------------------------end

        // 通知客户端app加载当前网页时的各种时机状态
        /*appView.setWebChromeClient(new WebChromeClient() {
            //用于启用定位功能
            @Override
            public void onGeolocationPermissionsShowPrompt(String origin, GeolocationPermissions.Callback callback) {
                callback.invoke(origin, true, false);
                super.onGeolocationPermissionsShowPrompt(origin, callback);
            }

            @Override
            public void onReceivedTitle(WebView view, String title) {
                super.onReceivedTitle(view, title);
               *//* if (StringUtils.isEmpty(title)){
                    titleBar.setTitleText(mtitle);
                }else{
                    if("http://sun.suneee.com/proProgress.html".equals(url)){
                        titleBar.setTitleText("生产进度");
                    }else if ("http://sun.suneee.com/sales_rb.html".equals(url)||"http://sun.suneee.com/sale.html".equals(url)){
                        titleBar.setTitleText(mtitle);
                    } else{
                        titleBar.setTitleText(title);
                    }
                }*//*
            }
        });*/

        appView.setWebViewClient(new WebViewClient(){
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                  /*   if("http://sun.suneee.com/proProgress.html".equals(url)){
                          titleBar.setTitleText("生产进度");
                      }*/
                     view.loadUrl(url);
                return super.shouldOverrideUrlLoading(view, url);
            }
        });
    }

    @Override
    public void onBackPressed() {
        doReBack();
    }

    private void doReBack() {
        if (appView.canGoBack()) {//回退h5页面
            appView.goBack();
            titleBar.setTitleText("事件轴");
        } else {		//h5没有回退页面，则关闭当前页面
            finish();
        }
    }

}
