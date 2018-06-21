package com.xiangpu.activity.usercenter;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.konecty.rocket.chat.R;
import com.lssl.activity.SuneeeApplication;
import com.xiangpu.common.Constants;
import com.xiangpu.utils.LogUtil;
import com.xiangpu.utils.ScreenUtil;
import com.xiangpu.utils.SharedPrefUtils;

import org.apache.cordova.ConfigXmlParser;
import org.apache.cordova.CordovaInterfaceImpl;
import org.apache.cordova.CordovaWebView;
import org.apache.cordova.CordovaWebViewImpl;
import org.apache.cordova.engine.SystemWebView;
import org.apache.cordova.engine.SystemWebViewEngine;

/**
 * Created by Administrator on 2017/12/5 0005.
 * Info：
 */

public class PersonDataFragment extends Fragment {
    private View rootView = null;
    private SystemWebView systemWebView = null;

    private Activity activity = null;
    private CordovaInterfaceImpl cordovaInterface = null;
    private CordovaWebView cordovaWebView = null;

    public static PersonDataFragment newInstance() {
        PersonDataFragment fragment = new PersonDataFragment();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.fragment_person_data, container, false);
        systemWebView = (SystemWebView) rootView.findViewById(R.id.system_web_view);

        activity = getActivity();
        cordovaInterface = new CordovaInterfaceImpl(activity);
        if (savedInstanceState != null) {
            cordovaInterface.restoreInstanceState(savedInstanceState);
        }
        ConfigXmlParser parser = new ConfigXmlParser();
        parser.parse(activity);
        cordovaWebView = new CordovaWebViewImpl(new SystemWebViewEngine(systemWebView));
        cordovaWebView.init(cordovaInterface, parser.getPluginEntries(), parser.getPreferences());
        loadConfig();
        return rootView;
    }

    public void loadConfig() {
        StringBuilder stringBuilder = new StringBuilder(Constants.PERSON_CENTER_DATA);
        String sessionId = SharedPrefUtils.getSessionId(getActivity());
        stringBuilder.append(sessionId);
        stringBuilder.append("&navigationHeight=");
        int navigationHeight = ScreenUtil.px2dip(activity, ScreenUtil.getBottomStatusHeight(activity));
        stringBuilder.append(navigationHeight);
        String loadUrl = stringBuilder.toString();
        LogUtil.d("loadUrl: " + loadUrl);
        cordovaWebView.loadUrl(loadUrl);
        cordovaWebView.handlePause(true);
    }

}