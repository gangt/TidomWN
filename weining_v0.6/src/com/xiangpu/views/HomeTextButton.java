package com.xiangpu.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.konecty.rocket.chat.R;

/**
 * description: 主页带文字的button
 * autour: Andy
 * date: 2018/1/26 20:53
 * update: 2018/1/26
 * version: 1.0
 */
public class HomeTextButton extends LinearLayout {

    private ImageView img;
    private TextView textView;

    public HomeTextButton(Context context) {
        super(context);

    }

    public HomeTextButton(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initView(context, attrs);
    }

    public HomeTextButton(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void initView(Context context, AttributeSet attrs) {
        LayoutInflater.from(context).inflate(R.layout.home_text_button, this, true);
        img = (ImageView) findViewById(R.id.image);
        textView = (TextView) findViewById(R.id.title);

        TypedArray attributes = context.obtainStyledAttributes(attrs, R.styleable.HomeTextButton);
        if (attributes != null) {
            // 处理image
            // image的icon

            // 处理textview
            // textview文字
            String titleText = attributes.getString(R.styleable.HomeTextButton_title_text);
            if (!TextUtils.isEmpty(titleText)) {
                textView.setText(titleText);
            }
            // textview颜色
            int titleTextColor = attributes.getColor(R.styleable.HomeTextButton_title_text_color, Color.WHITE);
            textView.setTextColor(titleTextColor);
            // textview文字大小
            float titleTextSize = attributes.getFloat(R.styleable.HomeTextButton_title_text_size, 14);
            textView.setTextSize(titleTextSize);

//            //处理titleBar背景色
//            int titleBarBackGround = attributes.getResourceId(R.styleable.CustomTitleBar_title_background_color, Color.GREEN);
//            setBackgroundResource(titleBarBackGround);
//            //先处理左边按钮
//            //获取是否要显示左边按钮
//            boolean leftButtonVisible = attributes.getBoolean(R.styleable.CustomTitleBar_left_button_visible, true);
//            if (leftButtonVisible) {
//                titleBarLeftBtn.setVisibility(View.VISIBLE);
//            } else {
//                titleBarLeftBtn.setVisibility(View.INVISIBLE);
//            }
//            //设置左边按钮的文字
//            String leftButtonText = attributes.getString(R.styleable.CustomTitleBar_left_button_text);
//            if (!TextUtils.isEmpty(leftButtonText)) {
//                titleBarLeftBtn.setText(leftButtonText);
//                //设置左边按钮文字颜色
//                int leftButtonTextColor = attributes.getColor(R.styleable.CustomTitleBar_left_button_text_color, Color.WHITE);
//                titleBarLeftBtn.setTextColor(leftButtonTextColor);
//            } else {
//                //设置左边图片icon 这里是二选一 要么只能是文字 要么只能是图片
//                int leftButtonDrawable = attributes.getResourceId(R.styleable.CustomTitleBar_left_button_drawable, R.mipmap.titlebar_back_icon);
//                if (leftButtonDrawable != -1) {
//                    titleBarLeftBtn.setBackgroundResource(leftButtonDrawable);
//                }
//            }
//
//            //处理标题
//            //先获取标题是否要显示图片icon
//            int titleTextDrawable = attributes.getResourceId(R.styleable.CustomTitleBar_title_text_drawable, -1);
//            if (titleTextDrawable != -1) {
//                titleBarTitle.setBackgroundResource(titleTextDrawable);
//            } else {
//                //如果不是图片标题 则获取文字标题
////                String titleText = attributes.getString(R.styleable.CustomTitleBar_title_text);
////                if (!TextUtils.isEmpty(titleText)) {
////                    titleBarTitle.setText(titleText);
////                }
//                //获取标题显示颜色
//                int titleTextColor = attributes.getColor(R.styleable.CustomTitleBar_title_text_color, Color.WHITE);
//                titleBarTitle.setTextColor(titleTextColor);
//            }
//
//            //先处理右边按钮
//            //获取是否要显示右边按钮
//            boolean rightButtonVisible = attributes.getBoolean(R.styleable.CustomTitleBar_right_button_visible, true);
//            if (rightButtonVisible) {
//                titleBarRightBtn.setVisibility(View.VISIBLE);
//            } else {
//                titleBarRightBtn.setVisibility(View.INVISIBLE);
//            }
//            //设置右边按钮的文字
//            String rightButtonText = attributes.getString(R.styleable.CustomTitleBar_right_button_text);
//            if (!TextUtils.isEmpty(rightButtonText)) {
//                titleBarRightBtn.setText(rightButtonText);
//                //设置右边按钮文字颜色
//                int rightButtonTextColor = attributes.getColor(R.styleable.CustomTitleBar_right_button_text_color, Color.WHITE);
//                titleBarRightBtn.setTextColor(rightButtonTextColor);
//            } else {
//                //设置右边图片icon 这里是二选一 要么只能是文字 要么只能是图片
//                int rightButtonDrawable = attributes.getResourceId(R.styleable.CustomTitleBar_right_button_drawable, -1);
//                if (rightButtonDrawable != -1) {
//                    titleBarRightBtn.setBackgroundResource(rightButtonDrawable);
//                }
//            }
            attributes.recycle();
        }
    }

}
