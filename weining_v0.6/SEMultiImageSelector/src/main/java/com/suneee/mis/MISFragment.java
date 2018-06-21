package com.suneee.mis;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.GridView;
import android.widget.ListPopupWindow;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.CompoundButton.OnCheckedChangeListener;

import com.squareup.picasso.Picasso;
import com.suneee.mis.adapter.FolderAdapter;
import com.suneee.mis.adapter.ImageGridAdapter;
import com.suneee.mis.bean.Folder;
import com.suneee.mis.bean.Image;
import com.suneee.mis.factory.AlbumStorageDirFactory;
import com.suneee.mis.factory.BaseAlbumDirFactory;
import com.suneee.mis.factory.FroyoAlbumDirFactory;
import com.suneee.mis.utils.FileUtils;
import com.suneee.mis.utils.ImageCompress;
import com.suneee.mis.utils.TimeUtils;

public class MISFragment extends Fragment {

	private static final String TAG = "MultiImageSelector";

	/** 最大图片选择次数，int类型 */
	public static final String EXTRA_SELECT_COUNT = "max_select_count";
	/** 图片选择模式，int类型 */
	public static final String EXTRA_SELECT_MODE = "select_count_mode";
	/** 是否显示相机，boolean类型 */
	public static final String EXTRA_SHOW_CAMERA = "show_camera";
	/** 默认选择的数据集 */
	public static final String EXTRA_DEFAULT_SELECTED_LIST = "default_result";
	/** 临时存储文件夹路径 */
	public static final String EXTRA_IMAGE_CACHE_DIR = "image_cache_dir";

	public static final String EXTRA_TARGET_COMPRESS_WIDTH = "target_format_widht";// 压缩后的宽度

	public static final String EXTRA_TARGET_COMPRESS_HEIGHT = "target_format_height";// 压缩后的高度

	public static final String EXTRA_COMPRESS_PCITURE_SIZE_ABOVE = "compress_picture_size_above";// 图片大于多少时进行压缩

	/** 单选 */
	public static final int MODE_SINGLE = 0;
	/** 多选 */
	public static final int MODE_MULTI = 1;
	// 不同loader定义
	private static final int LOADER_ALL = 0;
	private static final int LOADER_CATEGORY = 1;
	// 请求加载系统照相机
	private static final int REQUEST_CAMERA = 100;

	// 结果数据
	private ArrayList<String> resultList = new ArrayList<String>();
	// 文件夹数据
	private ArrayList<Folder> mResultFolder = new ArrayList<Folder>();

	// 图片Grid
	private GridView mGridView;
	private Callback mCallback;

	private ImageGridAdapter mImageAdapter;
	private FolderAdapter mFolderAdapter;

	private ListPopupWindow mFolderPopupWindow;

	// 时间线
	private TextView mTimeLineText;
	// 类别
	private TextView mCategoryText;
	// 预览按钮
	private Button mPreviewBtn;
	// 底部View
	private View mPopupAnchorView;

	private CheckBox misPhotoviewOrginImage;

	private int mDesireImageCount;

	private boolean hasFolderGened = false;

	private File mTmpFile;

	private String mImageCacheDir;

	private static final int DEFAULT_WIDTH = 540;
	private static final int DEFAULT_HEIGHT = 960;
	private static final long DEFAULT_COMPRESS_PCITURE_SIZE = 1024 * 200;

	private int targetCompressWidth = DEFAULT_WIDTH;
	private int targetCompressHeight = DEFAULT_HEIGHT;
	private long compressPictureSizeAbove = DEFAULT_COMPRESS_PCITURE_SIZE;

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		try {
			mCallback = (Callback) activity;
		} catch (ClassCastException e) {
			throw new ClassCastException("The Activity must implement MultiImageSelectorFragment.Callback interface...");
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		return inflater.inflate(R.layout.mis_fragment_multi_image, container, false);
	}

