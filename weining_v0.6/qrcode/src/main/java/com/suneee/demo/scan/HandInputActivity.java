/*
    Suneee Android Client, HandInputActivity
    Copyright (c) 2015 Suneee Tech Company Limited
 */

package com.suneee.demo.scan;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.Toast;

import com.suneee.demo.scan.view.TitleHeaderBar;
import com.suneee.qrcode.R;

/**
 * [手动输入（功能暂时屏蔽）]
 * 
 * @author galaxy_xiong
 * @version 1.0
 * @date 2015年9月1日
 * 
 **/
public class HandInputActivity extends FragmentActivity implements OnClickListener,
		TextWatcher {

	private TitleHeaderBar titleHeaderBar;
	private EditText inputEditText;
	private String regex = "^[A-Za-z0-9]+$";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getWindow().setBackgroundDrawable(null);
		this.setContentView(R.layout.hand_input_layout);
		initView();
	}

	private void initView() {
		this.inputEditText = (EditText) this.findViewById(R.id.hand_input_et);
		this.inputEditText.addTextChangedListener(this);
		this.titleHeaderBar = (TitleHeaderBar) this.findViewById(R.id.titleBar);
		this.titleHeaderBar.setTitleText("手动输入");
		this.titleHeaderBar.getRightTextView().setText("完成");
		this.titleHeaderBar.getRightTextView().setOnClickListener(this);
	}

	@Override
	public void onClick(View arg0) {
		if (!TextUtils.isEmpty(this.inputEditText.getText().toString())) {
			String result = this.inputEditText.getText().toString();
			if (result.matches(regex)) {
				//手动输入，非法输入跳转至指定链接（暂时屏蔽）
//				H5RelateWebActivityBundle bundle = new H5RelateWebActivityBundle();
//				bundle.setUrl("http://h.weilian.cn/shop/index.php");
//				bundle.setAutoReadTitle(false);
//				bundle.setTitle("桂林微链社区");
//				Intent preWebIntent2 = H5RelateWebActivity.newIntentInstance(this, bundle);
//				startActivity(preWebIntent2);
			} else {
				Toast.makeText(this, "输入的只能为字母或数字，亲！", Toast.LENGTH_SHORT)
						.show();
			}
		} else {
			Toast.makeText(this, "输入不能为空!", Toast.LENGTH_SHORT).show();
		}

	}

	@Override
	public void afterTextChanged(Editable arg0) {

	}

	@Override
	public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
			int arg3) {

	}

	@Override
	public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {

	}
}
