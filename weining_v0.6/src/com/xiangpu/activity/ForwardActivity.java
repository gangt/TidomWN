package com.xiangpu.activity;

import com.lssl.activity.MainActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

public class ForwardActivity extends BaseActivity {

	@Override
	protected void onCreate(Bundle arg0) {
		// TODO Auto-generated method stub
		super.onCreate(arg0);

		Intent i = new Intent(this, MainActivity.class);
		this.startActivity(i);

		finish();
	}
}
