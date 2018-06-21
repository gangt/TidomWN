package com.xiangpu.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.konecty.rocket.chat.R;

/**
 * Created by Administrator on 2017/9/29 0029.
 * Info：刷脸登录失败dialog
 */

public class FaceLoginFailDialog extends Dialog implements View.OnClickListener {
    private TextView tv_try_again = null;
    private TextView tv_other_way = null;

    private OnFaceLoginFailListener onFaceLoginFailListener = null;

    public FaceLoginFailDialog(Context context) {
        this(context, R.style.dialog_style);
    }

    public FaceLoginFailDialog(Context context, int themeResId) {
        super(context, themeResId);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_face_login_fail);
        tv_try_again = (TextView) findViewById(R.id.tv_try_again);
        tv_other_way = (TextView) findViewById(R.id.tv_other_way);
        initListener();
    }

    private void initListener() {
        tv_try_again.setOnClickListener(this);
        tv_other_way.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_try_again:
                // 再试一次
                if (onFaceLoginFailListener != null) {
                    onFaceLoginFailListener.tryAgain();
                }
                break;
            case R.id.tv_other_way:
                // 其他登录方式
                if (onFaceLoginFailListener != null) {
                    onFaceLoginFailListener.otherWay();
                }
                break;
        }
        dismiss();
    }

    public void setOnFaceLoginFailListener(OnFaceLoginFailListener listener) {
        this.onFaceLoginFailListener = listener;
    }

    public interface OnFaceLoginFailListener {
        void tryAgain();

        void otherWay();
    }

}
