package com.xiangpu.activity.usercenter;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.konecty.rocket.chat.R;
import com.xiangpu.common.Constants;

import org.apache.cordova.ConfigXmlParser;
import org.apache.cordova.CordovaInterfaceImpl;
import org.apache.cordova.CordovaWebView;
import org.apache.cordova.CordovaWebViewImpl;
import org.apache.cordova.engine.SystemWebView;
import org.apache.cordova.engine.SystemWebViewEngine;

/**
 * Created by Administrator on 2017/12/3 0003.
 * Info：
 */

public class HelpFeedbackFragment extends Fragment {
    private View rootView = null;
    private SystemWebView system_web_view = null;

    private Activity activity = null;
    private CordovaInterfaceImpl cordovaInterface = null;
    private CordovaWebView cordovaWebView = null;

    public static HelpFeedbackFragment newInstance() {
        HelpFeedbackFragment fragment = new HelpFeedbackFragment();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_person_data, container, false);
        system_web_view = (SystemWebView) rootView.findViewById(R.id.system_web_view);

        activity = getActivity();
        cordovaInterface = new CordovaInterfaceImpl(activity);
        if (savedInstanceState != null) {
            cordovaInterface.restoreInstanceState(savedInstanceState);
        }
        loadConfig();
        return rootView;
    }

    private void loadConfig() {
        ConfigXmlParser parser = new ConfigXmlParser();
        parser.parse(activity);
        cordovaWebView = new CordovaWebViewImpl(new SystemWebViewEngine(system_web_view));
        cordovaWebView.init(cordovaInterface, parser.getPluginEntries(), parser.getPreferences());
        cordovaWebView.loadUrl(Constants.PERSON_CENTER_HELP_FEED);
        cordovaWebView.handlePause(true);
    }

    /**
     * 网页回退
     */
    public void goBack() {
        if (system_web_view.canGoBack()) {
            system_web_view.goBack();
        } else {
            activity.finish();
        }
    }

}
