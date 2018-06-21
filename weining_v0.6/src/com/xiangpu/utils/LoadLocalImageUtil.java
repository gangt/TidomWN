package com.xiangpu.utils;

import android.widget.ImageView;

import com.konecty.rocket.chat.R;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.lang.reflect.Field;

/**
 * description: 加载本地图片
 * autour: Andy
 * date: 2017/12/5 16:02
 * update: 2017/12/5
 * version: 1.0
 */
public class LoadLocalImageUtil {

    private static final String TAG = LoadLocalImageUtil.class.getSimpleName();

    private LoadLocalImageUtil() {
    }

    private static LoadLocalImageUtil instance = null;

    public static synchronized LoadLocalImageUtil getInstance() {
        if (instance == null) {
            instance = new LoadLocalImageUtil();
        }
        return instance;
    }

    /**
     * 从内存卡中异步加载本地图片
     *
     * @param uri
     * @param imageView
     */
    public void displayFromSDCard(String uri, ImageView imageView) {
        // String imageUri = "file:///mnt/sdcard/image.png"; // from SD card
        ImageLoader.getInstance().displayImage("file://" + uri, imageView);
    }

    /**
     * 从assets文件夹中异步加载图片
     *
     * @param imageName 图片名称，带后缀的，例如：1.png
     * @param imageView
     */
    public void dispalyFromAssets(String imageName, ImageView imageView) {
        // String imageUri = "assets://image.png"; // from assets
        ImageLoader.getInstance().displayImage("assets://" + imageName, imageView);
    }

    /**
     * 从drawable中异步加载本地图片
     *
     * @param imageId
     * @param imageView
     */
    public void displayFromDrawable(int imageId, ImageView imageView) {
        // String imageUri = "drawable://" + R.drawable.image; // from drawables
        // (only images, non-9patch)
        ImageLoader.getInstance().displayImage("drawable://" + imageId,
                imageView);
    }

    /**
     * 从drawable中异步加载本地图片
     *
     * @param imageName 图片名称
     * @param imageView
     */
    public void displayFromDrawable(String imageName, ImageView imageView) {
        int id = getResIdByImageName(imageName);
        if (id == 0) {
            ImageLoader.getInstance().displayImage("drawable://" + R.drawable.icon_weining,
                    imageView);
        } else {
            ImageLoader.getInstance().displayImage("drawable://" + id, imageView);
        }
    }

    /**
     * 从内容提提供者中抓取图片
     */
    public void displayFromContent(String uri, ImageView imageView) {
        // String imageUri = "content://media/external/audio/albumart/13"; //
        // from content provider
        ImageLoader.getInstance().displayImage("content://" + uri, imageView);
    }

    /**
     * 根据图片名称获取资源ID
     *
     * @param imageName
     * @return
     */
    public int getResIdByImageName(String imageName) {
        Class drawable = R.drawable.class;
        Field field;
        int res_ID;
        try {
            field = drawable.getField(imageName);
            if (field != null) {
                res_ID = field.getInt(field.getName());
                return res_ID;
            }
        } catch (Exception e) {
            LogUtil.e(TAG, "七星轴公司图片获取失败");
            e.printStackTrace();
        }
        return 0;
    }

}
