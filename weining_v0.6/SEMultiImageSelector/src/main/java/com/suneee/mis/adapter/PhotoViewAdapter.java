package com.suneee.mis.adapter;

import java.util.List;

import uk.co.senab.photoview.PhotoView;
import uk.co.senab.photoview.PhotoViewAttacher.OnViewTapListener;
import android.content.Context;
import android.os.Handler;
import android.support.v4.view.PagerAdapter;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.suneee.mis.R;

public class PhotoViewAdapter extends PagerAdapter {

	private List<String> picUrls;
	private Context context;
	private DisplayImageOptions options;

	public PhotoViewAdapter(Context contexts, List<String> picUrl) {
		this.context = contexts;
		this.picUrls = picUrl;
		options = new DisplayImageOptions.Builder().showImageForEmptyUri(R.drawable.mis_skin_icon_img_load_fail)
				.showImageOnFail(R.drawable.mis_skin_icon_img_load_fail).resetViewBeforeLoading(false) // default
				.cacheInMemory(true).cacheOnDisc(true).handler(new Handler()).build();
	}

	@Override
	public int getCount() {
		return null == picUrls ? 0 : picUrls.size();
	}

	@Override
	public boolean isViewFromObject(View view, Object object) {
		return view == object;
	}

	@Override
	public View instantiateItem(ViewGroup container, final int position) {
		if (null == picUrls) {
			return null;
		}
		PhotoView photoView = new PhotoView(container.getContext());
		String filepath = picUrls.get(position);
		if (!TextUtils.isEmpty(filepath)) {
			String imageUrl = "";
			if (filepath.contains("http://")) {
				imageUrl = filepath;
			} else if (filepath.contains("drawable://")) {
				filepath = filepath.substring(11);
				imageUrl = "drawable://" + context.getResources().getIdentifier(filepath, "drawable", context.getPackageName());
			} else {
				imageUrl = "file://" + filepath;
			}
			ImageLoader.getInstance().displayImage(imageUrl, photoView, options);

		} else {
			photoView.setImageResource(R.drawable.mis_skin_icon_img_load_fail);
		}

		container.addView(photoView, LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT);

		photoView.setOnViewTapListener(new OnViewTapListener() {

			@Override
			public void onViewTap(View view, float x, float y) {
				if (null != onItemClickListener) {
					onItemClickListener.onClick(view);
				}
			}
		});
		
		photoView.setOnLongClickListener(new OnLongClickListener(){

			@Override
			public boolean onLongClick(View view) {
				if (null != onItemClickListener) {
					onItemClickListener.onLongClick(view, picUrls.get(position));;
				}
				return true;
			}
			
		});
		return photoView;
	}

	@Override
	public void destroyItem(ViewGroup container, int position, Object object) {
		container.removeView((View) object);
	}

	private OnItemClickListener onItemClickListener;

	public OnItemClickListener getOnItemClickListener() {
		return onItemClickListener;
	}

	public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
		this.onItemClickListener = onItemClickListener;
	}

	public interface OnItemClickListener {
		public void onClick(View view);
		public void onLongClick(View view, String imgUrl);
	}

}
