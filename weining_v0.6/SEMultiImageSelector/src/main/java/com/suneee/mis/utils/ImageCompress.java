package com.suneee.mis.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory.Options;
import android.graphics.Matrix;
import android.media.ExifInterface;

import com.nostra13.universalimageloader.core.assist.ImageSize;
import com.nostra13.universalimageloader.core.assist.ViewScaleType;
import com.nostra13.universalimageloader.utils.L;

/**
 * 图片压缩处理类
 * 
 * @author Yafei.Chen
 * 
 */
public class ImageCompress {

	/**
	 * 按比例缩放 并保存文件
	 * 
	 * @param imageIs
	 * @param outFileName
	 * @param quality
	 * @param width
	 * @param height
	 */
	public static boolean compress(String compressPicUrl, String outFileName, int quality, int targetWidth, int targetHeight) {

		boolean result = true;
		InputStream imageStream = null;
		Bitmap decodedBitmap = null;
		ImageFileInfo imageFileInfo = null;

		try {
			System.out.println("~~~~~  进行第一步：获取到原始图片的大小， 旋转角度，是否翻转啦");
			// 步骤一： 获取到原始图片的大小， 旋转角度，是否翻转啦
			File file = new File(compressPicUrl);
			imageStream = new FileInputStream(compressPicUrl);
			System.out.println("~~~~~  原始图片大小=" + FileUtils.getFormatSize(file.length()));
			imageFileInfo = defineImageSizeAndRotation(imageStream, compressPicUrl);
			// 重置imageStream
			imageStream = resetStream(imageStream, compressPicUrl);

			System.out.println("~~~~~  进行第二步：裁切到我们目标大小 targetWidth=" + targetWidth + " , targetHeight=" + targetHeight);
			// 步骤二：裁切到我们目标大小
			ImageSize targetImageSize = new ImageSize(targetWidth, targetHeight);
			Options decodingOptions = prepareDecodingOptions(imageFileInfo.imageSize, targetImageSize);
			decodedBitmap = BitmapFactory.decodeStream(imageStream, null, decodingOptions);

		} catch (Exception e) {
			e.printStackTrace();
			if (null != imageStream) {
				try {
					imageStream.close();
				} catch (IOException e1) {
				}
			}
			result = false;
		}

		// 步骤三：进行旋转角度矫正，翻转矫正
		if (null != decodedBitmap) {
			System.out.println("~~~~~  进行第三步：进行旋转角度矫正，翻转矫正");
			decodedBitmap = considerExactScaleAndOrientatiton(decodedBitmap, imageFileInfo.exif.rotation, imageFileInfo.exif.flipHorizontal);
		} else {
			result = false;
		}

		// 步骤四：进行图片压缩
		if (null != decodedBitmap) {
			System.out.println("~~~~~  进行第四步：进行图片压缩");
			compress(decodedBitmap, outFileName, quality);
		} else {
			result = false;
		}
		return result;
	}

	private static InputStream resetStream(InputStream imageStream, String compressPicUrl) throws IOException {
		try {
			imageStream.reset();
		} catch (IOException e) {
			if (null != imageStream) {
				try {
					imageStream.close();
				} catch (IOException e1) {
				}
			}
			imageStream = new FileInputStream(compressPicUrl);
		}
		return imageStream;
	}

	private static Options prepareDecodingOptions(ImageSize imageSize, ImageSize targetSize) {
		// powerOf2,viewScaleType 目前这两个属性写死啦，可以考虑供外面配置
		boolean powerOf2 = false;
		ViewScaleType viewScaleType = ViewScaleType.FIT_INSIDE;
		int scale = computeImageSampleSize(imageSize, targetSize, viewScaleType, powerOf2);
		Options decodingOptions = new Options();
		decodingOptions.inSampleSize = scale;
		return decodingOptions;
	}

