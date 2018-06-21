/*
    Suneee Android Client, ScanResultActivity
    Copyright (c) 2015 Suneee Tech Company Limited
 */

package com.suneee.demo.scan;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.widget.TextView;

import com.suneee.qrcode.R;

/**
 * [扫描二维码 条形码非链接的结果显示]
 * 
 * @author galaxy_xiong
 * @version 1.0
 * @date 2015年8月27日
 * 
 **/
public class ScanResultActivity extends FragmentActivity {
	private TextView resultTv;
	private String resulstStr;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.scan_result_layout);
		initData();
		initView();
	}

	private void initView() {
		resultTv = (TextView) findViewById(R.id.result_tv);
		if (TextUtils.isEmpty(resulstStr)) {
			resulstStr = "啥都没有喔，亲!";
		}

		resultTv.setText(resulstStr);
	}

	private void initData() {
		this.resulstStr = this.getIntent().getStringExtra("result");
	}

}
