package com.xiangpu.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.konecty.rocket.chat.R;

/**
 * Created by Administrator on 2017/11/29 0029.
 * Info：
 */

public class CommonDialog extends Dialog implements View.OnClickListener {
    private Context context = null;
    private TextView tv_title = null;
    private TextView tv_content = null;
    private TextView tv_confirm = null;
    private TextView tv_cancel = null;
    private View v_vertical_line;

    private OnResultListener onResultListener = null;

    public CommonDialog(Context context) {
        this(context, R.style.dialog_style);
    }

    public CommonDialog(Context context, int themeResId) {
        super(context, themeResId);
        this.context = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_common);
        init();
    }

    private void init() {
        tv_title = (TextView) findViewById(R.id.tv_title);
        tv_content = (TextView) findViewById(R.id.tv_content);
        tv_confirm = (TextView) findViewById(R.id.tv_confirm);
        tv_cancel = (TextView) findViewById(R.id.tv_cancel);
        v_vertical_line = findViewById(R.id.v_vertical_line);

        tv_confirm.setOnClickListener(this);
        tv_cancel.setOnClickListener(this);
    }

    public void setConfirmButtonText(String text) {
        if (tv_confirm != null && text != null) {
            tv_confirm.setText(text);
        }
    }

    public void setConfirmButtonText(String text, int color) {
        if (tv_confirm != null && text != null) {
            tv_confirm.setText(text);
            tv_confirm.setTextColor(color);
        }
    }

    public void setCancelButtonText(String text) {
        if (tv_cancel != null && text != null) {
            tv_cancel.setText(text);
        }
    }

    /**
     * @param text
     * @param only 是否只设置取消键
     */
    public void setCancelButtonText(String text, boolean only) {
        if (tv_cancel != null && text != null) {
            tv_cancel.setText(text);
            if (only) {
                tv_confirm.setVisibility(View.GONE);
                v_vertical_line.setVisibility(View.GONE);
            }
        }
    }


    public void setTvTitle(String title) {
        if (tv_title != null && title != null) {
            tv_title.setText(title);
        }
    }

    /**
     * @param content：
     */
    public void setTvContent(String content) {
        if (tv_content != null && content != null) {
            tv_content.setText(content);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_confirm:
                // 确认
                if (onResultListener != null) {
                    onResultListener.onConfirm();
                }
                break;

            case R.id.tv_cancel:
                // 取消
                if (onResultListener != null) {
                    onResultListener.onCancel();
                }
                break;
        }
        dismiss();
    }

    public void setOnResultListener(OnResultListener listener) {
        this.onResultListener = listener;
    }

    public interface OnResultListener {
        void onConfirm();

        void onCancel();
    }

}
