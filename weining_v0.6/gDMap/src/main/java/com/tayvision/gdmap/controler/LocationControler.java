package com.tayvision.gdmap.controler;

import android.location.Location;
import android.os.Bundle;
import android.util.Log;

import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.MapView;
import com.amap.api.maps.model.MyLocationStyle;
import com.amap.api.services.core.AMapException;
import com.tayvision.gdmap.util.GDConstants;

/**
 * Created by huangda on 2017/3/27.
 */

public class LocationControler implements AMap.OnMyLocationChangeListener {
    private AMap aMap;
    private OnLocationListener onLocationListener;

    public void setOnLocationListener(OnLocationListener onLocationListener) {
        this.onLocationListener = onLocationListener;
    }

    /**
     * 定位
     *
     * @param mapView         地图控件
     * @param myLocationStyle 定位样式
     * @param zoomLevel       地图显示的缩放级别
     */
    public void location(MapView mapView, MyLocationStyle myLocationStyle, String zoomLevel) {
        if (aMap == null) {
            aMap = mapView.getMap();
        }
        aMap.getUiSettings().setMyLocationButtonEnabled(true);// 设置默认定位按钮是否显示
        aMap.setMyLocationEnabled(true);// 设置为true表示显示定位层并可触发定位，false表示隐藏定位层并不可触发定位，默认是false
        aMap.setMyLocationStyle(myLocationStyle);
        aMap.moveCamera(CameraUpdateFactory.zoomTo(Float.valueOf(zoomLevel)));
        //设置SDK 自带定位消息监听
        aMap.setOnMyLocationChangeListener(this);
    }

    @Override
    public void onMyLocationChange(Location location) {
        // 定位回调监听
        if (location != null) {
            if (onLocationListener!=null){
                onLocationListener.onLocationSuccess(location, AMapException.CODE_AMAP_SUCCESS);
            }
//            Log.e("amap", "onMyLocationChange 定位成功， lat: " + location.getLatitude() + " lon: " + location.getLongitude());
            Bundle bundle = location.getExtras();
            if (bundle != null) {
                int errorCode = bundle.getInt(MyLocationStyle.ERROR_CODE);
                String errorInfo = bundle.getString(MyLocationStyle.ERROR_INFO);
                // 定位类型，可能为GPS WIFI等，具体可以参考官网的定位SDK介绍
                int locationType = bundle.getInt(MyLocationStyle.LOCATION_TYPE);

                /*
                errorCode
                errorInfo
                locationType
                */
                Log.e("amap", "定位信息， code: " + errorCode + " errorInfo: " + errorInfo + " locationType: " + locationType);
            } else {
                Log.e("amap", "定位信息， bundle is null ");

            }

        } else {
            if (onLocationListener!=null){
                onLocationListener.onLocationFail(GDConstants.LOCATION_FAIL);
            }
            Log.e("amap", "定位失败");
        }
    }

    public interface OnLocationListener {
        void onLocationSuccess(Location location, int successCode);

        void onLocationFail(int errorCode);
    }
}
