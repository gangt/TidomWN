package com.suneee.mis.utils;

import android.content.Context;
import android.os.Environment;
import android.text.TextUtils;

import java.io.File;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class FileUtils {

	private static final String IMAGE_PRE_FILENAME = "mis_";
	private static final String IMAGE_FORMAT = ".jpg";
	private static final String TIME_FORMAT = "yyyyMMddHHmmssSSS";

	public static File createTmpFile(Context context) {
		return createTmpFile(context, "");
	}

	public static File createTmpFile(Context context, String imageCacheDir) {
		String state = Environment.getExternalStorageState();
		if (state.equals(Environment.MEDIA_MOUNTED)) {
			if (checkDirExists(imageCacheDir)) {
				StringBuilder sBuilder = new StringBuilder(imageCacheDir);
				String timeStamp = new SimpleDateFormat(TIME_FORMAT, Locale.CHINA).format(new Date());
				sBuilder.append(IMAGE_PRE_FILENAME).append(timeStamp).append(IMAGE_FORMAT);
				File tmpFile = new File(sBuilder.toString());
				return tmpFile;
			} else {
				File pic = Environment.getExternalStorageDirectory();
				String timeStamp = new SimpleDateFormat(TIME_FORMAT, Locale.CHINA).format(new Date());
				String fileName = IMAGE_PRE_FILENAME + timeStamp + "";
				File tmpFile = new File(pic, fileName + IMAGE_FORMAT);
				return tmpFile;
			}
		} else {
			StringBuilder sBuilder = new StringBuilder(imageCacheDir);
			File cacheDir = context.getCacheDir();
			String timeStamp = new SimpleDateFormat(TIME_FORMAT, Locale.CHINA).format(new Date());
			sBuilder.append(IMAGE_PRE_FILENAME).append(timeStamp).append(IMAGE_FORMAT);
			File tmpFile = new File(cacheDir, sBuilder.toString());
			return tmpFile;
		}

	}

	private static boolean checkDirExists(String storageDirPath) {
		if (TextUtils.isEmpty(storageDirPath)) {
			return false;
		}
		File storeageDir = new File(storageDirPath);
		if (!storeageDir.exists()) {
			if (!storeageDir.mkdirs()) {
				return false;
			}
		}
		return true;
	}

	public static String getFormatSize(long size) {
		double kiloByte = size / 1024;
		if (kiloByte < 1) {
			return size + "Byte(s)";
		}

		double megaByte = kiloByte / 1024;
		if (megaByte < 1) {
			BigDecimal result1 = new BigDecimal(Double.toString(kiloByte));
			return result1.setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString() + "KB";
		}

		double gigaByte = megaByte / 1024;
		if (gigaByte < 1) {
			BigDecimal result2 = new BigDecimal(Double.toString(megaByte));
			return result2.setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString() + "MB";
		}

		double teraBytes = gigaByte / 1024;
		if (teraBytes < 1) {
			BigDecimal result3 = new BigDecimal(Double.toString(gigaByte));
			return result3.setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString() + "GB";
		}
		BigDecimal result4 = new BigDecimal(teraBytes);
		return result4.setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString() + "TB";
	}

}
