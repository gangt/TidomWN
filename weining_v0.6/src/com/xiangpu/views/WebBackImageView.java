package com.xiangpu.views;

import com.xiangpu.utils.LogUtil;

import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.widget.ImageView;

/**
 * Created by suneee on 2016/12/26.
 */

public class WebBackImageView extends ImageView {

    private static final String TAG = "WebBackImageView";
    private int lastX;
    private int lastY;
    private int screenWidth;
    private int screenHight;
    private int width;
    private float startx;
    private float starty;

    private int totalX = 0;
    private int totalY = 0;

    private static int margin = 15;
    private static int marginBottom = 60;

    private Context mContext;
    private Activity myActivity;

    private WindowManager wm;
    private LayoutParams layoutParams;

    public WebBackImageView(Context context) {
        super(context);

        wm = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);

        screenWidth = wm.getDefaultDisplay().getWidth();
        screenHight = wm.getDefaultDisplay().getHeight();

        mContext = context;
        myActivity = (Activity) context;
    }

    public WebBackImageView(Context context, LayoutParams layoutParams) {
        super(context);

        wm = (WindowManager) getContext()
                .getSystemService(Context.WINDOW_SERVICE);

        screenWidth = wm.getDefaultDisplay().getWidth();
        screenHight = wm.getDefaultDisplay().getHeight();

        mContext = context;
        myActivity = (Activity) context;
        this.layoutParams = layoutParams;

    }

    public WebBackImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public WebBackImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public boolean onTouchEvent(MotionEvent event) {
        if (width <= 0) {
            width = getWidth();
        }

        //鑾峰彇鍒版墜鎸囧鐨勬í鍧愭爣鍜岀旱鍧愭爣
        int x = (int) event.getX();
        int y = (int) event.getY();
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                lastX = x;
                lastY = y;
                startx = event.getRawX();
                starty = event.getRawY();

                break;
            case MotionEvent.ACTION_MOVE:

                //璁＄畻绉诲姩鐨勮窛绂�
                int offX = x - lastX;
                int offY = y - lastY;

                int left = getLeft() + offX;
                int top = getTop() + offY;
                int right = getRight() + offX;
                int bottom = getBottom() + offY;

                if (left <= margin) {
                    left = margin;
                    right = left + width;
                } else if (left >= screenWidth - width - margin) {
                    left = screenWidth - width - margin;
                    right = left + width;
                } else {
                    totalX += offX;
                }

                if (top <= margin) {
                    top = margin;
                    bottom = top + width;
                } else if (top >= screenHight - width - marginBottom) {
                    top = screenHight - width - marginBottom;
                    bottom = top + width;
                } else {
                    totalY += offY;
                }

                //璋冪敤layout鏂规硶鏉ラ噸鏂版斁缃畠鐨勪綅缃�
                layout(left, top, right, bottom);

                remove();
                break;
            case MotionEvent.ACTION_UP:

                double dis = Math.sqrt((Math.pow((startx - event.getRawX()), 2) + Math.pow((starty - event.getRawY()), 2)));
                if (dis < 25) {
                    if (listener != null) {
                        listener.onClick(this);
                    }
//                    showButton();
                }
//                updatePosition();
//                autoMove();
                break;
        }
        return true;
    }

    private void updatePosition() {
        layoutParams.x = (int) startx - screenWidth / 2 - (int) lastX + 55 / 2;
        layoutParams.y = (int) starty - screenHight / 2 - (int) lastY + 55 / 2
                - 20 / 2;
        LogUtil.i(TAG, "==== params x - y : " + layoutParams.x + " - "
                + layoutParams.y);
        wm.updateViewLayout(this, layoutParams);
    }

    private void autoMove() {
        while (true) {
            if (layoutParams.x <= 0 && layoutParams.x > -screenWidth / 2 + 5) {
                layoutParams.x = layoutParams.x - 5;
                wm.updateViewLayout(this, layoutParams);
            } else if (layoutParams.x > 0 && layoutParams.x < screenWidth / 2 - 5) {
                layoutParams.x = layoutParams.x + 5;
                wm.updateViewLayout(this, layoutParams);
            } else {
                break;
            }
        }
    }

    private OnClickListener listener;

    @Override
    public void setOnClickListener(OnClickListener l) {
        listener = l;
    }

    private void showButton() {
        addBackBut();
    }

    AnimButtons menu;

    public void addBackBut() {
//		  FrameLayout.LayoutParams backParams = new FrameLayout.LayoutParams
//	                (FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT);
//	        backParams.bottomMargin = 60;
//	        backParams.rightMargin = 45;
//	        //设置底部
//	        backParams.gravity= Gravity.BOTTOM|Gravity.RIGHT;
//		  
//		    menu = new AnimButtons(mContext);
//		    menu.onFinishInflate();
//		    myActivity.addContentView(menu,backParams);
    }

    public void setLocation() {
        int left = getLeft() + totalX;
        int top = getTop() + totalY;
        int right = getRight() + totalX;
        int bottom = getBottom() + totalY;
        layout(left, top, right, bottom);
    }

    public void resetLocation() {
        totalX = 0;
        totalY = 0;
    }

    public void remove() {
        if (menu != null) {
            ((ViewGroup) menu.getParent()).removeView(menu);
        }
    }
}