	@Override
	public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.FROYO) {
			mAlbumStorageDirFactory = new FroyoAlbumDirFactory();
		} else {
			mAlbumStorageDirFactory = new BaseAlbumDirFactory();
		}

		// 选择图片数量
		mDesireImageCount = getArguments().getInt(EXTRA_SELECT_COUNT);

		// 图片选择模式
		final int mode = getArguments().getInt(EXTRA_SELECT_MODE);

		// 默认选择
		if (mode == MODE_MULTI) {
			ArrayList<String> tmp = getArguments().getStringArrayList(EXTRA_DEFAULT_SELECTED_LIST);
			if (tmp != null && tmp.size() > 0) {
				resultList = tmp;
			}
		}

		mImageCacheDir = getArguments().getString(EXTRA_IMAGE_CACHE_DIR);
		targetCompressWidth = getArguments().getInt(EXTRA_TARGET_COMPRESS_WIDTH, DEFAULT_WIDTH);
		targetCompressHeight = getArguments().getInt(EXTRA_TARGET_COMPRESS_HEIGHT, DEFAULT_HEIGHT);
		compressPictureSizeAbove = getArguments().getLong(EXTRA_COMPRESS_PCITURE_SIZE_ABOVE, DEFAULT_COMPRESS_PCITURE_SIZE);

		// 是否显示照相机
		final boolean showCamera = getArguments().getBoolean(EXTRA_SHOW_CAMERA, true);
		mImageAdapter = new ImageGridAdapter(getActivity(), showCamera);
		// 是否显示选择指示器
		mImageAdapter.showSelectIndicator(mode == MODE_MULTI);

		// 如果显示了照相机，则创建临时文件
		if (showCamera) {
			mTmpFile = FileUtils.createTmpFile(getActivity(), mImageCacheDir);
		}

		mPopupAnchorView = view.findViewById(R.id.footer);

		mTimeLineText = (TextView) view.findViewById(R.id.timeline_area);
		// 初始化，先隐藏当前timeline
		mTimeLineText.setVisibility(View.GONE);

		mCategoryText = (TextView) view.findViewById(R.id.category_btn);
		// 初始化，加载所有图片
		mCategoryText.setText(R.string.folder_all);
		mCategoryText.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				if (mFolderPopupWindow.isShowing()) {
					mFolderPopupWindow.dismiss();
				} else {
					mFolderPopupWindow.show();
					int index = mFolderAdapter.getSelectIndex();
					index = index == 0 ? index : index - 1;
					mFolderPopupWindow.getListView().setSelection(index);
				}
			}
		});

		mPreviewBtn = (Button) view.findViewById(R.id.preview);
		misPhotoviewOrginImage = (CheckBox) view.findViewById(R.id.mis_photoview_orgin_image);
		misPhotoviewOrginImage.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				showImageSize(isChecked);
				if (null != mCallback) {
					mCallback.onSendOriginalPciture(misPhotoviewOrginImage.isChecked());
				}
			}
		});

		// 初始化，按钮状态初始化
		if (resultList == null || resultList.size() <= 0) {
			mPreviewBtn.setText(R.string.preview);
			mPreviewBtn.setEnabled(false);
		}
		mPreviewBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				// TODO 预览
			}
		});

		mGridView = (GridView) view.findViewById(R.id.grid);
		mGridView.setOnScrollListener(new AbsListView.OnScrollListener() {
			@Override
			public void onScrollStateChanged(AbsListView absListView, int state) {

				final Picasso picasso = Picasso.with(getActivity());
				if (state == SCROLL_STATE_IDLE || state == SCROLL_STATE_TOUCH_SCROLL) {
					picasso.resumeTag(getActivity());
				} else {
					picasso.pauseTag(getActivity());
				}

				if (state == SCROLL_STATE_IDLE) {
					// 停止滑动，日期指示器消失
					mTimeLineText.setVisibility(View.GONE);
				} else if (state == SCROLL_STATE_FLING) {
					mTimeLineText.setVisibility(View.VISIBLE);
				}
			}

			@Override
			public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
				if (mTimeLineText.getVisibility() == View.VISIBLE) {
					int index = firstVisibleItem + 1 == view.getAdapter().getCount() ? view.getAdapter().getCount() - 1 : firstVisibleItem + 1;
					Image image = (Image) view.getAdapter().getItem(index);
					if (image != null) {
						mTimeLineText.setText(TimeUtils.formatPhotoDate(image.path));
					}
				}
			}
		});
		mGridView.setAdapter(mImageAdapter);
		mGridView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
			@Override
			@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
			public void onGlobalLayout() {

				final int width = mGridView.getWidth();
				final int height = mGridView.getHeight();

				final int desireSize = getResources().getDimensionPixelOffset(R.dimen.image_size);
				final int numCount = width / desireSize;
				final int columnSpace = getResources().getDimensionPixelOffset(R.dimen.space_size);
				int columnWidth = (width - columnSpace * (numCount - 1)) / numCount;
				mImageAdapter.setItemSize(columnWidth);

				if (mFolderPopupWindow == null) {
					createPopupFolderList(width, height);
				}

				if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
					mGridView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
				} else {
					mGridView.getViewTreeObserver().removeGlobalOnLayoutListener(this);
				}
			}
		});
		mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
				if (mImageAdapter.isShowCamera()) {
					// 如果显示照相机，则第一个Grid显示为照相机，处理特殊逻辑
					if (i == 0) {
						showCameraAction();
					} else {
						// 正常操作
						Image image = (Image) adapterView.getAdapter().getItem(i);
						if (image.length == 0) {
							Toast.makeText(getActivity(), R.string.msg_no_picture, Toast.LENGTH_SHORT).show();
							return;
						}
						selectImageFromGrid(image, mode);
					}
				} else {
					// 正常操作
					Image image = (Image) adapterView.getAdapter().getItem(i);
					if (image.length == 0) {
						Toast.makeText(getActivity(), R.string.msg_no_picture, Toast.LENGTH_SHORT).show();
						return;
					}
					selectImageFromGrid(image, mode);
				}
			}
		});

		mFolderAdapter = new FolderAdapter(getActivity());
	}

	/**
	 * 创建弹出的ListView
	 */
	private void createPopupFolderList(int width, int height) {
		mFolderPopupWindow = new ListPopupWindow(getActivity());
		mFolderPopupWindow.setAdapter(mFolderAdapter);
		mFolderPopupWindow.setContentWidth(width);
		mFolderPopupWindow.setHeight(height * 5 / 8);
		mFolderPopupWindow.setAnchorView(mPopupAnchorView);
		mFolderPopupWindow.setModal(true);
		mFolderPopupWindow.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
				if (i == 0) {
					getActivity().getSupportLoaderManager().restartLoader(LOADER_ALL, null, mLoaderCallback);
					mCategoryText.setText(R.string.folder_all);
					mImageAdapter.setShowCamera(true);
				} else {
					Folder folder = (Folder) adapterView.getAdapter().getItem(i);
					if (null != folder) {
						Bundle args = new Bundle();
						args.putString("path", folder.path);
						getActivity().getSupportLoaderManager().restartLoader(LOADER_CATEGORY, args, mLoaderCallback);
						mCategoryText.setText(folder.name);
					}
					mImageAdapter.setShowCamera(false);
				}
				mFolderAdapter.setSelectIndex(i);
				mFolderPopupWindow.dismiss();

				// 滑动到最初始位置
				mGridView.smoothScrollToPosition(0);
			}
		});
	}

	@Override
	public void onActivityCreated(@Nullable Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		// 首次加载所有图片
		// new LoadImageTask().execute();
		getActivity().getSupportLoaderManager().initLoader(LOADER_ALL, null, mLoaderCallback);
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		// 相机拍照完成后，返回图片路径
		if (resultCode == Activity.RESULT_OK) {
			if (requestCode == REQUEST_CAMERA) {

				handleCameraPhoto();

				// Bundle bundle = data.getExtras();
				// if (bundle != null) {
				// Bitmap mBitmap = (Bitmap) bundle.get("data");
				// MediaStore.Images.Media.insertImage(getActivity().getContentResolver(),
				// mBitmap, "", "");
				// 这里只做刷新
				// getActivity().getSupportLoaderManager().restartLoader(LOADER_ALL,
				// null, mLoaderCallback);
				// mCategoryText.setText(R.string.folder_all);
				// mImageAdapter.setShowCamera(true);
				// }
				// if (mTmpFile != null) {
				// if (mCallback != null) {
				// mCallback.onCameraShot(mTmpFile);
				// }
				// }
			}
		}
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		Log.d(TAG, "on change");

		final int orientation = newConfig.orientation;

		if (mFolderPopupWindow != null) {
			if (mFolderPopupWindow.isShowing()) {
				mFolderPopupWindow.dismiss();
			}
		}

		mGridView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
			@Override
			@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
			public void onGlobalLayout() {

				final int height = mGridView.getHeight();

				final int desireSize = getResources().getDimensionPixelOffset(R.dimen.image_size);
				Log.d(TAG, "Desire Size = " + desireSize);
				final int numCount = mGridView.getWidth() / desireSize;
				Log.d(TAG, "Grid Size = " + mGridView.getWidth());
				Log.d(TAG, "num count = " + numCount);
				final int columnSpace = getResources().getDimensionPixelOffset(R.dimen.space_size);
				int columnWidth = (mGridView.getWidth() - columnSpace * (numCount - 1)) / numCount;
				mImageAdapter.setItemSize(columnWidth);

				if (mFolderPopupWindow != null) {
					mFolderPopupWindow.setHeight(height * 5 / 8);
				}

				if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
					mGridView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
				} else {
					mGridView.getViewTreeObserver().removeGlobalOnLayoutListener(this);
				}
			}
		});

		super.onConfigurationChanged(newConfig);

	}

	/**
	 * 选择相机
	 */
	private void showCameraAction() {
		// 跳转到系统照相机
		Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		if (cameraIntent.resolveActivity(getActivity().getPackageManager()) != null) {
			// 设置系统相机拍照后的输出路径
			// cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT,
			// Uri.fromFile(mTmpFile));

			// 拍照之后存储到系统相册
			// ContentValues values = new ContentValues(3);
			// values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
			// Uri cameraImagePath =
			// getActivity().getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
			// values);
			// cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, cameraImagePath);
			// startActivityForResult(cameraIntent, REQUEST_CAMERA);

			File f = null;
			try {
				f = setUpPhotoFile();
				mCurrentPhotoPath = f.getAbsolutePath();
				cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(f));
			} catch (IOException e) {
				e.printStackTrace();
				f = null;
				mCurrentPhotoPath = null;
			}
			startActivityForResult(cameraIntent, REQUEST_CAMERA);
		} else {
			Toast.makeText(getActivity(), R.string.msg_no_camera, Toast.LENGTH_SHORT).show();
		}
	}

	/**
	 * 选择图片操作
	 * 
	 * @param image
	 */
	private void selectImageFromGrid(Image image, int mode) {
		if (image != null) {
			// 多选模式
			if (mode == MODE_MULTI) {
				if (resultList.contains(image.path)) {
					resultList.remove(image.path);
					if (resultList.size() != 0) {
						mPreviewBtn.setEnabled(true);
						mPreviewBtn.setText(getResources().getString(R.string.preview) + "(" + resultList.size() + ")");
					} else {
						mPreviewBtn.setEnabled(false);
						mPreviewBtn.setText(R.string.preview);
					}

					showCheckBoxView();

					if (mCallback != null) {
						mCallback.onImageUnselected(image.path);
					}
				} else {
					// 判断选择数量问题
					if (mDesireImageCount == resultList.size()) {
						Toast.makeText(getActivity(), R.string.msg_amount_limit, Toast.LENGTH_SHORT).show();
						return;
					}

					resultList.add(image.path);
					mPreviewBtn.setEnabled(true);
					mPreviewBtn.setText(getResources().getString(R.string.preview) + "(" + resultList.size() + ")");

					showCheckBoxView();
					// 压缩图片
					compress(image.path);

					if (mCallback != null) {
						mCallback.onImageSelected(image.path);
					}
				}
				mImageAdapter.select(image);
			} else if (mode == MODE_SINGLE) {
				// 单选模式
				if (mCallback != null) {
					mCallback.onSingleImageSelected(image.path);
				}
			}
		}
	}

	private LoaderManager.LoaderCallbacks<Cursor> mLoaderCallback = new LoaderManager.LoaderCallbacks<Cursor>() {

		private final String[] IMAGE_PROJECTION = { MediaStore.Images.Media.DATA, MediaStore.Images.Media.DISPLAY_NAME, MediaStore.Images.Media.DATE_ADDED,
				MediaStore.Images.Media._ID };

		@Override
		public Loader<Cursor> onCreateLoader(int id, Bundle args) {
			if (id == LOADER_ALL) {
				CursorLoader cursorLoader = new CursorLoader(getActivity(), MediaStore.Images.Media.EXTERNAL_CONTENT_URI, IMAGE_PROJECTION, null, null,
						IMAGE_PROJECTION[2] + " DESC");
				return cursorLoader;
			} else if (id == LOADER_CATEGORY) {
				CursorLoader cursorLoader = new CursorLoader(getActivity(), MediaStore.Images.Media.EXTERNAL_CONTENT_URI, IMAGE_PROJECTION, IMAGE_PROJECTION[0]
						+ " like '%" + args.getString("path") + "%'", null, IMAGE_PROJECTION[2] + " DESC");
				return cursorLoader;
			}

			return null;
		}

		@Override
		public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
			if (data != null) {
				List<Image> images = new ArrayList<Image>();
				int count = data.getCount();
				if (count > 0) {
					data.moveToFirst();
					do {
						String path = data.getString(data.getColumnIndexOrThrow(IMAGE_PROJECTION[0]));
						String name = data.getString(data.getColumnIndexOrThrow(IMAGE_PROJECTION[1]));
						long dateTime = data.getLong(data.getColumnIndexOrThrow(IMAGE_PROJECTION[2]));

						File imageFile = new File(path);
						Image image = new Image(path, name, dateTime, imageFile.length());
						if (!hasFolderGened) {
							// 获取文件夹名称
							File folderFile = imageFile.getParentFile();
							Folder folder = new Folder();
							folder.name = folderFile.getName();
							folder.path = folderFile.getAbsolutePath();
							folder.cover = image;
							if (!mResultFolder.contains(folder)) {
								List<Image> imageList = new ArrayList<Image>();
								imageList.add(image);
								folder.images = imageList;
								mResultFolder.add(folder);
							} else {
								// 更新
								Folder f = mResultFolder.get(mResultFolder.indexOf(folder));
								f.images.add(image);
							}
						}
						images.add(image);

					} while (data.moveToNext());

					mImageAdapter.setData(images);

					// 设定默认选择
					if (resultList != null && resultList.size() > 0) {
						mImageAdapter.setDefaultSelected(resultList);
					}

					mFolderAdapter.setData(mResultFolder);
					hasFolderGened = true;

				}
			}
		}

		@Override
		public void onLoaderReset(Loader<Cursor> loader) {

		}
	};

	/**
	 * 回调接口
	 */
	public interface Callback {
		public void onSingleImageSelected(String path);

		public void onImageSelected(String path);

		public void onImageUnselected(String path);

		public void onCameraShot(File imageFile);

		public void onSendOriginalPciture(boolean sendOriginalPicture);

		public void onCompressFinished(Map<String, String> map);

	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		if (null != mCompressSuccessMap) {
			mCompressSuccessMap.clear();
			mCompressSuccessMap = null;
		}

	}

	/**
	 * 存储到相册
	 */
	private String mCurrentPhotoPath;

	private static final String JPEG_FILE_PREFIX = "IMG_";
	private static final String JPEG_FILE_SUFFIX = ".jpg";

	private AlbumStorageDirFactory mAlbumStorageDirFactory = null;

	private String getAlbumName() {
		return "";// 可以在这里建分类目录
	}

	private File getAlbumDir() {
		File storageDir = null;

		if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {

			storageDir = mAlbumStorageDirFactory.getAlbumStorageDir(getAlbumName());

			if (storageDir != null) {
				if (!storageDir.mkdirs()) {
					if (!storageDir.exists()) {
						Log.d("MISFragment", "failed to create directory");
						return null;
					}
				}
			}
		} else {
			Log.v("~~~MISFragment~~~", "External storage is not mounted READ/WRITE.");
		}
		return storageDir;
	}

	private File createImageFile() throws IOException {
		// Create an image file name
		String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
		String imageFileName = JPEG_FILE_PREFIX + timeStamp + "_";
		File albumF = getAlbumDir();
		File imageF = File.createTempFile(imageFileName, JPEG_FILE_SUFFIX, albumF);
		return imageF;
	}

	private File setUpPhotoFile() throws IOException {

		File f = createImageFile();
		mCurrentPhotoPath = f.getAbsolutePath();

		return f;
	}

	private void handleCameraPhoto() {
		if (mCurrentPhotoPath != null) {
			galleryAddPic();
			mCurrentPhotoPath = null;

			getActivity().getSupportLoaderManager().restartLoader(LOADER_ALL, null, mLoaderCallback);
			mCategoryText.setText(R.string.folder_all);
			mImageAdapter.setShowCamera(true);
		}
	}

	private void galleryAddPic() {
		Intent mediaScanIntent = new Intent("android.intent.action.MEDIA_SCANNER_SCAN_FILE");
		File f = new File(mCurrentPhotoPath);
		Uri contentUri = Uri.fromFile(f);
		mediaScanIntent.setData(contentUri);
		getActivity().sendBroadcast(mediaScanIntent);
	}

	/**
	 * 图片压缩
	 */
	private static final int HANDLE_COMPRESS_SINGLE = 0x00001;

	private int quality = 80;
	private Map<String, String> mCompressSuccessMap = new HashMap<String, String>();

	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case HANDLE_COMPRESS_SINGLE:
				Map<String, String> compressSuccessMap = (Map<String, String>) msg.obj;
				if (null != compressSuccessMap && compressSuccessMap.size() > 0) {// 说明有压缩成功的文件
					if (null != mCompressSuccessMap) {
						mCompressSuccessMap.putAll(compressSuccessMap);
					}
					if (null != mCallback) {
						mCallback.onCompressFinished(compressSuccessMap);
					}
				}
				break;
			}
		};
	};

	private void compress(final String targetCompressPicUrl) {
		if (TextUtils.isEmpty(targetCompressPicUrl)) {
			return;
		}
		// 判断原来是否已经压缩过
		if (mCompressSuccessMap.containsKey(targetCompressPicUrl)) {
			return;
		}
		File targetCompressFile = new File(targetCompressPicUrl);
		if (null == targetCompressFile || !targetCompressFile.exists()) {
			return;
		}

		long lenght = targetCompressFile.length();
		// 如果文件大小小于200kB的就不进行压缩
		if (lenght < compressPictureSizeAbove) {
			System.out.println("~~~~  图片小于" + FileUtils.getFormatSize(compressPictureSizeAbove) + ",不进行图片压缩");
			return;
		}
		Thread compressThread = new Thread(new Runnable() {

			@Override
			public void run() {
				Message message = handler.obtainMessage();
				message.what = HANDLE_COMPRESS_SINGLE;
				Map<String, String> currentCompressPicturePath = new HashMap<String, String>();
				// 设置压缩后的图片存储目录
				File outFile = FileUtils.createTmpFile(getActivity(), mImageCacheDir);
				String tmpfileAbsolutePath = "";
				if (null != outFile) {
					tmpfileAbsolutePath = outFile.getAbsolutePath();
					System.out.println("~~~~  开始压缩图片");
					try {
						boolean result = ImageCompress.compress(targetCompressPicUrl, tmpfileAbsolutePath, quality, targetCompressWidth, targetCompressHeight);
						if (result) {
							System.out.println("~~~~~~ 压缩后的图片大小=" + FileUtils.getFormatSize(tmpfileAbsolutePath.length()));
							currentCompressPicturePath.put(targetCompressPicUrl, tmpfileAbsolutePath);
						} else {
							System.out.println("~~~~~  图片压缩失败");
						}
					} catch (Exception e) {
						e.printStackTrace();
						System.out.println("~~~~~  图片压缩出现异常");
					}
				}
				message.obj = currentCompressPicturePath;
				handler.sendMessage(message);
			}
		});
		compressThread.start();
	}

	private void showCheckBoxView() {
		if (resultList.size() > 0) {
			misPhotoviewOrginImage.setEnabled(true);
			if (misPhotoviewOrginImage.isChecked()) {
				showImageSize(true);
			}
		} else {
			misPhotoviewOrginImage.setEnabled(false);
			misPhotoviewOrginImage.setChecked(false);
			misPhotoviewOrginImage.setText("原图");
		}
	}

	private void showImageSize(boolean show) {
		if (show) {
			long totalLenght = 0;
			for (String url : resultList) {
				File file = new File(url);
				if (file.exists()) {
					totalLenght += file.length();
				}
			}
			String afterFormat = FileUtils.getFormatSize(totalLenght);
			misPhotoviewOrginImage.setText("(" + afterFormat + ")原图");
		} else {
			misPhotoviewOrginImage.setText("原图");
		}
	}

}
