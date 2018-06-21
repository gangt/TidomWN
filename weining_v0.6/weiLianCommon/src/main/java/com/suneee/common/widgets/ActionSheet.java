package com.suneee.common.widgets;

import java.util.Arrays;
import java.util.List;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.StateListDrawable;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.suneee.common.R;

public class ActionSheet extends Dialog implements OnClickListener {

	private static final int CANCEL_BUTTON_ID = 100;
	private static final int TRANSLATE_DURATION = 300;

	private Context mContext;
	private DisplayMetrics displayMetrics;
	private Attributes mAttrs;
	private MenuItemClickListener mListener;
	private View mView;
	private LinearLayout mPanel;
	private List<String> items;
	private String cancelTitle = "";
	private String titleText = "";
	private boolean mDismissed = true;
	private boolean titleVisibility=false;
	private boolean cancelVisibility=false;

	public ActionSheet(Context context) {
		super(context, R.style.ActionSheet);
		this.mContext = context;
		displayMetrics = mContext.getResources().getDisplayMetrics();
		initViews();
		getWindow().setGravity(Gravity.BOTTOM);
	}

	public void initViews() {
		InputMethodManager imm = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
		if (imm.isActive()) {
			View focusView = ((Activity) mContext).getCurrentFocus();
			if (focusView != null)
				imm.hideSoftInputFromWindow(focusView.getWindowToken(), 0);
		}
		mAttrs = readAttribute();
		mView = createView();
		mPanel.startAnimation(createTranslationInAnimation());
	}

	private Animation createTranslationInAnimation() {
		int type = TranslateAnimation.RELATIVE_TO_SELF;
		TranslateAnimation an = new TranslateAnimation(type, 0, type, 0, type, 1, type, 0);
		an.setDuration(TRANSLATE_DURATION);
		return an;
	}

	private Animation createTranslationOutAnimation() {
		int type = TranslateAnimation.RELATIVE_TO_SELF;
		TranslateAnimation an = new TranslateAnimation(type, 0, type, 0, type, 0, type, 1);
		an.setDuration(TRANSLATE_DURATION);
		an.setFillAfter(true);
		return an;
	}

