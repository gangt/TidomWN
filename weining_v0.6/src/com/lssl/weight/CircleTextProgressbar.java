/*
 * Copyright 2016 Yan Zhenjie
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.lssl.weight;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Build;
import android.support.annotation.ColorInt;
import android.util.AttributeSet;
import android.widget.TextView;

import com.konecty.rocket.chat.R;
import com.lssl.AndroidUtilities;
import com.lssl.entity.DonutProgressInfo;

/**
 * Created on 2016/7/12.
 *
 * @author Yan Zhenjie.
 */
public class CircleTextProgressbar extends TextView {

    /**
     * 外部轮廓的颜色。
     */
    private int outLineColor = Color.BLACK;

    /**
     * 外部轮廓的宽度。
     */
    private int outLineWidth = 2;

    /**
     * 内部圆的颜色。
     */
    private ColorStateList inCircleColors = ColorStateList.valueOf(Color.TRANSPARENT);

    /**
     * 中心圆的颜色。
     */
    private int circleColor;

    /**
     * 进度条的颜色。
     */
    private int progressLineColor = Color.BLUE;

    /**
     * 进度条的宽度。
     */
    private int progressLineWidth = 8;

    /**
     * 画笔。
     */
    private Paint mPaint = new Paint();

    /**
     * 进度条的矩形区域。
     */
    private RectF mArcRect = new RectF();

    /**
     * 进度。
     */
    private int progress = 0;

    /**
     * 进度。
     */
    private int currentProgress = 0;

    public void setCurrentProgress(int currentProgress) {
        this.currentProgress = currentProgress;
    }

    /**
     * 进度条类型。
     */
    private ProgressType mProgressType = ProgressType.COUNT_BACK;
    /**
     * 进度倒计时时间。
     */
    private long timeMillis = 500;

    /**
     * View的显示区域。
     */
    final Rect bounds = new Rect();
    /**
     * 进度条通知。
     */
    private OnCountdownProgressListener mCountdownProgressListener;
    /**
     * Listener what。
     */
    private int listenerWhat = 0;

    public Bitmap getBitmap() {
        return bitmap;
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
        postInvalidate();
    }

    Bitmap bitmap = null;

    public void setInfo(DonutProgressInfo info) {
        topStr = info.topStr;
        midStr = info.midStr;
        bottomStr = info.bottomStr;

        currentProgress = info.progress;

        invalidate();
        reStart();
    }

    private String topStr = "交易：278.9万";
    private String midStr = "完成率：75%";
    private String bottomStr = "订单：1328个";
    private AndroidUtilities androidUtilities;

    public CircleTextProgressbar(Context context) {
        this(context, null);
    }

