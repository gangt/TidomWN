package com.suneee.mis.view;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * try cache java.lang.IllegalArgumentException: pointerIndex out of range
 * 
 * @author chenyafei617
 * 
 */
public class PhotoViewPager extends ViewPager {

	public PhotoViewPager(Context context) {
		super(context);
	}

	public PhotoViewPager(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {
		try {
			return super.onInterceptTouchEvent(ev);
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
			return false;
		}
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		return super.onTouchEvent(event);
	}

}
