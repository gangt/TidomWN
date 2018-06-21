package com.xiangpu.appversion;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;

import com.lssl.activity.SuneeeApplication;
import com.xiangpu.dialog.CommonDialog;
import com.xiangpu.dialog.DownloadProgressDialog;
import com.xiangpu.utils.NetWorkUtils;
import com.xiangpu.utils.ToastUtils;

import java.io.File;

public class UpdataAppVersion implements Runnable {

    public final int DOWN_ERROR = -1;
    public final int UPDATA_CLIENT = 200;
    public final int GET_UNDATAINFO_ERROR = 400;

    public UpdataInfo info = new UpdataInfo();

    public Activity activity;
    private DownloadProgressDialog progressDialog;
    public DownLoadManager down;

    public UpdataFileToDevice updataFileToDeviceInteface = null;

    public interface UpdataFileToDevice {
        // 下发文件到设备
        void updataFileToDeviceResult(Boolean bValue, String name);
    }

    public UpdataAppVersion(Activity act, String url, String version, String description, String binFile) {
        info.setUrl(url);
        info.setVersion(version);
        info.setDescription(description);
        info.setBinFile(binFile);
        activity = act;
    }

    public void run() {
        try {
            Message msg = new Message();
            msg.what = UPDATA_CLIENT;
            handler.sendMessage(msg);

        } catch (Exception e) {
            // 待处理
            Message msg = new Message();
            msg.what = GET_UNDATAINFO_ERROR;
            handler.sendMessage(msg);
            e.printStackTrace();
        }
    }

    public Handler handler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case UPDATA_CLIENT:
                    showUpdataDialog();
                    break;

                case GET_UNDATAINFO_ERROR:
                    //服务器超时
                    ToastUtils.showCenterToast(SuneeeApplication.getInstance().getApplicationContext(), "获取服务器更新信息失败");
                    break;

                case DOWN_ERROR:
                    ToastUtils.showCenterToast(SuneeeApplication.getInstance().getApplicationContext(), "下载新版本失败");
                    if (updataFileToDeviceInteface != null) {
                        updataFileToDeviceInteface.updataFileToDeviceResult(false, "");
                    }
                    break;

                case 5:
                    progressDialog.dismiss(); //结束掉进度条对话框
                    installApk(down.getUpdataFile());
                    break;

                case DownLoadManager.DOWNLOAD_PROGRESS:
                    // 更新进度条
                    int progress = msg.arg1;
                    progressDialog.setProgress(progress);
                    break;

            }
        }
    };

    private void showUpdataDialog() {
        CommonDialog dialog = new CommonDialog(activity);
        dialog.show();
        dialog.setTvContent(info.getDescription().replace("\\n", "\n"));
        dialog.setOnResultListener(new CommonDialog.OnResultListener() {
            @Override
            public void onConfirm() {
                int netType = NetWorkUtils.getAPNType(activity);
                if (netType == 0) {
                    ToastUtils.showCenterToast(activity, "请先连接网络");
                    return;
                }
                if (netType != 1) {
                    // 非wifi网络情况
                    CommonDialog netDialog = new CommonDialog(activity);
                    netDialog.show();
                    netDialog.setTvTitle("温馨提示");
                    netDialog.setTvContent("你当前处于非WiFi环境下，继续下载将会产生手机流量，确定继续？");
                    netDialog.setOnResultListener(new CommonDialog.OnResultListener() {
                        @Override
                        public void onConfirm() {
                            downLoadApk();
                        }

                        @Override
                        public void onCancel() {

                        }
                    });
                    return;
                }
                downLoadApk();
            }

            @Override
            public void onCancel() {

            }
        });
    }

    /**
     * 从服务器中下载APK
     */
    protected void downLoadApk() {
        progressDialog = new DownloadProgressDialog(activity);
        progressDialog.show();
        progressDialog.setCanceledOnTouchOutside(false);

        down = new DownLoadManager(info, handler);
        new Thread() {
            @Override
            public void run() {
                try {
                    File file = down.getFileFromServer();
                    if (file != null) {
                        handler.sendEmptyMessage(5);
                    } else {
                        Message msg = new Message();
                        msg.what = DOWN_ERROR;
                        handler.sendMessage(msg);
                    }
                } catch (Exception e) {
                    Message msg = new Message();
                    msg.what = DOWN_ERROR;
                    handler.sendMessage(msg);
                    e.printStackTrace();
                }
            }
        }.start();
    }

    // 安装apk
    private void installApk(File file) {
        Intent intent = new Intent();
        // 执行动作
        intent.setAction(Intent.ACTION_VIEW);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        // 执行的数据类型
        intent.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");
        SuneeeApplication.getInstance().getApplicationContext().startActivity(intent);
    }

}
