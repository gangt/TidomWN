package com.suneee.mis;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MISActivity extends FragmentActivity implements MISFragment.Callback {

	/** 最大图片选择次数，int类型，默认9 */
	public static final String EXTRA_SELECT_COUNT = "max_select_count";
	/** 图片选择模式，默认多选 */
	public static final String EXTRA_SELECT_MODE = "select_count_mode";
	/** 是否显示相机，默认显示 */
	public static final String EXTRA_SHOW_CAMERA = "show_camera";
	/** 选择结果，返回为 ArrayList&lt;String&gt; 图片路径集合 */
	public static final String EXTRA_RESULT = "select_result";
	/** 默认选择集 */
	public static final String EXTRA_DEFAULT_SELECTED_LIST = "default_list";

	public static final String EXTRA_IMAGE_CACHE_DIR = "image_cache_dir";

	public static final String EXTRA_TARGET_ACTIVITY = "target_activity";

	public static final String EXTRA_TITLE_BAR_BG_COLOR = "title_bar_bg_color";

	public static final String EXTRA_TARGET_COMPRESS_WIDTH = "target_format_widht";// 压缩后的宽度

	public static final String EXTRA_TARGET_COMPRESS_HEIGHT = "target_format_height";// 压缩后的高度

	public static final String EXTRA_COMPRESS_PCITURE_SIZE_ABOVE = "compress_picture_size_above";// 图片大于多少时进行压缩

	public static final int MAX_SELECT_DEFAULT_COUNT = 9;

	private boolean sendOriginalPicture = false;

	/** 单选 */
	public static final int MODE_SINGLE = 0;
	/** 多选 */
	public static final int MODE_MULTI = 1;
	public int mDefaultTitleBarBgColor = Color.parseColor("#2c333a");

	private ArrayList<String> resultList = new ArrayList<String>();
	private Button mSubmitButton;
	private int mDefaultCount;
	private String mImageCacheDir;
	private String mTargetActivity;

	private LinearLayout misTitleBarLayout;
	private TextView misBackView;

	private static final int DEFAULT_WIDTH = 540;
	private static final int DEFAULT_HEIGHT = 960;
	private static final long DEFAULT_COMPRESS_PCITURE_SIZE = 1024 * 200;

	private int targetCompressWidth = DEFAULT_WIDTH;
	private int targetCompressHeight = DEFAULT_HEIGHT;
	private long compressPictureSizeAbove = DEFAULT_COMPRESS_PCITURE_SIZE;

	private Map<String, String> mCompressSuccessMap = new HashMap<String, String>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.mis_activity_main);

		Intent intent = getIntent();
		mDefaultCount = intent.getIntExtra(EXTRA_SELECT_COUNT, MAX_SELECT_DEFAULT_COUNT);
		int mode = intent.getIntExtra(EXTRA_SELECT_MODE, MODE_MULTI);
		boolean isShow = intent.getBooleanExtra(EXTRA_SHOW_CAMERA, true);
		if (mode == MODE_MULTI && intent.hasExtra(EXTRA_DEFAULT_SELECTED_LIST)) {
			resultList = intent.getStringArrayListExtra(EXTRA_DEFAULT_SELECTED_LIST);
		}
		mImageCacheDir = intent.getStringExtra(EXTRA_IMAGE_CACHE_DIR);
		mTargetActivity = intent.getStringExtra(EXTRA_TARGET_ACTIVITY);
		mDefaultTitleBarBgColor = intent.getIntExtra(EXTRA_TITLE_BAR_BG_COLOR, mDefaultTitleBarBgColor);
		targetCompressWidth = intent.getIntExtra(EXTRA_TARGET_COMPRESS_WIDTH, DEFAULT_WIDTH);
		targetCompressHeight = intent.getIntExtra(EXTRA_TARGET_COMPRESS_HEIGHT, DEFAULT_HEIGHT);
		compressPictureSizeAbove = intent.getLongExtra(EXTRA_COMPRESS_PCITURE_SIZE_ABOVE, DEFAULT_COMPRESS_PCITURE_SIZE);

		Bundle bundle = new Bundle();
		bundle.putInt(MISFragment.EXTRA_SELECT_COUNT, mDefaultCount);
		bundle.putInt(MISFragment.EXTRA_SELECT_MODE, mode);
		bundle.putBoolean(MISFragment.EXTRA_SHOW_CAMERA, isShow);
		bundle.putStringArrayList(MISFragment.EXTRA_DEFAULT_SELECTED_LIST, resultList);
		bundle.putString(MISFragment.EXTRA_IMAGE_CACHE_DIR, mImageCacheDir);
		bundle.putInt(MISFragment.EXTRA_TARGET_COMPRESS_WIDTH, targetCompressWidth);
		bundle.putInt(MISFragment.EXTRA_TARGET_COMPRESS_HEIGHT, targetCompressHeight);
		bundle.putLong(MISFragment.EXTRA_COMPRESS_PCITURE_SIZE_ABOVE, compressPictureSizeAbove);

		getSupportFragmentManager().beginTransaction().add(R.id.images_grid, Fragment.instantiate(this, MISFragment.class.getName(), bundle)).commit();

		misTitleBarLayout = (LinearLayout) findViewById(R.id.mis_title_bar_layout);
		misTitleBarLayout.setBackgroundColor(mDefaultTitleBarBgColor);

		// 返回按钮
		misBackView = (TextView) findViewById(R.id.mis_back_view);
		misBackView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				// 清理临时缓存
				clearCompressPicture();
				setResult(RESULT_CANCELED);
				finish();
			}
		});

		mSubmitButton = (Button) findViewById(R.id.mis_done);
		if (resultList == null || resultList.size() <= 0) {
			mSubmitButton.setText("完成");
			mSubmitButton.setEnabled(false);
		} else {
			mSubmitButton.setText("完成(" + resultList.size() + "/" + mDefaultCount + ")");
			mSubmitButton.setEnabled(true);
		}
		mSubmitButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				if (resultList != null && resultList.size() > 0) {
					done();
				}
			}
		});
	}

	@Override
	public void onSingleImageSelected(String path) {
		resultList.add(path);
		done();
	}

	@Override
	public void onImageSelected(String path) {
		if (!resultList.contains(path)) {
			resultList.add(path);
		}
		// 有图片之后，改变按钮状态
		if (resultList.size() > 0) {
			mSubmitButton.setText("完成(" + resultList.size() + "/" + mDefaultCount + ")");
			if (!mSubmitButton.isEnabled()) {
				mSubmitButton.setEnabled(true);
			}
		}
	}

	@Override
	public void onImageUnselected(String path) {
		if (resultList.contains(path)) {
			resultList.remove(path);
			mSubmitButton.setText("完成(" + resultList.size() + "/" + mDefaultCount + ")");
		} else {
			mSubmitButton.setText("完成(" + resultList.size() + "/" + mDefaultCount + ")");
		}
		// 当为选择图片时候的状态
		if (resultList.size() == 0) {
			mSubmitButton.setText("完成");
			mSubmitButton.setEnabled(false);
		}
	}

	@Override
	public void onSendOriginalPciture(boolean sendOriginalPicture) {
		this.sendOriginalPicture = sendOriginalPicture;
	}

	@Override
	public void onCompressFinished(Map<String, String> map) {
		mCompressSuccessMap.putAll(map);
	}

	@Override
	public void onCameraShot(File imageFile) {
		if (imageFile != null) {
			resultList.add(imageFile.getAbsolutePath());
			done();
			// Intent photoViewIntent = new Intent(MISActivity.this,
			// PhotoViewActivity.class);
			// photoViewIntent.putStringArrayListExtra(PhotoViewActivity.EXTRA_PIC_URLS,
			// resultList);
			// photoViewIntent.putExtra(PhotoViewActivity.EXTRA_IMAGE_CACHE_DIR,
			// mImageCacheDir);
			// photoViewIntent.putExtra(PhotoViewActivity.EXTRA_VIEW_MODEL,
			// false);
			// startActivity(photoViewIntent);
		}
	}

	private void done() {
		ArrayList<String> returnList = new ArrayList<String>();
		System.out.println("~~~~  mCompressSuccessMap.size=" + mCompressSuccessMap.size() + ", sendOriginalPicture=" + sendOriginalPicture);
		if (sendOriginalPicture) {
			returnList.addAll(resultList);
		} else {
			for (String originalPictureUrl : resultList) {
				if (mCompressSuccessMap.containsKey(originalPictureUrl)) {
					returnList.add(mCompressSuccessMap.get(originalPictureUrl));
				} else {
					returnList.add(originalPictureUrl);
				}
			}
			mCompressSuccessMap.clear();
		}
		if (!TextUtils.isEmpty(mTargetActivity)) {
			try {
				Intent goTargetActivity = new Intent();
				goTargetActivity.setClassName(getPackageName(), mTargetActivity);
				goTargetActivity.putStringArrayListExtra(EXTRA_RESULT, returnList);
				startActivity(goTargetActivity);
				finish();
				return;
			} catch (Exception e) {
			}
		}

		Intent data = new Intent();
		data.putStringArrayListExtra(EXTRA_RESULT, returnList);
		setResult(RESULT_OK, data);
		finish();
	}

	public void clearCompressPicture() {
		// 清理处理过的临时文件
		if (null != mCompressSuccessMap && mCompressSuccessMap.size() > 0) {
			for (String key : mCompressSuccessMap.keySet()) {
				String url = mCompressSuccessMap.get(key);
				File file = new File(url);
				if (file.exists()) {
					file.delete();
				}
			}
		}
	}

	@Override
	public boolean onKeyUp(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			clearCompressPicture();
		}
		return super.onKeyUp(keyCode, event);
	}

}
