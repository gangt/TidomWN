/*
       Licensed to the Apache Software Foundation (ASF) under one
       or more contributor license agreements.  See the NOTICE file
       distributed with this work for additional information
       regarding copyright ownership.  The ASF licenses this file
       to you under the Apache License, Version 2.0 (the
       "License"); you may not use this file except in compliance
       with the License.  You may obtain a copy of the License at

         http://www.apache.org/licenses/LICENSE-2.0

       Unless required by applicable law or agreed to in writing,
       software distributed under the License is distributed on an
       "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
       KIND, either express or implied.  See the License for the
       specific language governing permissions and limitations
       under the License.
*/
package com.xiangpu.plugin;

import android.content.Context;

import com.baidu.location.BDLocation;
import com.xiangpu.location.LocationUtils;
import com.xiangpu.utils.LogUtil;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaArgs;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.PluginResult;
import org.json.JSONException;
import org.json.JSONObject;

public class LocationPlugin extends CordovaPlugin {

    static String TAG = "LocationPlugin";
    public CallbackContext mCallbackContext;

    private static final String ACTION_GET_LOCATION = "getLocation";

    public boolean execute(String action, CordovaArgs args, final CallbackContext callbackContext) throws JSONException {

        mCallbackContext = callbackContext;

        if (ACTION_GET_LOCATION.equals(action)) {
            getLocation(cordova.getActivity());
            return true;
        }
        return false;
    }

    private void getLocation(Context context) {
        LocationUtils.getInstance(context).getLocation(new LocationUtils.LocationCallback() {
            @Override
            public void onErrorCallback(JSONObject errorInfo) {
//                ToastUtil.showSingleToast(context, "定位失败");
                LogUtil.i(TAG, "定位失败");
                sendMessageToJs(errorInfo.toString());
            }

            @Override
            public void onSucceedCallback(BDLocation location) {
                JSONObject jsonObject = new JSONObject();
                double longitude = location.getLongitude(); //经度
                double latitude = location.getLatitude();   //纬度
                double altitude = location.getAltitude();//高度
                String province = location.getProvince();
                String city = location.getCity();
                String cityCode = location.getCityCode();
                String district = location.getDistrict();//地区名称
                String street = location.getStreet();
                String streetNumber = location.getStreetNumber();
                String buildingName = location.getBuildingName();
                String buildingID = location.getBuildingID();
                float derect = location.getDerect();//竖立的
                float direction = location.getDirection();//方向
                float speed = location.getSpeed();
                String addrStr = location.getAddrStr();
                LogUtil.i(TAG, "定位成功，longitude:" + longitude + "  latitude:" + latitude + "  addrStr:" + addrStr + "city:" + city + "cityCode:" + cityCode);
//                ToastUtil.showSingleToast(context, "定位成功");
                try {
                    jsonObject.put("code", "0");
                    jsonObject.put("msg", "定位成功");
                    jsonObject.put("longitude", longitude);
                    jsonObject.put("latitude", latitude);
                    jsonObject.put("altitude", altitude);
                    jsonObject.put("province", province);
                    jsonObject.put("city", city);
                    jsonObject.put("cityCode", cityCode);
                    jsonObject.put("district", district);
                    jsonObject.put("street", street);
                    jsonObject.put("streetNumber", streetNumber);
                    jsonObject.put("buildingName", buildingName);
                    jsonObject.put("buildingID", buildingID);
                    jsonObject.put("derect", derect);
                    jsonObject.put("direction", direction);
                    jsonObject.put("speed", speed);
                    jsonObject.put("addrStr", addrStr);
                    sendMessageToJs(jsonObject.toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, true);
    }

    private void sendMessageToJs(String message) {
        PluginResult dataResult = new PluginResult(PluginResult.Status.OK, message);
        dataResult.setKeepCallback(true);//非常重要
        mCallbackContext.sendPluginResult(dataResult);
    }

    //andi
    @Override
    public Boolean shouldAllowRequest(String url) {
        return true;
    }

    @Override
    public Boolean shouldAllowBridgeAccess(String url) {
        return true;
    }

}