	private static int computeImageSampleSize(ImageSize srcSize, ImageSize targetSize, ViewScaleType viewScaleType, boolean powerOf2Scale) {
		final int srcWidth = srcSize.getWidth();
		final int srcHeight = srcSize.getHeight();
		final int targetWidth = targetSize.getWidth();
		final int targetHeight = targetSize.getHeight();

		int scale = 1;

		switch (viewScaleType) {
		case FIT_INSIDE:
			if (powerOf2Scale) {
				final int halfWidth = srcWidth / 2;
				final int halfHeight = srcHeight / 2;
				while ((halfWidth / scale) > targetWidth || (halfHeight / scale) > targetHeight) { // ||
					scale *= 2;
				}
			} else {
				scale = Math.max(srcWidth / targetWidth, srcHeight / targetHeight); // max
			}
			break;
		case CROP:
			if (powerOf2Scale) {
				final int halfWidth = srcWidth / 2;
				final int halfHeight = srcHeight / 2;
				while ((halfWidth / scale) > targetWidth && (halfHeight / scale) > targetHeight) { // &&
					scale *= 2;
				}
			} else {
				scale = Math.min(srcWidth / targetWidth, srcHeight / targetHeight); // min
			}
			break;
		}

		if (scale < 1) {
			scale = 1;
		}
		return scale;
	}

	private static Bitmap considerExactScaleAndOrientatiton(Bitmap subsampledBitmap, int rotation, boolean flipHorizontal) {
		Matrix m = new Matrix();
		// Flip bitmap if need
		if (flipHorizontal) {
			m.postScale(-1, 1);
		}
		// Rotate bitmap if need
		if (rotation != 0) {
			m.postRotate(rotation);
		}

		Bitmap finalBitmap = Bitmap.createBitmap(subsampledBitmap, 0, 0, subsampledBitmap.getWidth(), subsampledBitmap.getHeight(), m, true);
		if (finalBitmap != subsampledBitmap) {
			subsampledBitmap.recycle();
		}
		return finalBitmap;
	}

	private static ImageFileInfo defineImageSizeAndRotation(InputStream imageStream, String imageUrl) throws IOException {
		Options options = new Options();
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeStream(imageStream, null, options);

		ExifInfo exif;
		exif = defineExifOrientation(imageUrl);
		return new ImageFileInfo(new ImageSize(options.outWidth, options.outHeight, exif.rotation), exif);
	}

	private static void compress(Bitmap bitmap, String outFileName, int quality) {
		if (null == bitmap) {
			return;
		}
		try {
			File file = new File(outFileName);
			if (file.exists()) {
				file.createNewFile();
			}
			FileOutputStream out = new FileOutputStream(file);
			// 假如quality为30 表示压缩70%; 如果不压缩是100，表示压缩率为0
			System.out.println("~~~~~  压缩率=" + quality);
			bitmap.compress(CompressFormat.JPEG, quality, out);
			bitmap.recycle();// 释放资源
			out.flush();
			out.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	protected static ExifInfo defineExifOrientation(String imageUri) {
		int rotation = 0;
		boolean flip = false;
		try {
			ExifInterface exif = new ExifInterface(imageUri);
			int exifOrientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
			switch (exifOrientation) {
			case ExifInterface.ORIENTATION_FLIP_HORIZONTAL:
				flip = true;
			case ExifInterface.ORIENTATION_NORMAL:
				rotation = 0;
				break;
			case ExifInterface.ORIENTATION_TRANSVERSE:
				flip = true;
			case ExifInterface.ORIENTATION_ROTATE_90:
				rotation = 90;
				break;
			case ExifInterface.ORIENTATION_FLIP_VERTICAL:
				flip = true;
			case ExifInterface.ORIENTATION_ROTATE_180:
				rotation = 180;
				break;
			case ExifInterface.ORIENTATION_TRANSPOSE:
				flip = true;
			case ExifInterface.ORIENTATION_ROTATE_270:
				rotation = 270;
				break;
			}
		} catch (IOException e) {
			L.w("Can't read EXIF tags from file [%s]", imageUri);
		}
		System.out.println("~~~~~ 压缩前的图片旋转角度=" + rotation + " 是否翻转=" + flip);
		return new ExifInfo(rotation, flip);
	}

	protected static class ExifInfo {

		public final int rotation;
		public final boolean flipHorizontal;

		protected ExifInfo() {
			this.rotation = 0;
			this.flipHorizontal = false;
		}

		protected ExifInfo(int rotation, boolean flipHorizontal) {
			this.rotation = rotation;
			this.flipHorizontal = flipHorizontal;
		}
	}

	protected static class ImageFileInfo {

		public final ImageSize imageSize;
		public final ExifInfo exif;

		protected ImageFileInfo(ImageSize imageSize, ExifInfo exif) {
			this.imageSize = imageSize;
			this.exif = exif;
		}
	}

}
