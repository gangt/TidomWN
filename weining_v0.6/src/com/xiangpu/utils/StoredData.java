package com.xiangpu.utils;

import android.app.Application;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Environment;
import android.text.TextUtils;

import java.io.File;

/***
 * 判断用户是否是第一次打开apk或者是升级更新
 * 
 * @author zxw
 *
 */
public class StoredData {

	public static final int LMODE_NEW_INSTALL = 1; // 启动-模式,首次安装-首次启动、覆盖安装-首次启动、已安装-二次启动
	public static final int LMODE_UPDATE = 2;
	public static final int LMODE_AGAIN = 3;
	private int launchMode = LMODE_AGAIN; // 启动-模式

	private static StoredData instance;

	public static StoredData getThis() {
		if (instance == null)
			instance = new StoredData();

		return instance;
	}

	// -------启动状态------------------------------------------------------------

	// 标记-打开app,用于产生-是否首次打开
	public void markOpenApp(Context context) {
		// 防止-重复调用

		String lastVersion = SharedPrefUtils.getStringData(context, "lastVersion", "");
		String thisVersion = getAppVersion(context);

		// 首次启动
		if (TextUtils.isEmpty(lastVersion)) {
			launchMode = LMODE_NEW_INSTALL;
			File appLoadDir = new File(Environment.getExternalStorageDirectory(),"/cargopull/");
			Utils.clearCache(appLoadDir);
			SharedPrefUtils.saveStringData(context, "lastVersion", thisVersion);
		}
		// 更新
		else if (!thisVersion.equals(lastVersion)) {
			launchMode = LMODE_UPDATE;
			SharedPrefUtils.saveStringData(context, "lastVersion", thisVersion);
		}
		// 二次启动(版本未变)
		else
			launchMode = LMODE_AGAIN;
	}

	public int getLaunchMode() {
		return launchMode;
	}

	// 首次打开,新安装、覆盖(版本号不同)
	public boolean isFirstOpen() {
		return launchMode != LMODE_AGAIN;
	}

	// -------------------------
	// 软件-版本
	public static String getAppVersion(Context context) {
		String versionName = "";
		Application app = (Application) context.getApplicationContext();
		try {
			PackageManager pkgMng = app.getPackageManager();
			PackageInfo pkgInfo = pkgMng.getPackageInfo(app.getPackageName(), 0);
			versionName = pkgInfo.versionName;
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		return versionName;
	}
}