	private View createView() {
		FrameLayout parent = new FrameLayout(mContext);
		FrameLayout.LayoutParams parentParams = new FrameLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		parentParams.gravity = Gravity.BOTTOM;
		parent.setLayoutParams(parentParams);

		mPanel = new LinearLayout(mContext);
		FrameLayout.LayoutParams mPanelParams = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.WRAP_CONTENT);
		mPanelParams.gravity = Gravity.BOTTOM;
		mPanel.setLayoutParams(mPanelParams);
		mPanel.setOrientation(LinearLayout.VERTICAL);
		parent.addView(mPanel);
		return parent;
	}

	private void createItems() {
		if (mAttrs.titleButtonVisible) {
			Button titleBtn = new Button(mContext);
			titleBtn.setBackgroundDrawable(mAttrs.otherButtonTopBackground);
			titleBtn.setText(titleText);
			titleBtn.setEnabled(false);
			titleBtn.setTextColor(mAttrs.titleButtonTextColor);
			titleBtn.setTextSize(TypedValue.COMPLEX_UNIT_PX, mAttrs.actionSheetTextSize);
			LinearLayout.LayoutParams params = createButtonLayoutParams();
			params.topMargin = mAttrs.otherButtonSpacing;
			if (titleVisibility){
				mPanel.addView(titleBtn, params);
			}
		}
		if (items != null && items.size() > 0) {
			for (int i = 0; i < items.size(); i++) {
				Button bt = new Button(mContext);
				bt.setId(CANCEL_BUTTON_ID + i + 1);
				bt.setOnClickListener(this);
				if (mAttrs.titleButtonVisible) {
					if (items.size() > 1) {// 多于1个
						if (i == (items.size() - 1)) {
							bt.setBackgroundDrawable(mAttrs.otherButtonBottomBackground);
						} else {
							bt.setBackgroundDrawable(mAttrs.otherButtonMiddleBackground.getConstantState().newDrawable());
						}
					} else {
						bt.setBackgroundDrawable(mAttrs.otherButtonBottomBackground);
					}
				} else {
					bt.setBackgroundDrawable(getOtherButtonBg(items.toArray(new String[items.size()]), i));
				}
				bt.setText(items.get(i));
				bt.setTextColor(mAttrs.otherButtonTextColor);
				bt.setTextSize(TypedValue.COMPLEX_UNIT_PX, mAttrs.actionSheetTextSize);
				if (i > 0 && mAttrs.otherButtonSpacing > 0) {
					LinearLayout.LayoutParams params = createButtonLayoutParams();
					params.topMargin = mAttrs.otherButtonSpacing;
					mPanel.addView(bt, params);
				} else {
					mPanel.addView(bt);
				}
			}
		}

		Button cancelBtn = new Button(mContext);
		cancelBtn.setTextSize(TypedValue.COMPLEX_UNIT_PX, mAttrs.actionSheetTextSize);
		cancelBtn.setId(CANCEL_BUTTON_ID);
		cancelBtn.setBackgroundDrawable(mAttrs.cancelButtonBackground);
		cancelBtn.setText(cancelTitle);
		cancelBtn.setTextColor(mAttrs.cancelButtonTextColor);
		cancelBtn.setOnClickListener(this);
		LinearLayout.LayoutParams params = createButtonLayoutParams();
		params.topMargin = mAttrs.cancelButtonMarginTop;
		if (cancelVisibility){
			mPanel.addView(cancelBtn, params);
			mPanel.setBackgroundDrawable(mAttrs.background);
			mPanel.setPadding(mAttrs.padding, mAttrs.padding, mAttrs.padding, mAttrs.padding);
		}
	}

	public LinearLayout.LayoutParams createButtonLayoutParams() {
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
		return params;
	}

	private Drawable getOtherButtonBg(String[] titles, int i) {
		if (titles.length == 1)
			return mAttrs.otherButtonSingleBackground;
		else if (titles.length == 2)
			switch (i) {
			case 0:
				return mAttrs.otherButtonTopBackground;
			case 1:
				return mAttrs.otherButtonBottomBackground;
			}
		else if (titles.length > 2) {
			if (i == 0)
				return mAttrs.otherButtonTopBackground;
			else if (i == (titles.length - 1))
				return mAttrs.otherButtonBottomBackground;
			return mAttrs.getOtherButtonMiddleBackground();
		}
		return null;
	}

	public void showMenu() {
		if (!mDismissed)
			return;
		show();
		getWindow().setContentView(mView);

		WindowManager.LayoutParams lp = getWindow().getAttributes();
		lp.width = (int) (displayMetrics.widthPixels); // 设置宽度
		getWindow().setAttributes(lp);
		mDismissed = false;
	}

	public void dismissMenu() {
		if (mDismissed)
			return;
		dismiss();
		onDismiss();
		mDismissed = true;
	}

	private void onDismiss() {
		mPanel.startAnimation(createTranslationOutAnimation());
	}

	public ActionSheet setCancelButtonTitle(String title) {
		this.cancelTitle = title;
		return this;
	}
	public ActionSheet setCancelButtonVisibility(boolean type) {
		this.cancelVisibility = type;
		return this;
	}
	public ActionSheet setTitleButtonVisibility(boolean type) {
		this.titleVisibility = type;
		return this;
	}

	public ActionSheet setCancelButtonTitle(int strId) {
		return setCancelButtonTitle(mContext.getString(strId));
	}

	public ActionSheet setSheetTitle(String title) {
		this.titleText = title;
		return this;
	}

	public ActionSheet setSheetTitle(int strId) {
		return setSheetTitle(mContext.getString(strId));
	}

	public ActionSheet setCancelableOnTouchMenuOutside(boolean cancelable) {
		setCanceledOnTouchOutside(cancelable);
		return this;
	}

	public ActionSheet addItems(String... titles) {
		if (titles == null || titles.length == 0)
			return this;
		items = Arrays.asList(titles);
		createItems();
		return this;
	}

	public ActionSheet setItemClickListener(MenuItemClickListener listener) {
		this.mListener = listener;
		return this;
	}

	private Attributes readAttribute() {
		Attributes attrs = new Attributes(mContext);
		TypedArray a = mContext.getTheme().obtainStyledAttributes(null, R.styleable.ActionSheet, R.attr.actionSheetStyle, 0);
		Drawable background = a.getDrawable(R.styleable.ActionSheet_actionSheetBackground);
		if (background != null) {
			attrs.background = background;
		}

		Drawable cancelButtonBackground = a.getDrawable(R.styleable.ActionSheet_cancelButtonBackground);
		if (cancelButtonBackground != null) {
			attrs.cancelButtonBackground = cancelButtonBackground;
		}

		Drawable otherButtonTopBackground = a.getDrawable(R.styleable.ActionSheet_otherButtonTopBackground);
		if (otherButtonTopBackground != null) {
			attrs.otherButtonTopBackground = otherButtonTopBackground;
		}

		Drawable otherButtonMiddleBackground = a.getDrawable(R.styleable.ActionSheet_otherButtonMiddleBackground);
		if (otherButtonMiddleBackground != null) {
			attrs.otherButtonMiddleBackground = otherButtonMiddleBackground;
		}

		Drawable otherButtonBottomBackground = a.getDrawable(R.styleable.ActionSheet_otherButtonBottomBackground);
		if (otherButtonBottomBackground != null) {
			attrs.otherButtonBottomBackground = otherButtonBottomBackground;
		}

		Drawable otherButtonSingleBackground = a.getDrawable(R.styleable.ActionSheet_otherButtonSingleBackground);
		if (otherButtonSingleBackground != null) {
			attrs.otherButtonSingleBackground = otherButtonSingleBackground;
		}

		attrs.cancelButtonTextColor = a.getColor(R.styleable.ActionSheet_cancelButtonTextColor, attrs.cancelButtonTextColor);
		attrs.otherButtonTextColor = a.getColor(R.styleable.ActionSheet_otherButtonTextColor, attrs.otherButtonTextColor);
		attrs.titleButtonTextColor = a.getColor(R.styleable.ActionSheet_titleButtonTextColor, attrs.titleButtonTextColor);
		attrs.titleButtonVisible = a.getBoolean(R.styleable.ActionSheet_titleButtonVisible, attrs.titleButtonVisible);
		attrs.padding = (int) a.getDimension(R.styleable.ActionSheet_actionSheetPadding, attrs.padding);
		attrs.otherButtonSpacing = (int) a.getDimension(R.styleable.ActionSheet_otherButtonSpacing, attrs.otherButtonSpacing);
		attrs.cancelButtonMarginTop = (int) a.getDimension(R.styleable.ActionSheet_cancelButtonMarginTop, attrs.cancelButtonMarginTop);
		attrs.actionSheetTextSize = a.getDimensionPixelSize(R.styleable.ActionSheet_actionSheetTextSize, (int) attrs.actionSheetTextSize);

		a.recycle();
		return attrs;
	}

	@Override
	public void onClick(View v) {
		dismissMenu();
		if (v.getId() != CANCEL_BUTTON_ID) {
			if (mListener != null) {
				mListener.onItemClick(v.getId() - CANCEL_BUTTON_ID - 1);
			}
		}
	}

	private class Attributes {
		private Context mContext;
		private Drawable background;
		private Drawable cancelButtonBackground;
		private Drawable otherButtonTopBackground;
		private Drawable otherButtonMiddleBackground;
		private Drawable otherButtonBottomBackground;
		private Drawable otherButtonSingleBackground;
		private int cancelButtonTextColor;
		private int otherButtonTextColor;
		private int titleButtonTextColor;
		private int padding;
		private int otherButtonSpacing;
		private int cancelButtonMarginTop;
		private float actionSheetTextSize;
		private boolean titleButtonVisible;

		public Attributes(Context context) {
			mContext = context;
			this.background = new ColorDrawable(Color.TRANSPARENT);
			this.cancelButtonBackground = new ColorDrawable(Color.BLACK);
			ColorDrawable gray = new ColorDrawable(Color.GRAY);
			this.otherButtonTopBackground = gray;
			this.otherButtonMiddleBackground = gray;
			this.otherButtonBottomBackground = gray;
			this.otherButtonSingleBackground = gray;
			this.cancelButtonTextColor = Color.WHITE;
			this.otherButtonTextColor = Color.BLACK;
			this.titleButtonTextColor = Color.BLACK;
			this.padding = dp2px(20);
			this.otherButtonSpacing = dp2px(2);
			this.cancelButtonMarginTop = dp2px(10);
			this.actionSheetTextSize = dp2px(16);
			this.titleButtonVisible = false;
		}

		private int dp2px(int dp) {
			return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, mContext.getResources().getDisplayMetrics());
		}

		public Drawable getOtherButtonMiddleBackground() {
			if (otherButtonMiddleBackground instanceof StateListDrawable) {
				TypedArray a = mContext.getTheme().obtainStyledAttributes(null, R.styleable.ActionSheet, R.attr.actionSheetStyle, 0);
				a.getDrawable(R.styleable.ActionSheet_otherButtonMiddleBackground);
				a.recycle();
			}
			return otherButtonMiddleBackground;
		}

	}

	public static interface MenuItemClickListener {
		void onItemClick(int itemPosition);
	}

}