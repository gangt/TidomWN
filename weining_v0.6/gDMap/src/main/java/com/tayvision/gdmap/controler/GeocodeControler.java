package com.tayvision.gdmap.controler;

import android.content.Context;
import android.text.TextUtils;

import com.amap.api.services.core.AMapException;
import com.amap.api.services.geocoder.GeocodeAddress;
import com.amap.api.services.geocoder.GeocodeQuery;
import com.amap.api.services.geocoder.GeocodeResult;
import com.amap.api.services.geocoder.GeocodeSearch;
import com.amap.api.services.geocoder.RegeocodeResult;
import com.tayvision.gdmap.util.GDConstants;

/**
 * 地理编码控制类
 * Created by huangda on 2017/3/27.
 */

public class GeocodeControler implements GeocodeSearch.OnGeocodeSearchListener {
    private GeocodeSearch geocoderSearch;
    private GeocodeQuery geocodeQuery;

    private OnGeocodeSearchListener onGeocodeSearchListener;

    public void setOnGeocodeSearchListener(OnGeocodeSearchListener OnGeocodeSearchListener) {
        this.onGeocodeSearchListener = OnGeocodeSearchListener;
    }

    public GeocodeControler(Context context) {
        geocoderSearch = new GeocodeSearch(context);
        geocoderSearch.setOnGeocodeSearchListener(this);

    }

    /**
     * 根据地址名称和城市名称转换成坐标
     *
     * @param locationName 地址名称
     * @param city         城市的中文名或城市区号
     */
    public void getAddressFromLocationNameAsyn(String locationName, String city) {
        if (!TextUtils.isEmpty(locationName)) {
            geocodeQuery = new GeocodeQuery(locationName, city);
            geocoderSearch.getFromLocationNameAsyn(geocodeQuery);
        }
    }

    @Override
    public void onRegeocodeSearched(RegeocodeResult regeocodeResult, int i) {

    }

    @Override
    public void onGeocodeSearched(GeocodeResult result, int rCode) {
        if (onGeocodeSearchListener == null) {
            return;
        }
        if (rCode == AMapException.CODE_AMAP_SUCCESS) {
            if (result != null && result.getGeocodeAddressList() != null
                    && result.getGeocodeAddressList().size() > 0) {
                if (result.getGeocodeQuery().equals(geocodeQuery)) {
                    GeocodeAddress geocodeAddress = result.getGeocodeAddressList().get(0);
                    onGeocodeSearchListener.onGeocodeSearchSuccess(geocodeAddress, rCode);
                }
            } else {
                onGeocodeSearchListener.onGeocodeSearchFail(GDConstants.NO_RESULT);
            }
        } else {
            onGeocodeSearchListener.onGeocodeSearchFail(rCode);
        }
    }

    public interface OnGeocodeSearchListener {
        void onGeocodeSearchSuccess(GeocodeAddress geocodeAddress, int successCode);

        void onGeocodeSearchFail(int errorCode);
    }
}
