package com.xiangpu.appversion;

import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class DownLoadManager {
    public static final int DOWNLOAD_PROGRESS = 100;

    // 文件存储
    private File updateDir = null;
    private File updateFile = null;

    private UpdataInfo version;

    private Handler handler;

    public DownLoadManager(UpdataInfo info, Handler handler) {
        version = info;
        this.handler = handler;

        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
            updateDir = new File(Environment.getExternalStorageDirectory(), "xiangpu/updata");
            updateFile = new File(updateDir.getPath(), version.getBinFile());
        }
    }

    public File getUpdataFile() {
        return updateFile;
    }

    /*
    * 从服务器获取版本
    */
    public File getFileFromServer() {
        try {
            if (!updateDir.exists()) {
                updateDir.mkdirs();
            }
            if (!updateFile.exists()) {
                updateFile.createNewFile();
            }
            long downloadSize = downloadUpdateFile(version.getUrl(), updateFile);
            if (downloadSize > 0) {
                return updateFile;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }

    /*
    * 执行下载文件的方法体
    */
    public long downloadUpdateFile(String downloadUrl, File saveFile)
            throws Exception {
        // 这样的下载代码很多，我就不做过多的说明
        int downloadCount = 0;
        int currentSize = 0;
        long totalSize = 0;
        int updateTotalSize = 0;
        HttpURLConnection httpConnection = null;
        InputStream is = null;
        FileOutputStream fos = null;

        try {
            URL url = new URL(downloadUrl);
            httpConnection = (HttpURLConnection) url.openConnection();

            //httpConnection.setRequestProperty("User-Agent", "PacificHttpClient");
            httpConnection.setRequestProperty("Accept-Encoding", "identity");
            if (currentSize > 0) {
                httpConnection.setRequestProperty("RANGE", "bytes=" + currentSize + "-");
            }
            httpConnection.setConnectTimeout(10000); // you  zhi
            httpConnection.setReadTimeout(20000);

            updateTotalSize = httpConnection.getContentLength(); // cuo
            Log.v("MyTag", "总大小:" + updateTotalSize);

            httpConnection.connect();
            is = httpConnection.getInputStream();
            fos = new FileOutputStream(saveFile, false);

            byte buffer[] = new byte[updateTotalSize];
            int readsize = 0;

            if (httpConnection.getResponseCode() == 404) {
                throw new Exception("fail!");
            }

            while ((readsize = is.read(buffer)) > 0) {
                fos.write(buffer, 0, readsize);
                totalSize += readsize;

                Message message = handler.obtainMessage();
                message.arg1 = (int) (totalSize * 100 / updateTotalSize);
                message.what = DOWNLOAD_PROGRESS;
                handler.sendMessage(message);
            }
        } finally {
            if (httpConnection != null) {
                httpConnection.disconnect();
            }
            if (is != null) {
                is.close();
            }
            if (fos != null) {
                fos.close();
            }
        }
        return totalSize;
    }

}
