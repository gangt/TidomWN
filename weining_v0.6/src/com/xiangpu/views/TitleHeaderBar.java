package com.xiangpu.views;

import com.konecty.rocket.chat.R;
import com.xiangpu.utils.SoftKeyboardUtils;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class TitleHeaderBar extends RelativeLayout {

    private TextView mTitleTextView;
    private TextView mRightTextView;
    private TextView mLeftTextView;

    public TitleHeaderBar(Context context) {
        this(context, null);
    }

    public TitleHeaderBar(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TitleHeaderBar(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        LayoutInflater.from(context).inflate(getLayoutId(), this);
        mLeftTextView = (TextView) findViewById(R.id.tv_title_bar_left);
        mTitleTextView = (TextView) findViewById(R.id.tv_title_bar_title);
        mRightTextView = (TextView) findViewById(R.id.tv_title_bar_right);
        enableBackKey(true);
    }

    protected int getLayoutId() {
        return R.layout.base_header_bar_title;
    }

    public TextView getLeftTextView() {
        return mLeftTextView;
    }

    public TextView getTitleTextView() {
        return mTitleTextView;
    }

    public TextView getRightTextView() {
        return mRightTextView;
    }

    public void setTitleText(String titleTxt) {
        if (titleTxt != null)
            mTitleTextView.setText(titleTxt.trim());
    }

    public CharSequence getText() {
        if (null != mTitleTextView) {
            return mTitleTextView.getText();
        }
        return "";
    }

    public void setLeftViewImageRes(int res) {
        Drawable drawable = getResources().getDrawable(res);
        drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
        mLeftTextView.setCompoundDrawables(drawable, null, null, null);
    }

    public void setRightViewImageRes(int res) {
        mRightTextView.setCompoundDrawablesWithIntrinsicBounds(0, 0, res, 0);
    }

    public void enableBackKey(boolean enable) {
        if (enable) {
            findViewById(R.id.ly_title_bar_left).setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (getContext() != null) {
                        SoftKeyboardUtils.hideKeyboard((Activity) getContext());
                        ((Activity) getContext()).onBackPressed();
                    }
                }
            });
        } else {
            findViewById(R.id.ly_title_bar_left).setVisibility(GONE);
        }
    }

    public void setRightOnClickListener(OnClickListener l) {
        findViewById(R.id.ly_title_bar_right).setOnClickListener(l);
    }

    public void setLeftOnClickListener(OnClickListener l) {
        findViewById(R.id.ly_title_bar_left).setOnClickListener(l);
    }

    public void setBackgroundColor(int colorId) {
        findViewById(R.id.rl_title).setBackgroundColor(colorId);
    }

}