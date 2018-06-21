package com.suneee.sepay;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.suneee.sepay.core.sepay.config.WXConfig;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;


/**
 * 微信注册的广播
 */
public class WXAppRegister extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		final IWXAPI msgApi = WXAPIFactory.createWXAPI(context, null);

		// 将该app注册到微信
		msgApi.registerApp(WXConfig.APP_ID);
	}
}
