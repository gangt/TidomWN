package com.xiangpu.views;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.GridView;

/**
 * description: $todo$
 * autour: Andy
 * date: 2018/1/29 16:22
 * update: 2018/1/29
 * version: 1.0
 */
public class FullGridView extends GridView {

    public FullGridView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public FullGridView(Context context) {
        super(context);
    }

    public FullGridView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2,
                MeasureSpec.AT_MOST);
        super.onMeasure(widthMeasureSpec, expandSpec);
    }

}
