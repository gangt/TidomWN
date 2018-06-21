package com.xiangpu.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;

import com.xiangpu.utils.NetWorkUtils;

/**
 * description: 自定义检查手机网络状态是否切换的广播接受器
 * autour: Andy
 * date: 2017/12/4 17:05
 * update: 2017/12/4
 * version: 1.0
 */
public class NetBroadcastReceiver extends BroadcastReceiver {

    private ActionListener listener;

    public void setListener(ActionListener listener) {
        this.listener = listener;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        // 如果相等的话就说明网络状态发生了变化
        if (ConnectivityManager.CONNECTIVITY_ACTION.equals(intent.getAction())) {
            boolean netWorkState = NetWorkUtils.isNetworkConnected(context);
            // 接口回调传过去状态的类型
            if (listener != null) {
                listener.receiveResult(netWorkState);
            }
        }
    }

    public interface ActionListener {
        void receiveResult(boolean isNetworkOk);
    }
}
