package com.lssl.weight;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.WindowManager;
import android.widget.ImageView;

/**
 * Created by suneee on 2016/12/26.
 */

public class WebBackImageView extends ImageView {

    private int lastX;
    private int lastY;
    private int screenWidth;
    private int screenHight;
    private int width;
    private float startx;
    private float starty;

    private static int margin = 15;
    private static int marginBottom = 60;

    public WebBackImageView(Context context) {
        super(context);

        WindowManager wm = (WindowManager) getContext()
                .getSystemService(Context.WINDOW_SERVICE);

        screenWidth = wm.getDefaultDisplay().getWidth();
        screenHight = wm.getDefaultDisplay().getHeight();


    }

    public WebBackImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public WebBackImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public boolean onTouchEvent(MotionEvent event) {
        if(width <= 0){
            width = getWidth();
        }

        //获取到手指处的横坐标和纵坐标
        int x = (int) event.getX();
        int y = (int) event.getY();
        switch(event.getAction()){
            case MotionEvent.ACTION_DOWN:
                lastX = x;
                lastY = y;
                startx = event.getRawX();
                starty = event.getRawY();

                break;
            case MotionEvent.ACTION_MOVE:

                //计算移动的距离
                int offX = x - lastX;
                int offY = y - lastY;

                int left = getLeft()+offX;
                int top = getTop()+offY;
                int right = getRight()+offX;
                int bottom = getBottom() + offY;

                if(left<=margin){
                    left = margin;
                    right = left + width;
                }else if(left>= screenWidth-width-margin){
                    left = screenWidth-width-margin;
                    right = left + width;
                }

                if(top<=margin){
                    top = margin;
                    bottom = top + width;
                }else if(top>=screenHight-width-marginBottom){
                    top = screenHight-width-marginBottom;
                    bottom = top + width;
                }

                //调用layout方法来重新放置它的位置
                layout(left, top, right  , bottom);
                break;
            case MotionEvent.ACTION_UP:

                double dis = Math.sqrt((Math.pow((startx-event.getRawX()), 2) + Math.pow((starty-event.getRawY()), 2)));

                if (dis < 25) {
                    if (listener != null) {
                        listener.onClick(this);
                    }
                }
                break;
        }
        return true;
    }

    private OnClickListener listener;

    @Override
    public void setOnClickListener(OnClickListener l) {
        listener = l;
    }
}
