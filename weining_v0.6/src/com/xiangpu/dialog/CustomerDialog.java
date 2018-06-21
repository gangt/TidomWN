package com.xiangpu.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.konecty.rocket.chat.R;

/**
 * description: 自定义弹出框
 * autour: Andy
 * date: 2017/12/2 13:30
 * update: 2017/12/2
 * version: 1.0
 */
public class CustomerDialog extends Dialog {

    /**
     * title
     */
    protected TextView titleTv;

    /**
     * message
     */
    protected TextView messageTv;

    /**
     * 确认按钮
     */
    protected Button confirmBtn;

    /**
     * 间隔线
     */
    protected TextView lineTv;

    /**
     * 取消按钮
     */
    protected Button cancelBtn;

    public CustomerDialog(Context context) {
        super(context, R.style.CustomDialogStyle);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setCancelable(false);  // 是否可以撤销
        setContentView(R.layout.dialog_custom);
        titleTv = (TextView) findViewById(R.id.tv_title);
        messageTv = (TextView) findViewById(R.id.tv_message);
        confirmBtn = (Button) findViewById(R.id.btn_confirm);
        lineTv = (TextView) findViewById(R.id.tv_line);
        cancelBtn = (Button) findViewById(R.id.btn_cancel);
    }

    /**
     * 设置标题内容
     *
     * @param content 内容
     */
    public void setTitleText(String content) {
        titleTv.setText(content);
        titleTv.setVisibility(View.VISIBLE);
    }

    /**
     * 设置消息内容
     *
     * @param content 内容
     */
    public void setMessageText(String content) {
        messageTv.setText(content);
        messageTv.setVisibility(View.VISIBLE);
    }

    /**
     * 设置确认按钮点击事件
     *
     * @param clickListener 点击事件
     */
    public void setButtonEvent(View.OnClickListener clickListener) {
        confirmBtn.setOnClickListener(clickListener);
    }

    /**
     * 设置按钮文字和点击事件
     *
     * @param text          文字
     * @param clickListener 点击事件
     */
    public void setButtonEvent(String text, View.OnClickListener clickListener) {
        confirmBtn.setOnClickListener(clickListener);
        confirmBtn.setText(text);
    }

    /**
     * 设置取消按钮点击事件
     *
     * @param clickListener 点击事件
     */
    public void setCancelButtonEvent(View.OnClickListener clickListener) {
        lineTv.setVisibility(View.VISIBLE);
        cancelBtn.setVisibility(View.VISIBLE);
        cancelBtn.setOnClickListener(clickListener);
    }

    /**
     * 设置取消按钮文字和点击事件
     *
     * @param text          文字
     * @param clickListener 点击事件
     */
    public void setCancelButtonEvent(String text, View.OnClickListener clickListener) {
        lineTv.setVisibility(View.VISIBLE);
        cancelBtn.setVisibility(View.VISIBLE);
        cancelBtn.setOnClickListener(clickListener);
        cancelBtn.setText(text);
    }

    @Override
    public void show() {
        if (isShowing()) return;
        super.show();
    }

    @Override
    public void dismiss() {
        super.dismiss();
    }
}
