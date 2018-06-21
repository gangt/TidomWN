package com.xiangpu.location;

import android.content.Context;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.mapapi.SDKInitializer;
import com.xiangpu.utils.LogUtil;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * 定位功能
 */
public class LocationUtils {

    private String TAG = getClass().getSimpleName();
    private Context mContext = null;
    //定位服务配置
    private LocationService locationService;
    //单例
    private static LocationUtils mInstance;
    //定位回调
    private LocationCallback mCallback;
    //优先级标志
    private boolean mPriority = false;

    public static  LocationUtils getInstance(Context context){
        if (mInstance == null){
            synchronized (LocationUtils.class){
                if(mInstance == null){
                    mInstance = new LocationUtils(context);
                }
            }
        }
        return mInstance;
    }


    private LocationUtils(Context context) {
        mContext = context;
        initLocationService(context);
    }

    /**
     * 获取定位信息
     * @param callback
     */
    public void getLocation(LocationCallback callback,boolean priority) {
        if (!mPriority && priority){
            mPriority = priority;
            locationService.stop();
            locationService = null;

            initLocationService(mContext);
            startService(callback);
        }
    }

    private void initLocationService(Context context){
        locationService = new LocationService(context.getApplicationContext());
        SDKInitializer.initialize(context.getApplicationContext());
    }

    private void startService(LocationCallback callback){
        this.mCallback = callback;
        // -----------location config ------------
        //获取locationservice实例，建议应用中只初始化1个location实例
        locationService.registerListener(mListener);
        //注册监听
        locationService.setLocationOption(locationService.getDefaultLocationClientOption());
        // start之后会默认发起一次定位请求，开发者无须判断isstart并主动调用request
        locationService.start();// 定位SDK
    }

    //定位监听
    private BDLocationListener mListener = new BDLocationListener() {

        @Override
        public void onReceiveLocation(BDLocation location) {
            if (mPriority){
                mPriority =false;
            }
            LogUtil.i(TAG,"BDLocationListener-----------------");
            if (null != location && location.getLocType() != BDLocation.TypeServerError) {

                mCallback.onSucceedCallback(location);
            }else{
                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject.put("code","1");
                    jsonObject.put("msg","定位失败，请检测网络");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                LogUtil.i(TAG, "定位失败，请检测网络");
                mCallback.onErrorCallback(jsonObject);
            }
            stopLocation();
        }
    };

    /**
     * 停止服务
     */
    public void stopLocation() {
        LogUtil.i(TAG, "停止定位服务");
        locationService.unregisterListener(mListener); //注销掉监听
        locationService.stop(); //停止定位服务
    }

    /**
     * 定位回调
     */
    public interface LocationCallback {
        /**
         * 失败回调
         *
         * @param errorInfo
         */
         void onErrorCallback(JSONObject errorInfo);

        /**
         * 成功回调
         * @param location
         */
         void onSucceedCallback(BDLocation location);


    }
}
