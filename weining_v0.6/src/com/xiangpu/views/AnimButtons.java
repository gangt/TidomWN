package com.xiangpu.views;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.RelativeLayout;

import com.konecty.rocket.chat.R;

public class AnimButtons extends RelativeLayout {

		private Context context;
		private int leftMargin = 0, bottomMargin = 0;
		private final int buttonWidth = 58;// 图片宽高
		private final int r = 180;// 半径
		private final int maxTimeSpent = 200;// 最长动画耗时
		private final int minTimeSpent = 80;// 最短动画耗时
		private int intervalTimeSpent;// 每相邻2个的时间间隔
		private Button[] btns;
		private Button btn_menu;
		private LayoutParams params;
		private boolean isOpen = false;// 是否菜单打开状态
		private float angle;// 每个按钮之间的夹角

		public int bottomMargins = this.getMeasuredHeight() - buttonWidth
				- bottomMargin;

		public AnimButtons(Context context) {
			super(context);
			this.context = context;
			
		     WindowManager wm = (WindowManager) getContext()
		                .getSystemService(Context.WINDOW_SERVICE);

		     screenWidth = wm.getDefaultDisplay().getWidth();
		     screenHight = wm.getDefaultDisplay().getHeight();
		        
		}

		public AnimButtons(Context context, AttributeSet attrs) {
			super(context, attrs);
			this.context = context;
		}

		@Override
		public void onFinishInflate() {
			super.onFinishInflate();
			View view = LayoutInflater.from(context).inflate(R.layout.anim_menu_button,this);

			initButtons(view);

		}

		private void initButtons(View view) {
			// 可以根据按钮的个数自己增减
			btns = new Button[4];
			btns[0] = (Button) view.findViewById(R.id.btn_camera);
			btns[1] = (Button) view.findViewById(R.id.btn_with);
			btns[2] = (Button) view.findViewById(R.id.btn_place);
			btns[3] = (Button) view.findViewById(R.id.btn_music);
			// btns[4] = (Button) view.findViewById(R.id.btn_thought);
			// btns[5] = (Button) view.findViewById(R.id.btn_sleep);
			btn_menu = (Button) view.findViewById(R.id.btn_menu);

			leftMargin = ((LayoutParams) (btn_menu.getLayoutParams())).leftMargin;
			bottomMargin = ((LayoutParams) (btn_menu
					.getLayoutParams())).bottomMargin;

			for (int i = 0; i < btns.length; i++) {
				// 初始化的时候按钮重合
				btns[i].setLayoutParams(btn_menu.getLayoutParams());
				btns[i].setTag(String.valueOf(i));
				btns[i].setOnClickListener(clickListener);
			}

			intervalTimeSpent = (maxTimeSpent - minTimeSpent) / btns.length;
			angle = (float) Math.PI / (2 * (btns.length - 1));
		}

		@Override
		protected void onSizeChanged(int w, int h, int oldw, int oldh) {
			super.onSizeChanged(w, h, oldw, oldh);
			bottomMargins = this.getMeasuredHeight() - buttonWidth - bottomMargin;
			btn_menu.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					if (!isOpen) {
						openMenu();
					} else {
						closeMenu();
					}
				}
			});

		}

		public void closeMenu() {
			if (isOpen == true) {
				isOpen = false;
				for (int i = 0; i < btns.length; i++) {
					float xLenth = (float) (r * Math.sin(i * angle));
					float yLenth = (float) (r * Math.cos(i * angle));
					btns[i].startAnimation(animTranslate(-xLenth, yLenth,
							leftMargin, bottomMargins, btns[i], maxTimeSpent - i
									* intervalTimeSpent));
					btns[i].setVisibility(View.INVISIBLE);
				}
			}
		}

		public void openMenu() {
			isOpen = true;
			for (int i = 0; i < btns.length; i++) {
				float xLenth = (float) (r * Math.sin(i * angle));
				float yLenth = (float) (r * Math.cos(i * angle));
				btns[i].startAnimation(animTranslate(xLenth, -yLenth, leftMargin
						+ (int) xLenth, bottomMargins - (int) yLenth, btns[i],
						minTimeSpent + i * intervalTimeSpent));
				btns[i].setVisibility(View.VISIBLE);
			}

		}

		private Animation animScale(float toX, float toY) {
			Animation animation = new ScaleAnimation(1.0f, toX, 1.0f, toY,
					Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
					0.5f);
			animation.setInterpolator(context,R.anim.accelerate_decelerate_interpolator);
			animation.setDuration(400);
			animation.setFillAfter(false);
			return animation;

		}

		private Animation animTranslate(float toX, float toY, final int lastX,
				final int lastY, final Button button, long durationMillis) {
			Animation animation = new TranslateAnimation(0, toX, 0, toY);
			animation.setAnimationListener(new AnimationListener() {

				@Override
				public void onAnimationStart(Animation animation) {

				}

				@Override
				public void onAnimationRepeat(Animation animation) {

				}

				@Override
				public void onAnimationEnd(Animation animation) {
					params = new LayoutParams(0, 0);
					params.height = buttonWidth;
					params.width = buttonWidth;
					params.setMargins(lastX, lastY, 0, 0);
					button.setLayoutParams(params);
					button.clearAnimation();

				}
			});
			animation.setDuration(durationMillis);
			return animation;
		}

		OnClickListener clickListener = new OnClickListener() {

			@Override
			public void onClick(View v) {
				int selectedItem = Integer.parseInt((String) v.getTag());
				for (int i = 0; i < btns.length; i++) {
					if (i == selectedItem) {
						btns[i].startAnimation(animScale(2.0f, 2.0f));
					} else {
						btns[i].startAnimation(animScale(0.0f, 0.0f));
					}
				}
				if (onButtonClickListener != null) {
					onButtonClickListener.onButtonClick(v, selectedItem);
				}
			}

		};

		public boolean isOpen() {
			return isOpen;
		}

		private OnButtonClickListener onButtonClickListener;

		public interface OnButtonClickListener {
			void onButtonClick(View v, int id);
		}

		public void setOnButtonClickListener(
				OnButtonClickListener onButtonClickListener) {
			this.onButtonClickListener = onButtonClickListener;
		}


		   private int lastX;
		    private int lastY;
		    private int screenWidth;
		    private int screenHight;
		    private int width;
		    private float startx;
		    private float starty;

		    private static int margin = 15;
		    private static int marginBottom = 60;
		    
		 public boolean onTouchEvent(MotionEvent event) {
		        if(width <= 0){
		            width = getWidth();
		        }

		        //鑾峰彇鍒版墜鎸囧鐨勬í鍧愭爣鍜岀旱鍧愭爣
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

		                //璁＄畻绉诲姩鐨勮窛绂�
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

		                //璋冪敤layout鏂规硶鏉ラ噸鏂版斁缃畠鐨勪綅缃�
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