    public CircleTextProgressbar(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CircleTextProgressbar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initialize(context, attrs);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public CircleTextProgressbar(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initialize(context, attrs);
    }

    /**
     * 初始化。androidUtilities
     *
     * @param context      上下文。
     * @param attributeSet 属性。
     */
    private void initialize(Context context, AttributeSet attributeSet) {
        mPaint.setAntiAlias(true);

        androidUtilities = new AndroidUtilities(context);

        TypedArray typedArray = context.obtainStyledAttributes(attributeSet, R.styleable.circleTextProgressbar);
        if (typedArray.hasValue(R.styleable.circleTextProgressbar_in_circle_color))
            inCircleColors = typedArray.getColorStateList(R.styleable.circleTextProgressbar_in_circle_color);
        else
            inCircleColors = ColorStateList.valueOf(Color.TRANSPARENT);
        circleColor = inCircleColors.getColorForState(getDrawableState(), Color.TRANSPARENT);
        typedArray.recycle();
    }

    /**
     * 设置外部轮廓的颜色。
     *
     * @param outLineColor 颜色值。
     */
    public void setOutLineColor(@ColorInt int outLineColor) {
        this.outLineColor = outLineColor;
        invalidate();
    }

    /**
     * 设置外部轮廓的颜色。
     *
     * @param outLineWidth 颜色值。
     */
    public void setOutLineWidth(@ColorInt int outLineWidth) {
        this.outLineWidth = outLineWidth;
        invalidate();
    }

    /**
     * 设置圆形的填充颜色。
     *
     * @param inCircleColor 颜色值。
     */
    public void setInCircleColor(@ColorInt int inCircleColor) {
        this.inCircleColors = ColorStateList.valueOf(inCircleColor);
        invalidate();
    }

    /**
     * 是否需要更新圆的颜色。
     */
    private void validateCircleColor() {
        int circleColorTemp = inCircleColors.getColorForState(getDrawableState(), Color.TRANSPARENT);
        if (circleColor != circleColorTemp) {
            circleColor = circleColorTemp;
            invalidate();
        }
    }

    /**
     * 设置进度条颜色。
     *
     * @param progressLineColor 颜色值。
     */
    public void setProgressColor(@ColorInt int progressLineColor) {
        this.progressLineColor = progressLineColor;
        invalidate();
    }

    /**
     * 设置进度条线的宽度。
     *
     * @param progressLineWidth 宽度值。
     */
    public void setProgressLineWidth(int progressLineWidth) {
        this.progressLineWidth = progressLineWidth;
        invalidate();
    }

    /**
     * 设置进度。
     *
     * @param progress 进度。
     */
    public void setProgress(int progress) {
        this.progress = validateProgress(progress);
        invalidate();
    }

    /**
     * 验证进度。
     *
     * @param progress 你要验证的进度值。
     * @return 返回真正的进度值。
     */
    private int validateProgress(int progress) {
        if (progress > 100)
            progress = 100;
        else if (progress < 0)
            progress = 0;
        return progress;
    }

    /**
     * 拿到此时的进度。
     *
     * @return 进度值，最大100，最小0。
     */
    public int getProgress() {
        return progress;
    }

    /**
     * 设置倒计时总时间。
     *
     * @param timeMillis 毫秒。
     */
    public void setTimeMillis(long timeMillis) {
        this.timeMillis = timeMillis;
        invalidate();
    }

    /**
     * 拿到进度条计时时间。
     *
     * @return 毫秒。
     */
    public long getTimeMillis() {
        return this.timeMillis;
    }

    /**
     * 设置进度条类型。
     *
     * @param progressType {@link ProgressType}.
     */
    public void setProgressType(ProgressType progressType) {
        this.mProgressType = progressType;
        resetProgress();
        invalidate();
    }

    /**
     * 重置进度。
     */
    private void resetProgress() {
        switch (mProgressType) {
            case COUNT:
                progress = 0;
                break;
            case COUNT_BACK:
                progress = 100;
                break;
        }
    }

    /**
     * 拿到进度条类型。
     *
     * @return
     */
    public ProgressType getProgressType() {
        return mProgressType;
    }

    /**
     * 设置进度监听。
     *
     * @param mCountdownProgressListener 监听器。
     */
    public void setCountdownProgressListener(int what, OnCountdownProgressListener mCountdownProgressListener) {
        this.listenerWhat = what;
        this.mCountdownProgressListener = mCountdownProgressListener;
    }

    /**
     * 开始。
     */
    public void start() {
        stop();
        post(progressChangeTask);
    }

    /**
     * 重新开始。
     */
    public void reStart() {
        resetProgress();
        start();
    }

    /**
     * 停止。
     */
    public void stop() {
        removeCallbacks(progressChangeTask);
    }

    private Paint textPaint;
    private Rect textBounds = new Rect();

    @Override
    protected void onDraw(Canvas canvas) {
        //获取view的边界
        getDrawingRect(bounds);

        int size = bounds.height() > bounds.width() ? bounds.width() : bounds.height();
        float outerRadius = size / 2;

//        // 画内部背景
//        int circleColor = inCircleColors.getColorForState(getDrawableState(), 0);
//        circleColor = Color.parseColor("#A2A8A8");
//        mPaint.setStyle(Paint.Style.FILL);
//        mPaint.setColor(circleColor);
//        canvas.drawCircle(bounds.centerX(), bounds.centerY(), outerRadius - outLineWidth, mPaint);
//
//        int color = Color.parseColor("#29323B");
//        mPaint.setStyle(Paint.Style.FILL);
//        mPaint.setColor(color);
//        canvas.drawCircle(bounds.centerX(), bounds.centerY(), outerRadius - progressLineWidth, mPaint);
//
//        //画边框圆
//        mPaint.setStyle(Paint.Style.STROKE);
//        mPaint.setStrokeWidth(outLineWidth);
//        mPaint.setColor(outLineColor);
//        canvas.drawCircle(bounds.centerX(), bounds.centerY(), outerRadius - outLineWidth / 2, mPaint);
//
//        //画字
//        Paint paint = getPaint();
//        paint.setColor(getCurrentTextColor());
//        paint.setAntiAlias(true);
//        paint.setTextAlign(Paint.Align.CENTER);
//        float textY = bounds.centerY() - (paint.descent() + paint.ascent()) / 2;
//        canvas.drawText(getText().toString(), bounds.centerX(), textY, paint);

        textPaint = new Paint();
        textPaint.setColor(Color.WHITE);
        textPaint.setAntiAlias(true);

        float titleSize = androidUtilities.toSp(14);
        float marginSize = androidUtilities.toSp(16);

        textPaint.setTextSize(titleSize);
        String text1 = topStr == null ? "暂无数据" : topStr;
        float w1 = textPaint.measureText(text1);
        textPaint.getTextBounds("1", 0, 1, textBounds);
        float h1 = textBounds.height();

        String text2 = midStr == null ? "暂无数据" : midStr;
        float w2 = textPaint.measureText(text2);

        textPaint.setTextSize(titleSize);

        textPaint.setTextSize(titleSize);
        canvas.drawText(text1, bounds.centerX() - (w1) / 2, bounds.centerY() + h1, textPaint);
        canvas.drawText(text2, bounds.centerX() - (w2) / 2, bounds.centerY() + h1 * 2 + marginSize, textPaint);

        textPaint.setTextSize(titleSize);

        String text3 = bottomStr == null ? "暂无数据" : bottomStr;
        textPaint.setTextSize(titleSize);
        float w3 = textPaint.measureText(text3);
        textPaint.getTextBounds("1", 0, 1, textBounds);
        float h3 = textBounds.height();

        canvas.drawText(text3, bounds.centerX() - w3 / 2, bounds.centerY() + h1 * 2 + h3 + marginSize * 2, textPaint);

//        // 画图标
//        if (bitmap == null) {
//            bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.lssl_home_logo);
//        }
//        Bitmap centerBitmap = BitmapUtils.zoomImg(bitmap, DensityUtil.dip2px(getContext(), (float) bitmap.getWidth() / 3), DensityUtil.dip2px(getContext(), (float) bitmap.getHeight() / 3));
//        int iconWidth = centerBitmap.getWidth();
//        int iconHight = centerBitmap.getHeight();
//        // 绘图
//        canvas.drawBitmap(centerBitmap, bounds.centerX() - (iconWidth) / 2, (bounds.centerY() - iconHight) / 2 + DensityUtil.dip2px(getContext(), 5), textPaint);

//        //画进度条
//        mPaint.setColor(progressLineColor);
//        mPaint.setStyle(Paint.Style.STROKE);
//        mPaint.setStrokeWidth(progressLineWidth);
//        mPaint.setStrokeCap(Paint.Cap.ROUND);
//        int deleteWidth = progressLineWidth + outLineWidth;
//        mArcRect.set(bounds.left + deleteWidth / 2, bounds.top + deleteWidth / 2, bounds.right - deleteWidth / 2, bounds.bottom - deleteWidth / 2);
//
//        canvas.drawArc(mArcRect, -90, 360 * progress / 100, false, mPaint);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
//        int lineWidth = 4 * (outLineWidth + progressLineWidth);
//        int width = getMeasuredWidth();
//        int height = getMeasuredHeight();
//        int size = (width > height ? width : height) + lineWidth;
//        setMeasuredDimension(size, size);
    }

    @Override
    protected void drawableStateChanged() {
        super.drawableStateChanged();
        validateCircleColor();
    }

    /**
     * 进度更新task。
     */
    private Runnable progressChangeTask = new Runnable() {
        @Override
        public void run() {
            removeCallbacks(this);
            if (currentProgress <= progress) return;

            switch (mProgressType) {
                case COUNT:
                    progress += 1;
                    break;
                case COUNT_BACK:
                    progress -= 1;
                    break;
            }
            if (progress >= 0 && progress <= 100) {
                if (mCountdownProgressListener != null)
                    mCountdownProgressListener.onProgress(listenerWhat, progress);
                invalidate();
                postDelayed(progressChangeTask, timeMillis / 100);
            } else {
                progress = validateProgress(progress);
            }
        }
    };

    /**
     * 进度条类型。
     */
    public enum ProgressType {
        /**
         * 顺数进度条，从0-100；
         */
        COUNT,

        /**
         * 倒数进度条，从100-0；
         */
        COUNT_BACK;
    }

    /**
     * 进度监听。
     */
    public interface OnCountdownProgressListener {

        /**
         * 进度通知。
         *
         * @param progress 进度值。
         */
        void onProgress(int what, int progress);
    }

}
