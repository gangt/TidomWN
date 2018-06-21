/*
    Suneee Android Client, QRCodeDialog
    Copyright (c) 2015 Suneee Tech Company Limited
 */

package com.suneee.demo.scan.view;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager.LayoutParams;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import com.suneee.qrcode.R;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

/**
 * [二维码对话框显示用户账号的二维码]
 * 
 * @author galaxy_xiong
 * @version 1.0
 * @date 2015年8月27日
 * 
 **/
/**
 * @author galaxy_xiong
 * 
 */
public class QRCodeDialog extends Dialog implements
		android.view.View.OnClickListener {

	public static final String WEILIANFFRIENDTAG = "jid:";
	public static final String WEILIANGROUPTAG = "gid:";
	public static final String WEILIANDISCUSSTIONTAG = "did:";
	public static final String WEILIANEND = "@suneeedev";

	private ImageView qrImg;

	private ImageView downloadQrImg;

	private TextView qrTitleTv;
	private TextView qrInfoTv;

	private Context mContext;

	private Bitmap startBitmap, endBitmap;

	private static final int IMAGE_HALFWIDTH = 80;
	private int[] pixels = new int[2 * IMAGE_HALFWIDTH * 2 * IMAGE_HALFWIDTH];

	public static final String ROOT = "SEQRCODE";
	// 默认存放图片的路径
	public final static String DEFAULT_SAVE_IMAGE_PATH = Environment.getExternalStorageDirectory() + File.separator + ROOT + File.separator + "Images"
			+ File.separator;

	private String qrcodeFile = DEFAULT_SAVE_IMAGE_PATH + "Cache/Image/";

	private String fileName = ".jpg";
	private Handler mHandler = new Handler();

	private int width = 0;
	private int height = 0;
	private String code;
	private String qrTitle;
	private String qrInfo;
	private int type;

	/**
	 * @param context
	 */
	public QRCodeDialog(Context context) {
		super(context);
	}

	public QRCodeDialog(Context context, String code) {
		this(context);
		mContext = context;
		this.code = WEILIANFFRIENDTAG + code + WEILIANEND;
		this.fileName = this.code + this.fileName;
		this.type = 1;
		this.qrTitle = "我的二维码";
		this.qrInfo = "扫一扫上面的二维码，加我为好友";

	}

	/**
	 * @param context
	 * @param code
	 *            jid
	 * @param qrCodeTitle
	 *            对话框标题 如的我的二维码
	 * @param qrCodeInfo
	 *            对话框 信息 茹扫一扫加我为好友
	 * @param type
	 *            1 好友2群3讨论组
	 */
	public QRCodeDialog(Context context, String code, String qrCodeTitle,
			String qrCodeInfo, int type) {
		this(context);
		this.mContext = context;
		this.code = code;
		this.qrTitle = qrCodeTitle;
		this.qrInfo = qrCodeInfo;
		this.type = type;
		initData();

	}

	private void initData() {
		if (this.type == 1) {
			this.code = WEILIANFFRIENDTAG + this.code + "@suneeedev";

		} else if (this.type == 2) {
			this.code = WEILIANGROUPTAG + this.code;

		} else if (this.type == 3) {
			this.code = WEILIANDISCUSSTIONTAG + this.code;
		}
		this.fileName = this.code + this.fileName;

	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.qrcode_dialog_layout);
		initWindowParams();
		initView();
	}

	private void initWindowParams() {
		Window window = getWindow();
		window.setBackgroundDrawableResource(android.R.color.transparent);
		window.setGravity(Gravity.CENTER);
		window.setLayout(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
	}

	private void initView() {
		this.qrTitleTv = (TextView) this.findViewById(R.id.qr_title);
		this.qrInfoTv = (TextView) this.findViewById(R.id.qr_info);
		this.qrTitleTv.setText(qrTitle);
		this.qrInfoTv.setText(qrInfo);
		this.qrImg = (ImageView) this.findViewById(R.id.qr_code_img);
		this.downloadQrImg = (ImageView) this
				.findViewById(R.id.download_qr_img);
		this.qrImg.setOnClickListener(this);
		this.downloadQrImg.setOnClickListener(this);
		qrImg.post(new Runnable() {

			@Override
			public void run() {
				width = qrImg.getWidth();
				height = qrImg.getHeight();
				if (!TextUtils.isEmpty(code)) {
					buildCode(code);
				}
			}

		});
	}

	@Override
	public void onClick(View v) {
		int vid = v.getId();
		if(vid == R.id.qr_code_img){
			this.dismiss();
		}else if(vid == R.id.download_qr_img){
			File qrcodeFileDir = new File(qrcodeFile);
			if (!qrcodeFileDir.exists()) {
				try {
					qrcodeFileDir.mkdirs();
				} catch (Exception e) {
				}
			}
			File qrCodeImgFile = new File(qrcodeFile + fileName);

			if (qrCodeImgFile.exists()) {
				Toast.makeText(mContext, "已下载！", Toast.LENGTH_SHORT).show();
			} else {

				if (null != endBitmap) {
					mHandler.post(new Runnable() {

						@Override
						public void run() {
							saveMyBitmap(endBitmap, "qrcode");
						}

					});
				}

			}
		}
	}

	public Bitmap cretaeBitmap(String str) throws WriterException {
		Map<EncodeHintType, Object> hints = new HashMap<EncodeHintType, Object>();
		hints.put(EncodeHintType.CHARACTER_SET, "utf-8");
		// 容错级别
		hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H);
		// 设置空白边距的宽度
		hints.put(EncodeHintType.MARGIN, 0); // default is 4
		// BitMatrix matrix = new MultiFormatWriter().encode(str,
		// BarcodeFormat.QR_CODE, 1200, 1200);
		BitMatrix matrix = new MultiFormatWriter().encode(str,
				BarcodeFormat.QR_CODE, 1200, 1200, hints);
		int width = matrix.getWidth();
		int height = matrix.getHeight();
		int halfW = width / 2;
		int halfH = height / 2;
		int[] pixels = new int[width * height];
		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {

				// if (x > halfW - IMAGE_HALFWIDTH && x < halfW +
				// IMAGE_HALFWIDTH
				// && y > halfH - IMAGE_HALFWIDTH
				// && y < halfH + IMAGE_HALFWIDTH) {
				// pixels[y * width + x] = startBitmap.getPixel(x - halfW
				// + IMAGE_HALFWIDTH, y - halfH + IMAGE_HALFWIDTH);
				// } else {
				pixels[y * width + x] = matrix.get(x, y) ? 0xff000000
						: 0xffffffff;
				// }

			}
		}
		Bitmap bitmap = Bitmap.createBitmap(width, height,
				Bitmap.Config.ARGB_8888);
		bitmap.setPixels(pixels, 0, width, 0, 0, width, height);
		return bitmap;
	}

	protected void buildCode(String s) {
		try {
			File qrcodeFileTemp = new File(qrcodeFile + fileName);
			if (qrcodeFileTemp.exists()) {
				endBitmap = BitmapFactory.decodeFile(qrcodeFile + fileName);
				if (null != endBitmap) {
					qrImg.setImageBitmap(endBitmap);
				}
			} else {
				startBitmap = ((BitmapDrawable) mContext.getResources()
						.getDrawable(R.drawable.app_icon)).getBitmap();
				Matrix m = new Matrix();
				float sx = (float) 2 * IMAGE_HALFWIDTH / startBitmap.getWidth();
				float sy = (float) 2 * IMAGE_HALFWIDTH / startBitmap.getHeight();
				m.setScale(sx, sy);
				startBitmap = Bitmap.createBitmap(startBitmap, 0, 0,
						startBitmap.getWidth(), startBitmap.getHeight(), m, false);

				endBitmap = cretaeBitmap(new String(s.getBytes(), "ISO-8859-1"));
				qrImg.setImageBitmap(endBitmap);
			}
		} catch (WriterException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}catch (OutOfMemoryError error){
			error.printStackTrace();
		}
	}

	public void saveMyBitmap(Bitmap bitmap, String bitName) {
		File f = new File(qrcodeFile + fileName);
		if (!f.exists()) {
			try {
				f.createNewFile();
			} catch (Exception e) {
			}
		}
		try {
			BufferedOutputStream bos = new BufferedOutputStream(
					new FileOutputStream(f));
			bitmap.compress(Bitmap.CompressFormat.JPEG, 80, bos);
			bos.flush();
			bos.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		// // 其次把文件插入到系统图库
		// try {
		// MediaStore.Images.Media.insertImage(
		// this.mContext.getContentResolver(), f.getAbsolutePath(),
		// fileName, null);
		// } catch (FileNotFoundException e) {
		// e.printStackTrace();
		// }
		// 最后通知图库更新
		mContext.sendBroadcast(new Intent(
				Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.parse("file://"
						+ f.getAbsolutePath())));
		Toast.makeText(mContext, "下载完成!", Toast.LENGTH_SHORT).show();
	}

}
